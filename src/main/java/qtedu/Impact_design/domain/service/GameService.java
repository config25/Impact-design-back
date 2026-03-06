package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.response.game.StudentDashboardResponse;
import qtedu.Impact_design.domain.implementation.game.GameReader;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameReader gameReader;

    public String getUserStep(Long userId) {
        return gameReader.readStepByUserId(userId);
    }

    public StudentDashboardResponse getDashboard(Long userId) {
        TbGameModel game = gameReader.readGameByUserId(userId);
        String teamName = gameReader.readTeamNameByUserId(userId);
        UserinfoModel userinfo = gameReader.readUserinfoByUserId(userId);

        return StudentDashboardResponse.builder()
                .className(game.getName())
                .classImage(gameReader.resolveImageUrl(game.getImageUrl()))
                .teamName(teamName)
                .userName(userinfo.getUserName())
                .build();
    }
}
