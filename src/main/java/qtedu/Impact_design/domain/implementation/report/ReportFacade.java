package qtedu.Impact_design.domain.implementation.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.api.dto.response.report.TeamCanvasResponse;
import qtedu.Impact_design.domain.implementation.flowcanvas.FlowCanvasReader;
import qtedu.Impact_design.domain.implementation.game.GameReader;
import qtedu.Impact_design.domain.implementation.identitycanvas.IdentityCanvasReader;
import qtedu.Impact_design.domain.implementation.teach.TeachTeamReader;
import qtedu.Impact_design.domain.implementation.wincanvas.WinCanvasReader;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReportFacade {

    private final ReportDataAggregator reportDataAggregator;
    private final ReportAnalyzer reportAnalyzer;
    private final WinCanvasScoreCalculator winCanvasScoreCalculator;
    private final GameReader gameReader;
    private final TeachTeamReader teachTeamReader;
    private final IdentityCanvasReader identityCanvasReader;
    private final FlowCanvasReader flowCanvasReader;
    private final WinCanvasReader winCanvasReader;
    private final TeamUserRepository teamUserRepository;
    private final UserinfoRepository userinfoRepository;
    private final ExecutorService ioExecutor;

    public ReportFacade(
            ReportDataAggregator reportDataAggregator,
            ReportAnalyzer reportAnalyzer,
            WinCanvasScoreCalculator winCanvasScoreCalculator,
            GameReader gameReader,
            TeachTeamReader teachTeamReader,
            IdentityCanvasReader identityCanvasReader,
            FlowCanvasReader flowCanvasReader,
            WinCanvasReader winCanvasReader,
            TeamUserRepository teamUserRepository,
            UserinfoRepository userinfoRepository,
            @Qualifier("ioExecutor") ExecutorService ioExecutor
    ) {
        this.reportDataAggregator = reportDataAggregator;
        this.reportAnalyzer = reportAnalyzer;
        this.winCanvasScoreCalculator = winCanvasScoreCalculator;
        this.gameReader = gameReader;
        this.teachTeamReader = teachTeamReader;
        this.identityCanvasReader = identityCanvasReader;
        this.flowCanvasReader = flowCanvasReader;
        this.winCanvasReader = winCanvasReader;
        this.teamUserRepository = teamUserRepository;
        this.userinfoRepository = userinfoRepository;
        this.ioExecutor = ioExecutor;
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(Integer teamId) {
        ReportRawData raw = reportDataAggregator.load(teamId);

        // AI 분석, 점수 계산, 메타데이터 조회를 병렬 실행
        CompletableFuture<ReportAiResult> aiResultFuture =
                CompletableFuture.supplyAsync(() -> reportAnalyzer.analyze(raw), ioExecutor);
        CompletableFuture<ReportScoreResult> scoreResultFuture =
                CompletableFuture.supplyAsync(() -> winCanvasScoreCalculator.calculate(raw), ioExecutor);
        CompletableFuture<Optional<TbGameModel>> gameFuture =
                CompletableFuture.supplyAsync(() -> gameReader.findByTeamId(teamId), ioExecutor);
        CompletableFuture<Optional<TbTeamModel>> teamFuture =
                CompletableFuture.supplyAsync(() -> teachTeamReader.findByTeamId(teamId), ioExecutor);

        CompletableFuture.allOf(aiResultFuture, scoreResultFuture, gameFuture, teamFuture).join();

        ReportAiResult aiResult = aiResultFuture.join();
        ReportScoreResult scoreResult = scoreResultFuture.join();
        Optional<TbGameModel> gameOpt = gameFuture.join();
        Optional<TbTeamModel> teamOpt = teamFuture.join();

        return ReportResponse.builder()
                .teamId(teamId)
                .teamName(teamOpt.map(TbTeamModel::getName).orElse(null))
                .className(gameOpt.map(TbGameModel::getName).orElse(null))
                .target(gameOpt.map(TbGameModel::getTarget).orElse(null))
                .projectDate(gameOpt.map(TbGameModel::getProjectDate).orElse(null))
                .imageUrl(gameOpt.map(g -> gameReader.resolveImageUrl(g.getImageUrl())).orElse(null))
                .impactCheckScores(scoreResult.getImpactCheckScores())
                .externalThreats(aiResult.getExternalThreats())
                .internalLimitations(aiResult.getInternalLimitations())
                .visionMissionValue(aiResult.getVisionMissionValue())
                .flowCanvasGoals(aiResult.getFlowCanvasGoals())
                .tacticals(aiResult.getTacticals())
                .strategicActivities(aiResult.getStrategicActivities())
                .quickWinCanvasList(scoreResult.getQuickWinCanvasList())
                .buildWinCanvasList(scoreResult.getBuildWinCanvasList())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TeamCanvasResponse> getTeamCanvases(Integer gameId) {
        List<Integer> teamIds = gameReader.findTeamIdsByGameId(gameId);
        Map<Integer, TbTeamModel> teamMap = teachTeamReader.findByTeamIds(teamIds).stream()
                .collect(Collectors.toMap(TbTeamModel::getTeamId, Function.identity()));
        String imageUrl = gameReader.findById(gameId)
                .map(g -> gameReader.resolveImageUrl(g.getImageUrl()))
                .orElse(null);

        // 각 팀의 대표작성자 찾기
        Map<Integer, Long> writerByTeam = findWriterByTeam(teamIds);

        // 팀별 캔버스 조회를 병렬 실행
        List<CompletableFuture<TeamCanvasResponse>> futures = teamIds.stream()
                .filter(writerByTeam::containsKey)
                .map(teamId -> CompletableFuture.supplyAsync(() -> {
                    Long writerId = writerByTeam.get(teamId);

                    List<IdentityCanvasModel> identities = identityCanvasReader.readByUserIds(List.of(writerId));

                    return TeamCanvasResponse.builder()
                            .teamId(teamId)
                            .teamName(Optional.ofNullable(teamMap.get(teamId)).map(TbTeamModel::getName).orElse(null))
                            .writerUserId(writerId)
                            .imageUrl(imageUrl)
                            .identityCanvas(identities.isEmpty() ? null : IdentityCanvasResponse.from(identities.get(0)))
                            .flowCanvas(flowCanvasReader.read(writerId))
                            .quickWinCanvas(winCanvasReader.read(writerId, CanvasType.QUICK))
                            .buildWinCanvas(winCanvasReader.read(writerId, CanvasType.BUILD))
                            .build();
                }, ioExecutor))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private Map<Integer, Long> findWriterByTeam(List<Integer> teamIds) {
        List<TeamUserModel> allTeamUsers = teamUserRepository.findByTeamIdIn(teamIds);
        Map<Integer, List<Long>> teamUserMap = allTeamUsers.stream()
                .collect(Collectors.groupingBy(
                        TeamUserModel::getTeamId,
                        Collectors.mapping(TeamUserModel::getUserId, Collectors.toList())
                ));

        Map<Integer, Long> writerByTeam = new LinkedHashMap<>();
        for (Integer teamId : teamIds) {
            List<Long> memberIds = teamUserMap.getOrDefault(teamId, Collections.emptyList());
            if (memberIds.isEmpty()) continue;

            Long writerId = userinfoRepository.findWriterByUserIds(memberIds)
                    .map(u -> u.getUserId())
                    .orElse(memberIds.get(0));
            writerByTeam.put(teamId, writerId);
        }
        return writerByTeam;
    }

}
