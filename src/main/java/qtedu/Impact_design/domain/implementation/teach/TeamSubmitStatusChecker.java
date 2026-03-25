package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.ImpactCheckRepository;
import qtedu.Impact_design.domain.repository.IdentityCanvasRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.flow_canvas.FlowCanvasRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeamSubmitStatusChecker {

    private final TeamUserRepository teamUserRepository;
    private final ImpactCheckRepository impactCheckRepository;
    private final IdentityCanvasRepository identityCanvasRepository;
    private final FlowCanvasRepository flowCanvasRepository;
    private final WinCanvasRepository winCanvasRepository;
    private final FLetterOfIntentRepository fLetterOfIntentRepository;
    private final UserinfoRepository userinfoRepository;

    public Long findWriterUserId(Integer teamId) {
        List<TeamUserModel> teamUsers = teamUserRepository.findByTeamId(teamId);
        if (teamUsers.isEmpty()) {
            throw new NotFoundException(ErrorCode.TEAM_NOT_FOUND);
        }

        List<Long> userIds = teamUsers.stream()
                .map(TeamUserModel::getUserId)
                .toList();

        return userinfoRepository.findWriterByUserIds(userIds)
                .map(user -> user.getUserId())
                .orElse(userIds.get(0));
    }

    public String checkSubmitA(Long writerUserId) {
        return impactCheckRepository.existsSubmittedByUserId(writerUserId)
                ? "제출" : "미제출";
    }

    public String checkSubmitB(Long writerUserId) {
        return identityCanvasRepository.existsSubmittedByUserId(writerUserId)
                ? "제출" : "미제출";
    }

    public String checkSubmitC(Long writerUserId) {
        return flowCanvasRepository.existsSubmittedByUserId(writerUserId)
                ? "제출" : "미제출";
    }

    public String checkSubmitD(Long writerUserId) {
        return winCanvasRepository.existsSubmittedByUserIdAndCanvasType(writerUserId, CanvasType.QUICK)
                ? "제출" : "미제출";
    }

    public String checkSubmitE(Long writerUserId) {
        return winCanvasRepository.existsSubmittedByUserIdAndCanvasType(writerUserId, CanvasType.BUILD)
                ? "제출" : "미제출";
    }

    public String checkSubmitF(Long writerUserId) {
        boolean intentBuild = fLetterOfIntentRepository.existsSubmittedByUserId(writerUserId, CanvasType.BUILD);
        boolean intentQuick = fLetterOfIntentRepository.existsSubmittedByUserId(writerUserId, CanvasType.QUICK);
        return (intentBuild && intentQuick) ? "제출" : "미제출";
    }

    public Map<Integer, TeamSubmitResult> checkAllSubmitStatuses(
            Map<Integer, List<Long>> teamUserIdsMap) {

        if (teamUserIdsMap.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> allUserIds = teamUserIdsMap.values().stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Boolean> writerMap = userinfoRepository.findAllByUserIds(allUserIds).stream()
                .collect(Collectors.toMap(
                        u -> u.getUserId(),
                        u -> u.isWriter(),
                        (a, b) -> a
                ));

        Map<Integer, Long> teamWriterMap = new HashMap<>();
        for (Map.Entry<Integer, List<Long>> entry : teamUserIdsMap.entrySet()) {
            Long writerUserId = entry.getValue().stream()
                    .filter(uid -> Boolean.TRUE.equals(writerMap.get(uid)))
                    .findFirst()
                    .orElse(entry.getValue().isEmpty() ? null : entry.getValue().get(0));
            teamWriterMap.put(entry.getKey(), writerUserId);
        }

        List<Long> writerUserIds = teamWriterMap.values().stream()
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        if (writerUserIds.isEmpty()) {
            Map<Integer, TeamSubmitResult> emptyResult = new HashMap<>();
            teamUserIdsMap.keySet().forEach(teamId ->
                    emptyResult.put(teamId, TeamSubmitResult.allNotSubmitted()));
            return emptyResult;
        }

        Set<Long> impactSubmitted = impactCheckRepository.findByUserIdIn(writerUserIds).stream()
                .filter(m -> Boolean.TRUE.equals(m.getSubmitted()))
                .map(m -> m.getUserId())
                .collect(Collectors.toSet());

        Set<Long> identitySubmitted = identityCanvasRepository.findByUserIdIn(writerUserIds).stream()
                .filter(m -> Boolean.TRUE.equals(m.getSubmitted()))
                .map(m -> m.getUserId())
                .collect(Collectors.toSet());

        Set<Long> flowSubmitted = flowCanvasRepository.findByUserIdIn(writerUserIds).stream()
                .filter(m -> Boolean.TRUE.equals(m.getSubmitted()))
                .map(m -> m.getUserId())
                .collect(Collectors.toSet());

        Set<Long> quickWinSubmitted = winCanvasRepository
                .findByUserIdInAndCanvasType(writerUserIds, CanvasType.QUICK).stream()
                .filter(m -> Boolean.TRUE.equals(m.getSubmitted()))
                .map(m -> m.getUserId())
                .collect(Collectors.toSet());

        Set<Long> buildWinSubmitted = winCanvasRepository
                .findByUserIdInAndCanvasType(writerUserIds, CanvasType.BUILD).stream()
                .filter(m -> Boolean.TRUE.equals(m.getSubmitted()))
                .map(m -> m.getUserId())
                .collect(Collectors.toSet());

        Set<Long> intentBuildSubmitted = fLetterOfIntentRepository.findSubmittedUserIds(writerUserIds, CanvasType.BUILD);
        Set<Long> intentQuickSubmitted = fLetterOfIntentRepository.findSubmittedUserIds(writerUserIds, CanvasType.QUICK);

        Map<Integer, TeamSubmitResult> result = new HashMap<>();
        for (Map.Entry<Integer, Long> entry : teamWriterMap.entrySet()) {
            Integer teamId = entry.getKey();
            Long writerUserId = entry.getValue();

            if (writerUserId == null) {
                result.put(teamId, TeamSubmitResult.allNotSubmitted());
                continue;
            }

            result.put(teamId, new TeamSubmitResult(
                    impactSubmitted.contains(writerUserId) ? "제출" : "미제출",
                    identitySubmitted.contains(writerUserId) ? "제출" : "미제출",
                    flowSubmitted.contains(writerUserId) ? "제출" : "미제출",
                    quickWinSubmitted.contains(writerUserId) ? "제출" : "미제출",
                    buildWinSubmitted.contains(writerUserId) ? "제출" : "미제출",
                    (intentBuildSubmitted.contains(writerUserId) && intentQuickSubmitted.contains(writerUserId))
                            ? "제출" : "미제출"
            ));
        }

        return result;
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class TeamSubmitResult {
        private final String submitA;
        private final String submitB;
        private final String submitC;
        private final String submitD;
        private final String submitE;
        private final String submitF;

        public static TeamSubmitResult allNotSubmitted() {
            return new TeamSubmitResult("미제출", "미제출", "미제출", "미제출", "미제출", "미제출");
        }
    }
}
