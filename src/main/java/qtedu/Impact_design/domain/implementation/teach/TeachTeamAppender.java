package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;

@Component
@RequiredArgsConstructor
public class TeachTeamAppender {

    private final TbGameRepository tbGameRepository;
    private final TbTeamRepository tbTeamRepository;
    private final GameTeamRepository gameTeamRepository;

    @Transactional
    public Integer addTeam(Integer gameId) {
        TbGameModel game = tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        int nextSequence = tbTeamRepository.findMaxSequenceByGameId(gameId) + 1;
        String teamName = "팀" + nextSequence;

        TbTeamModel team = TbTeamModel.builder()
                .name(teamName)
                .sequence(nextSequence)
                .code(game.getCode())
                .status(0)
                .isDoing(1)
                .build();

        TbTeamModel savedTeam = tbTeamRepository.save(team);
        gameTeamRepository.save(gameId, savedTeam.getTeamId());
        tbGameRepository.incrementNumTeam(gameId);

        return savedTeam.getTeamId();
    }

    @Transactional
    public Integer addEvaluationTeam(Integer gameId) {
        TbGameModel game = tbGameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        int nextSequence = tbTeamRepository.findMaxSequenceByGameId(gameId) + 1;

        TbTeamModel team = TbTeamModel.builder()
                .name("평가팀")
                .sequence(nextSequence)
                .code(game.getCode())
                .status(0)
                .isDoing(1)
                .teamGubun(1)
                .build();

        TbTeamModel savedTeam = tbTeamRepository.save(team);
        gameTeamRepository.save(gameId, savedTeam.getTeamId());
        tbGameRepository.incrementNumTeam(gameId);

        return savedTeam.getTeamId();
    }
}
