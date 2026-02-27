package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntent2Repository;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.ImpactCheckRepository;
import qtedu.Impact_design.domain.repository.IdentityCanvasRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.flow_canvas.FlowCanvasRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 팀별 제출 상태 체크
 * 각 팀의 대표작성자(writer=1)의 제출 여부를 확인
 */
@Component
@RequiredArgsConstructor
public class TeamSubmitStatusChecker {

    private final TeamUserRepository teamUserRepository;
    private final ImpactCheckRepository impactCheckRepository;
    private final IdentityCanvasRepository identityCanvasRepository;
    private final FlowCanvasRepository flowCanvasRepository;
    private final WinCanvasRepository winCanvasRepository;
    private final FLetterOfIntentRepository fLetterOfIntentRepository;
    private final FLetterOfIntent2Repository fLetterOfIntent2Repository;
    private final UserinfoRepository userinfoRepository;

    /**
     * 팀의 대표작성자 userId 조회
     */
    public Long findWriterUserId(Integer teamId) {
        List<TeamUserModel> teamUsers = teamUserRepository.findByTeamId(teamId);
        if (teamUsers.isEmpty()) {
            return null;
        }

        List<Long> userIds = teamUsers.stream()
                .map(TeamUserModel::getUserId)
                .toList();

        // writer = 1인 사용자 찾기, 없으면 첫 번째 사용자
        return userinfoRepository.findWriterByUserIds(userIds)
                .map(user -> user.getUserId())
                .orElse(userIds.get(0));
    }

    /**
     * submitA: 성과관리 현황진단 (ImpactCheck)
     */
    public String checkSubmitA(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return impactCheckRepository.existsSubmittedByUserId(writerUserId)
                ? "제출" : "미제출";
    }

    /**
     * submitB: 정체성 설계 (IdentityCanvas)
     */
    public String checkSubmitB(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return identityCanvasRepository.existsSubmittedByUserId(writerUserId)
                ? "제출" : "미제출";
    }

    /**
     * submitC: 성과경로 설계 (FlowCanvas)
     */
    public String checkSubmitC(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return flowCanvasRepository.existsSubmittedByUserId(writerUserId)
                ? "제출" : "미제출";
    }

    /**
     * submitD: 전술적 실행과제 (WinCanvas - QUICK)
     */
    public String checkSubmitD(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return winCanvasRepository.existsSubmittedByUserIdAndCanvasType(writerUserId, CanvasType.QUICK)
                ? "제출" : "미제출";
    }

    /**
     * submitE: 전략적 실행과제 (WinCanvas - BUILD)
     */
    public String checkSubmitE(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return winCanvasRepository.existsSubmittedByUserIdAndCanvasType(writerUserId, CanvasType.BUILD)
                ? "제출" : "미제출";
    }

    /**
     * submitF: 실행과제 검증 (FLetterOfIntent + FLetterOfIntent2)
     */
    public String checkSubmitF(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        boolean intent1 = fLetterOfIntentRepository.existsSubmittedByUserId(writerUserId);
        boolean intent2 = fLetterOfIntent2Repository.existsSubmittedByUserId(writerUserId);
        return (intent1 || intent2) ? "제출" : "미제출";
    }

    /**
     * 여러 팀의 제출 상태를 한번에 조회 (배치)
     * @return teamId → TeamSubmitResult 매핑
     */
    public Map<Integer, TeamSubmitResult> checkAllSubmitStatuses(
            Map<Integer, List<Long>> teamUserIdsMap) {

        if (teamUserIdsMap.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1. 전체 userIds 수집
        List<Long> allUserIds = teamUserIdsMap.values().stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        // 2. writer 찾기 (전체 userIds에서 한번에)
        Map<Long, Boolean> writerMap = userinfoRepository.findAllByUserIds(allUserIds).stream()
                .collect(Collectors.toMap(
                        u -> u.getUserId(),
                        u -> u.isWriter(),
                        (a, b) -> a
                ));

        // 3. 팀별 writerUserId 매핑
        Map<Integer, Long> teamWriterMap = new HashMap<>();
        for (Map.Entry<Integer, List<Long>> entry : teamUserIdsMap.entrySet()) {
            Long writerUserId = entry.getValue().stream()
                    .filter(uid -> Boolean.TRUE.equals(writerMap.get(uid)))
                    .findFirst()
                    .orElse(entry.getValue().isEmpty() ? null : entry.getValue().get(0));
            teamWriterMap.put(entry.getKey(), writerUserId);
        }

        // 4. writerUserIds 수집
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

        // 5. 각 리포지토리에서 배치 조회 → submitted userIds 수집
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

        Set<Long> intent1Submitted = fLetterOfIntentRepository.findSubmittedUserIds(writerUserIds);
        Set<Long> intent2Submitted = fLetterOfIntent2Repository.findSubmittedUserIds(writerUserIds);

        // 6. 팀별 결과 조합
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
                    (intent1Submitted.contains(writerUserId) || intent2Submitted.contains(writerUserId))
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
