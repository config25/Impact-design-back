package qtedu.Impact_design.domain.implementation.funding;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 팀의 대표작성자(writer=1)를 찾아서 해당 사용자의 프로젝트명(taskName)을 반환
 */
@Component
@RequiredArgsConstructor
public class TeamBusinessNameResolver {

    private final TeamUserRepository teamUserRepository;
    private final UserinfoRepository userinfoRepository;
    private final WinCanvasRepository winCanvasRepository;

    public String resolve(String canvasType, Integer teamId) {
        List<TeamUserModel> teamUsers = teamUserRepository.findByTeamId(teamId);
        if (teamUsers.isEmpty()) {
            return "";
        }

        List<Long> userIds = teamUsers.stream()
                .map(TeamUserModel::getUserId)
                .collect(Collectors.toList());

        // 대표작성자(writer = 1) 찾기, 없으면 첫 번째 사용자
        Long writerUserId = userinfoRepository.findWriterByUserIds(userIds)
                .map(user -> user.getUserId())
                .orElse(userIds.get(0));

        CanvasType type = isBuildType(canvasType) ? CanvasType.BUILD : CanvasType.QUICK;
        return winCanvasRepository.findByUserIdAndCanvasType(writerUserId, type)
                .map(WinCanvasModel::getTaskName)
                .orElse("");
    }

    public Map<Integer, String> resolveBulk(String canvasType, List<Integer> teamIds) {
        if (teamIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1. 전체 팀원 배치 조회
        List<TeamUserModel> allTeamUsers = teamUserRepository.findByTeamIdIn(teamIds);
        Map<Integer, List<Long>> teamUserIdsMap = allTeamUsers.stream()
                .collect(Collectors.groupingBy(
                        TeamUserModel::getTeamId,
                        Collectors.mapping(TeamUserModel::getUserId, Collectors.toList())));

        // 2. 전체 userIds에서 writer 찾기
        List<Long> allUserIds = allTeamUsers.stream()
                .map(TeamUserModel::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Boolean> writerMap = userinfoRepository.findAllByUserIds(allUserIds).stream()
                .collect(Collectors.toMap(
                        u -> u.getUserId(),
                        u -> u.isWriter(),
                        (a, b) -> a));

        // 3. 팀별 writerUserId 매핑
        Map<Integer, Long> teamWriterMap = new HashMap<>();
        for (Integer teamId : teamIds) {
            List<Long> userIds = teamUserIdsMap.getOrDefault(teamId, Collections.emptyList());
            Long writerUserId = userIds.stream()
                    .filter(uid -> Boolean.TRUE.equals(writerMap.get(uid)))
                    .findFirst()
                    .orElse(userIds.isEmpty() ? null : userIds.get(0));
            teamWriterMap.put(teamId, writerUserId);
        }

        // 4. writerUserIds로 canvas 배치 조회
        List<Long> writerUserIds = teamWriterMap.values().stream()
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        CanvasType type = isBuildType(canvasType) ? CanvasType.BUILD : CanvasType.QUICK;
        Map<Long, String> canvasTaskNameMap = winCanvasRepository
                .findByUserIdInAndCanvasType(writerUserIds, type).stream()
                .collect(Collectors.toMap(
                        WinCanvasModel::getUserId,
                        c -> c.getTaskName() != null ? c.getTaskName() : "",
                        (a, b) -> a));

        // 5. 결과 조합
        Map<Integer, String> result = new HashMap<>();
        for (Integer teamId : teamIds) {
            Long writerUserId = teamWriterMap.get(teamId);
            result.put(teamId, writerUserId != null
                    ? canvasTaskNameMap.getOrDefault(writerUserId, "")
                    : "");
        }
        return result;
    }

    private boolean isBuildType(String canvasType) {
        return "build".equalsIgnoreCase(canvasType);
    }
}
