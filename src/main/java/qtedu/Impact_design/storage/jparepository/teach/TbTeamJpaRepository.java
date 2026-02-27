package qtedu.Impact_design.storage.jparepository.teach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.teach.TbTeam;

import java.util.List;
import java.util.Optional;

@Repository
public interface TbTeamJpaRepository extends JpaRepository<TbTeam, Integer> {
    List<TbTeam> findByCode(String code);
    @Query("SELECT t FROM TbTeam t WHERE t.code = :code AND t.teamId != :teamId AND (t.status IS NULL OR t.status != -1)")
    List<TbTeam> findByCodeExcludingDeletedTeam(@Param("code") String code, @Param("teamId") Integer teamId);
    Optional<TbTeam> findByTeamId(Integer teamId);
    List<TbTeam> findByTeamIdIn(List<Integer> teamIds);

    @Query(value = """
        SELECT t.* FROM tbteam t
        JOIN gameteam gt ON t.team_id = gt.team_id
        WHERE gt.game_id = :gameId AND t.status = -1
        ORDER BY t.sequence ASC
        """, nativeQuery = true)
    List<TbTeam> findDeletedTeamsByGameId(@Param("gameId") Integer gameId);

    @Query(value = """
        SELECT COALESCE(MAX(t.sequence), 0) FROM tbteam t
        JOIN gameteam gt ON t.team_id = gt.team_id
        WHERE gt.game_id = :gameId
        """, nativeQuery = true)
    int findMaxSequenceByGameId(@Param("gameId") Integer gameId);

    @Modifying
    @Query("UPDATE TbTeam t SET t.numUser = COALESCE(t.numUser, 0) + 1 WHERE t.teamId = :teamId")
    void incrementNumUser(@Param("teamId") Integer teamId);

    @Modifying
    @Query("UPDATE TbTeam t SET t.numUser = CASE WHEN COALESCE(t.numUser, 0) > 0 THEN t.numUser - 1 ELSE 0 END WHERE t.teamId = :teamId")
    void decrementNumUser(@Param("teamId") Integer teamId);
}
