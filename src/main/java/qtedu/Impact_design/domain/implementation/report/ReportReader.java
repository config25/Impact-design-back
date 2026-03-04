package qtedu.Impact_design.domain.implementation.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReportReader {

    private final TbGameRepository tbGameRepository;
    private final TbTeamRepository tbTeamRepository;
    private final GameTeamRepository gameTeamRepository;

    public Optional<TbGameModel> findGameByTeamId(Integer teamId) {
        return tbGameRepository.findByTeamId(teamId);
    }

    public Optional<TbGameModel> findGameById(Integer gameId) {
        return tbGameRepository.findById(gameId);
    }

    public Optional<TbTeamModel> findTeamById(Integer teamId) {
        return tbTeamRepository.findByTeamId(teamId);
    }

    public List<TbTeamModel> findTeamsByIds(List<Integer> teamIds) {
        return tbTeamRepository.findByTeamIdIn(teamIds);
    }

    public List<Integer> findTeamIdsByGameId(Integer gameId) {
        return gameTeamRepository.findTeamIdsByGameId(gameId);
    }
}
