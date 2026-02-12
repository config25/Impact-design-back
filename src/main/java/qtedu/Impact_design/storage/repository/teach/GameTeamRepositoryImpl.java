package qtedu.Impact_design.storage.repository.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;
import qtedu.Impact_design.storage.jpaentity.teach.GameTeam;
import qtedu.Impact_design.storage.jparepository.teach.GameTeamJpaRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GameTeamRepositoryImpl implements GameTeamRepository {

    private final GameTeamJpaRepository gameTeamJpaRepository;

    @Override
    public void save(Integer gameId, Integer teamId) {
        GameTeam gameTeam = GameTeam.builder()
                .gameId(gameId)
                .teamId(teamId)
                .build();
        gameTeamJpaRepository.save(gameTeam);
    }

    @Override
    public List<Integer> findTeamIdsByGameId(Integer gameId) {
        return gameTeamJpaRepository.findTeamIdsByGameId(gameId);
    }

    @Override
    public int countByGameId(Integer gameId) {
        return gameTeamJpaRepository.countByGameId(gameId);
    }
}
