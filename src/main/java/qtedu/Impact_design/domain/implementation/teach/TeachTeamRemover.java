package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeachTeamRemover {

    private final TbGameRepository tbGameRepository;
    private final TbTeamRepository tbTeamRepository;
    private final TeamUserRepository teamUserRepository;
    private final UserinfoRepository userinfoRepository;

    @Transactional
    public void deleteTeam(Integer teamId, Integer gameId) {
        TbTeamModel team = tbTeamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        TbTeamModel updated = TbTeamModel.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .sequence(team.getSequence())
                .status(-1)
                .aiPlay(team.getAiPlay())
                .code(team.getCode())
                .isDoing(team.getIsDoing())
                .teamGubun(team.getTeamGubun())
                .numUser(team.getNumUser())
                .build();

        tbTeamRepository.save(updated);

        TbGameModel game = tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        int currentNumTeam = game.getNumTeam() != null ? game.getNumTeam() : 0;
        if (currentNumTeam > 0) {
            updateNumTeam(game, currentNumTeam - 1);
        }
    }

    @Transactional
    public void deleteTeamMembers(List<Long> userIds) {
        for (Long userId : userIds) {
            userinfoRepository.findByUserId(userId).ifPresent(user -> {
                if ("1".equals(user.getWriter())) {
                    userinfoRepository.setWriter(userId);
                }
            });
            teamUserRepository.deleteByUserId(userId);
        }
    }

    private void updateNumTeam(TbGameModel game, int newNumTeam) {
        TbGameModel updated = TbGameModel.builder()
                .gameId(game.getGameId())
                .name(game.getName())
                .code(game.getCode())
                .num(game.getNum())
                .numTeam(newNumTeam)
                .numMember(game.getNumMember())
                .createdAt(game.getCreatedAt())
                .endedAt(game.getEndedAt())
                .status(game.getStatus())
                .eStatus(game.getEStatus())
                .summary(game.getSummary())
                .totalDd(game.getTotalDd())
                .lang(game.getLang())
                .worldType(game.getWorldType())
                .step(game.getStep())
                .classType(game.getClassType())
                .isDoing(game.getIsDoing())
                .regDate(game.getRegDate())
                .popupId(game.getPopupId())
                .build();

        tbGameRepository.save(updated);
    }
}
