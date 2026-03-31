package qtedu.Impact_design.domain.implementation.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.model.ImpactCheckModel;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;
import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;
import qtedu.Impact_design.domain.implementation.flowcanvas.FlowCanvasReader;
import qtedu.Impact_design.domain.implementation.identitycanvas.IdentityCanvasReader;
import qtedu.Impact_design.domain.implementation.impactcheck.ImpactCheckReader;
import qtedu.Impact_design.domain.implementation.team.TeamReader;
import qtedu.Impact_design.domain.implementation.wincanvas.WinCanvasReader;
import qtedu.Impact_design.domain.model.en.CanvasType;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReportDataAggregator {

    private final TeamReader teamReader;
    private final ImpactCheckReader impactCheckReader;
    private final IdentityCanvasReader identityCanvasReader;
    private final FlowCanvasReader flowCanvasReader;
    private final WinCanvasReader winCanvasReader;
    private final FLetterOfIntentRepository fLetterOfIntentRepository;
    private final TeamUserRepository teamUserRepository;
    private final TbGameRepository tbGameRepository;
    private final GameTeamRepository gameTeamRepository;
    private final UserinfoRepository userinfoRepository;

    public ReportRawData load(Integer teamId) {
        List<Long> userIds = teamReader.readTeamMemberUserIdsByTeamId(teamId);

        List<FlowCanvasModel> flowCanvases = flowCanvasReader.readByUserIds(userIds);
        List<Long> goalIds = flowCanvases.stream()
                .map(FlowCanvasModel::getGoalId)
                .collect(Collectors.toList());

        // WinCanvas & Intent: 같은 게임의 각 팀 대표작성자(writer=1) 기준으로 조회
        List<Integer> allTeamIds = findAllTeamIdsForGame(teamId);
        List<Long> writerUserIds = findWriterUserIds(allTeamIds);
        List<WinCanvasModel> quickCanvases = winCanvasReader.readCanvasesByUserIds(writerUserIds, CanvasType.QUICK);
        List<WinCanvasModel> buildCanvases = winCanvasReader.readCanvasesByUserIds(writerUserIds, CanvasType.BUILD);

        return ReportRawData.builder()
                .impactChecks(impactCheckReader.readByUserIds(userIds))
                .identityCanvases(identityCanvasReader.readByUserIds(userIds))
                .flowCanvases(flowCanvases)
                .tacticals(flowCanvasReader.readTacticalsByGoalIds(goalIds))
                .strategicActivities(flowCanvasReader.readStrategicActivitiesByGoalIds(goalIds))
                .quickCanvases(quickCanvases)
                .buildCanvases(buildCanvases)
                .quickIntents(fLetterOfIntentRepository.findByTargetTeamIds(allTeamIds, CanvasType.QUICK))
                .buildIntents(fLetterOfIntentRepository.findByTargetTeamIds(allTeamIds, CanvasType.BUILD))
                .build();
    }

    private List<Integer> findAllTeamIdsForGame(Integer teamId) {
        Integer gameId = tbGameRepository.findByTeamId(teamId)
                .map(TbGameModel::getGameId)
                .orElse(null);
        if (gameId == null) return Collections.emptyList();
        return gameTeamRepository.findTeamIdsByGameId(gameId);
    }

    private List<Long> findWriterUserIds(List<Integer> allTeamIds) {
        if (allTeamIds.isEmpty()) return Collections.emptyList();

        List<TeamUserModel> allTeamUsers = teamUserRepository.findByTeamIdIn(allTeamIds);
        Map<Integer, List<Long>> teamUserMap = allTeamUsers.stream()
                .collect(Collectors.groupingBy(
                        TeamUserModel::getTeamId,
                        Collectors.mapping(TeamUserModel::getUserId, Collectors.toList())
                ));

        List<Long> writerUserIds = new ArrayList<>();
        for (Integer tid : allTeamIds) {
            List<Long> memberIds = teamUserMap.getOrDefault(tid, Collections.emptyList());
            if (memberIds.isEmpty()) continue;

            Long writerId = userinfoRepository.findWriterByUserIds(memberIds)
                    .map(u -> u.getUserId())
                    .orElse(memberIds.get(0));
            writerUserIds.add(writerId);
        }
        return writerUserIds;
    }

    public Map<Integer, ReportRawData> loadBulk(List<Integer> teamIds) {
        List<TeamUserModel> allTeamUsers = teamUserRepository.findByTeamIdIn(teamIds);
        Map<Integer, List<Long>> teamUserMap = allTeamUsers.stream()
                .collect(Collectors.groupingBy(
                        TeamUserModel::getTeamId,
                        Collectors.mapping(TeamUserModel::getUserId, Collectors.toList())
                ));

        List<Long> allUserIds = allTeamUsers.stream()
                .map(TeamUserModel::getUserId)
                .distinct()
                .collect(Collectors.toList());

        List<ImpactCheckModel> allImpactChecks = impactCheckReader.readByUserIds(allUserIds);
        List<IdentityCanvasModel> allIdentityCanvases = identityCanvasReader.readByUserIds(allUserIds);
        List<FlowCanvasModel> allFlowCanvases = flowCanvasReader.readByUserIds(allUserIds);
        List<WinCanvasModel> allQuickCanvases = winCanvasReader.readCanvasesByUserIds(allUserIds, CanvasType.QUICK);
        List<WinCanvasModel> allBuildCanvases = winCanvasReader.readCanvasesByUserIds(allUserIds, CanvasType.BUILD);

        List<Long> allGoalIds = allFlowCanvases.stream()
                .map(FlowCanvasModel::getGoalId)
                .collect(Collectors.toList());
        List<TacticalModel> allTacticals = flowCanvasReader.readTacticalsByGoalIds(allGoalIds);
        List<StrategicActivityModel> allStrategicActivities = flowCanvasReader.readStrategicActivitiesByGoalIds(allGoalIds);

        List<FLetterOfIntentModel> allBuildIntents = fLetterOfIntentRepository.findByTargetTeamIds(teamIds, CanvasType.BUILD);
        List<FLetterOfIntentModel> allQuickIntents = fLetterOfIntentRepository.findByTargetTeamIds(teamIds, CanvasType.QUICK);

        Map<Long, List<ImpactCheckModel>> impactCheckByUser = allImpactChecks.stream()
                .collect(Collectors.groupingBy(ImpactCheckModel::getUserId));
        Map<Long, List<IdentityCanvasModel>> identityByUser = allIdentityCanvases.stream()
                .collect(Collectors.groupingBy(IdentityCanvasModel::getUserId));
        Map<Long, List<FlowCanvasModel>> flowByUser = allFlowCanvases.stream()
                .collect(Collectors.groupingBy(FlowCanvasModel::getUserId));
        Map<Long, List<WinCanvasModel>> quickByUser = allQuickCanvases.stream()
                .collect(Collectors.groupingBy(WinCanvasModel::getUserId));
        Map<Long, List<WinCanvasModel>> buildByUser = allBuildCanvases.stream()
                .collect(Collectors.groupingBy(WinCanvasModel::getUserId));
        Map<Long, List<TacticalModel>> tacticalByGoal = allTacticals.stream()
                .collect(Collectors.groupingBy(TacticalModel::getGoalId));
        Map<Long, List<StrategicActivityModel>> strategicByGoal = allStrategicActivities.stream()
                .collect(Collectors.groupingBy(StrategicActivityModel::getGoalId));

        Map<String, List<FLetterOfIntentModel>> buildIntentByTeam = allBuildIntents.stream()
                .collect(Collectors.groupingBy(FLetterOfIntentModel::getInvestmentTarget));
        Map<String, List<FLetterOfIntentModel>> quickIntentByTeam = allQuickIntents.stream()
                .collect(Collectors.groupingBy(FLetterOfIntentModel::getInvestmentTarget));

        Map<Integer, ReportRawData> result = new HashMap<>();
        for (Integer teamId : teamIds) {
            List<Long> userIds = teamUserMap.getOrDefault(teamId, Collections.emptyList());

            List<FlowCanvasModel> teamFlowCanvases = userIds.stream()
                    .flatMap(uid -> flowByUser.getOrDefault(uid, Collections.emptyList()).stream())
                    .collect(Collectors.toList());

            List<Long> teamGoalIds = teamFlowCanvases.stream()
                    .map(FlowCanvasModel::getGoalId)
                    .collect(Collectors.toList());

            result.put(teamId, ReportRawData.builder()
                    .impactChecks(userIds.stream()
                            .flatMap(uid -> impactCheckByUser.getOrDefault(uid, Collections.emptyList()).stream())
                            .collect(Collectors.toList()))
                    .identityCanvases(userIds.stream()
                            .flatMap(uid -> identityByUser.getOrDefault(uid, Collections.emptyList()).stream())
                            .collect(Collectors.toList()))
                    .flowCanvases(teamFlowCanvases)
                    .tacticals(teamGoalIds.stream()
                            .flatMap(gid -> tacticalByGoal.getOrDefault(gid, Collections.emptyList()).stream())
                            .collect(Collectors.toList()))
                    .strategicActivities(teamGoalIds.stream()
                            .flatMap(gid -> strategicByGoal.getOrDefault(gid, Collections.emptyList()).stream())
                            .collect(Collectors.toList()))
                    .quickCanvases(userIds.stream()
                            .flatMap(uid -> quickByUser.getOrDefault(uid, Collections.emptyList()).stream())
                            .collect(Collectors.toList()))
                    .buildCanvases(userIds.stream()
                            .flatMap(uid -> buildByUser.getOrDefault(uid, Collections.emptyList()).stream())
                            .collect(Collectors.toList()))
                    .quickIntents(quickIntentByTeam.getOrDefault(String.valueOf(teamId), Collections.emptyList()))
                    .buildIntents(buildIntentByTeam.getOrDefault(String.valueOf(teamId), Collections.emptyList()))
                    .build());
        }
        return result;
    }
}
