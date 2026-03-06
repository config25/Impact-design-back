package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.api.dto.response.funding.FundingInvestmentResponse;
import qtedu.Impact_design.api.dto.response.funding.FundingMyResultResponse;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.api.dto.response.teach.TeamSubmissionListResponse;
import qtedu.Impact_design.api.dto.response.wincanvas.WinCanvasResponse;
import qtedu.Impact_design.domain.implementation.flowcanvas.FlowCanvasReader;
import qtedu.Impact_design.domain.implementation.funding.FundingReader;
import qtedu.Impact_design.domain.implementation.identitycanvas.IdentityCanvasReader;
import qtedu.Impact_design.domain.implementation.impactcheck.ImpactCheckReader;
import qtedu.Impact_design.domain.implementation.wincanvas.WinCanvasReader;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeachSubmissionReader {

    private final GameTeamRepository gameTeamRepository;
    private final TbTeamRepository tbTeamRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamSubmitStatusChecker submitStatusChecker;

    // 기존 Reader들 재사용
    private final ImpactCheckReader impactCheckReader;
    private final IdentityCanvasReader identityCanvasReader;
    private final FlowCanvasReader flowCanvasReader;
    private final WinCanvasReader winCanvasReader;
    private final FundingReader fundingReader;

    public List<TeamSubmissionListResponse> getSubmissionList(Integer gameId) {
        List<Integer> teamIds = gameTeamRepository.findTeamIdsByGameId(gameId);
        if (teamIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, qtedu.Impact_design.domain.model.team.TbTeamModel> teamMap =
                tbTeamRepository.findByTeamIdIn(teamIds).stream()
                        .collect(Collectors.toMap(
                                qtedu.Impact_design.domain.model.team.TbTeamModel::getTeamId,
                                Function.identity()));

        List<qtedu.Impact_design.domain.model.team.TeamUserModel> allTeamUsers =
                teamUserRepository.findByTeamIdIn(teamIds);
        Map<Integer, List<Long>> teamUserIdsMap = allTeamUsers.stream()
                .collect(Collectors.groupingBy(
                        qtedu.Impact_design.domain.model.team.TeamUserModel::getTeamId,
                        Collectors.mapping(
                                qtedu.Impact_design.domain.model.team.TeamUserModel::getUserId,
                                Collectors.toList())));

        Map<Integer, TeamSubmitStatusChecker.TeamSubmitResult> submitMap =
                submitStatusChecker.checkAllSubmitStatuses(teamUserIdsMap);

        return teamIds.stream()
                .map(teamMap::get)
                .filter(team -> team != null && (team.getStatus() == null || team.getStatus() != -1))
                .map(team -> {
                    int numUser = teamUserIdsMap.getOrDefault(team.getTeamId(), Collections.emptyList()).size();
                    TeamSubmitStatusChecker.TeamSubmitResult submit =
                            submitMap.getOrDefault(team.getTeamId(), TeamSubmitStatusChecker.TeamSubmitResult.allNotSubmitted());

                    return TeamSubmissionListResponse.builder()
                            .teamId(team.getTeamId())
                            .teamName(team.getName())
                            .sequence(team.getSequence())
                            .numUser(numUser)
                            .submitA(submit.getSubmitA())
                            .submitB(submit.getSubmitB())
                            .submitC(submit.getSubmitC())
                            .submitD(submit.getSubmitD())
                            .submitE(submit.getSubmitE())
                            .submitF(submit.getSubmitF())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public ImpactCheckResponse getImpactCheck(Integer teamId) {
        Long writerUserId = submitStatusChecker.findWriterUserId(teamId);
        return impactCheckReader.read(writerUserId);
    }

    public IdentityCanvasResponse getIdentityCanvas(Integer teamId) {
        Long writerUserId = submitStatusChecker.findWriterUserId(teamId);
        return identityCanvasReader.read(writerUserId);
    }

    public FlowCanvasResponse getFlowCanvas(Integer teamId) {
        Long writerUserId = submitStatusChecker.findWriterUserId(teamId);
        return flowCanvasReader.read(writerUserId);
    }

    public WinCanvasResponse getWinCanvas(Integer teamId, CanvasType canvasType) {
        Long writerUserId = submitStatusChecker.findWriterUserId(teamId);
        return winCanvasReader.read(writerUserId, canvasType);
    }

    public List<FundingInvestmentResponse> getFunding(String canvasType, Integer teamId) {
        Long writerUserId = submitStatusChecker.findWriterUserId(teamId);
        return fundingReader.readByUserId(canvasType, writerUserId);
    }

    public FundingMyResultResponse getFundingResult(Integer teamId) {
        return fundingReader.getResultByTeamId(teamId);
    }
}
