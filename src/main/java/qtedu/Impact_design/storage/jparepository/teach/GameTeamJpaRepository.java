package qtedu.Impact_design.storage.jparepository.teach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.teach.GameTeam;
import qtedu.Impact_design.storage.jpaentity.teach.GameTeamId;

import java.util.List;

@Repository
public interface GameTeamJpaRepository extends JpaRepository<GameTeam, GameTeamId> {

    List<GameTeam> findByGameId(Integer gameId);

    @Query("SELECT gt.teamId FROM GameTeam gt WHERE gt.gameId = :gameId")
    List<Integer> findTeamIdsByGameId(@Param("gameId") Integer gameId);

    int countByGameId(Integer gameId);
}
