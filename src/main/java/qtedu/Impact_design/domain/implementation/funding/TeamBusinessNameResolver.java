package qtedu.Impact_design.domain.implementation.funding;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

import java.util.List;
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

    private boolean isBuildType(String canvasType) {
        return "build".equalsIgnoreCase(canvasType);
    }
}
