package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.response.game.StudentDashboardResponse;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

@Service
@RequiredArgsConstructor
public class GameService {

    private final TbGameRepository tbGameRepository;
    private final TeamUserRepository teamUserRepository;
    private final TbTeamRepository tbTeamRepository;
    private final UserinfoRepository userinfoRepository;

    public String getUserStep(Long userId) {
        return tbGameRepository.findStepByUserId(userId);
    }

    public StudentDashboardResponse getDashboard(Long userId) {
        var game = tbGameRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        String classImage = tbGameRepository.findGameImageUrlByUserId(userId);

        var teamUser = teamUserRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        var team = tbTeamRepository.findByTeamId(teamUser.getTeamId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        var userinfo = userinfoRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return StudentDashboardResponse.builder()
                .className(game.getName())
                .classImage(classImage)
                .teamName(team.getName())
                .userName(userinfo.getUserName())
                .build();
    }
}
