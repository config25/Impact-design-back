package qtedu.Impact_design.domain.repository.teach;

import java.util.List;

public interface GameTeamRepository {
    void save(Integer gameId, Integer teamId);
    List<Integer> findTeamIdsByGameId(Integer gameId);
    int countByGameId(Integer gameId);
}
