package qtedu.Impact_design.storage.jparepository.teach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.teach.TeamUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamUserJpaRepository extends JpaRepository<TeamUser, Integer> {
    List<TeamUser> findByTeamId(Integer teamId);
    List<TeamUser> findByTeamIdIn(List<Integer> teamIds);
    Optional<TeamUser> findByUserId(Long userId);
    List<TeamUser> findByUserIdIn(List<Long> userIds);

    @Modifying
    @Query("DELETE FROM TeamUser tu WHERE tu.userId IN :userIds")
    void deleteByUserIdIn(@Param("userIds") List<Long> userIds);

    // 팀별 사용자 수 카운트
    int countByTeamId(Integer teamId);

    @Modifying
    @Query("UPDATE TeamUser tu SET tu.teamId = :teamId WHERE tu.userId = :userId")
    void updateTeamIdByUserId(@Param("userId") Long userId, @Param("teamId") Integer teamId);

    void deleteByUserId(Long userId);

    List<TeamUser> findByTeamIdAndIsDoing(Integer teamId, Integer isDoing);

    @Query(value = """
            SELECT t.name FROM teamuser tu
            INNER JOIN tbteam t ON tu.team_id = t.team_id
            WHERE tu.user_id = :userId
            LIMIT 1
            """, nativeQuery = true)
    Optional<String> findTeamNameByUserId(@Param("userId") Long userId);
}
