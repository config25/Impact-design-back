package qtedu.Impact_design.storage.jparepository.teach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.team.ClassInfoProjection;
import qtedu.Impact_design.storage.jpaentity.teach.TbGame;

import java.util.List;

@Repository
public interface TbGameJpaRepository extends JpaRepository<TbGame, Integer> {

    @Query(value = """
            SELECT
                G.game_id AS gameId,
                G.name AS name,
                G.num_team AS numTeam,
                G.total_dd AS totalDd,
                G.image_url AS imageUrl,
                M.mission_id AS missionId,
                M.sequence AS sequence,
                M.subject AS subject,
                M.summary AS summary,
                M.startdate AS startdate,
                M.enddate AS enddate,
                M.dd_year AS ddYear,
                M.dd_term AS ddTerm
            FROM tbgame G
            INNER JOIN gameadmin A ON G.game_id = A.game_id
            LEFT JOIN (
                SELECT
                    M1.game_id,
                    M1.mission_id,
                    M1.sequence,
                    M1.subject,
                    M1.summary,
                    M1.startdate,
                    M1.enddate,
                    M1.dd_year,
                    M1.dd_term
                FROM tbmission M1
                INNER JOIN (
                    SELECT game_id, MAX(dd_year) AS max_dd_year, MAX(dd_term) AS max_dd_term
                    FROM tbmission
                    GROUP BY game_id
                ) M2 ON M1.game_id = M2.game_id
                    AND M1.dd_year = M2.max_dd_year
                    AND M1.dd_term = M2.max_dd_term
            ) M ON G.game_id = M.game_id
            WHERE A.user_id = :userId
              AND G.status = :status
              AND G.is_doing = 1
            ORDER BY G.game_id DESC
            """, nativeQuery = true)
    List<ClassInfoProjection> findClassList(@Param("userId") Long userId, @Param("status") Integer status);

    @Query(value = """
            SELECT
                G.game_id AS gameId,
                G.name AS name,
                G.num_team AS numTeam,
                G.total_dd AS totalDd,
                G.image_url AS imageUrl,
                M.mission_id AS missionId,
                M.sequence AS sequence,
                M.subject AS subject,
                M.summary AS summary,
                M.startdate AS startdate,
                M.enddate AS enddate,
                M.dd_year AS ddYear,
                M.dd_term AS ddTerm
            FROM tbgame G
            LEFT JOIN (
                SELECT
                    M1.game_id,
                    M1.mission_id,
                    M1.sequence,
                    M1.subject,
                    M1.summary,
                    M1.startdate,
                    M1.enddate,
                    M1.dd_year,
                    M1.dd_term
                FROM tbmission M1
                INNER JOIN (
                    SELECT game_id, MAX(dd_year) AS max_dd_year, MAX(dd_term) AS max_dd_term
                    FROM tbmission
                    GROUP BY game_id
                ) M2 ON M1.game_id = M2.game_id
                    AND M1.dd_year = M2.max_dd_year
                    AND M1.dd_term = M2.max_dd_term
            ) M ON G.game_id = M.game_id
            ORDER BY G.game_id ASC
            """, nativeQuery = true)
    List<ClassInfoProjection> findAllClassList();

    @Query(value = """
            SELECT
                G.game_id AS gameId,
                G.name AS name,
                G.num_team AS numTeam,
                G.total_dd AS totalDd,
                G.image_url AS imageUrl,
                M.mission_id AS missionId,
                M.sequence AS sequence,
                M.subject AS subject,
                M.summary AS summary,
                M.startdate AS startdate,
                M.enddate AS enddate,
                M.dd_year AS ddYear,
                M.dd_term AS ddTerm
            FROM tbgame G
            LEFT JOIN (
                SELECT
                    M1.game_id,
                    M1.mission_id,
                    M1.sequence,
                    M1.subject,
                    M1.summary,
                    M1.startdate,
                    M1.enddate,
                    M1.dd_year,
                    M1.dd_term
                FROM tbmission M1
                INNER JOIN (
                    SELECT game_id, MAX(dd_year) AS max_dd_year, MAX(dd_term) AS max_dd_term
                    FROM tbmission
                    GROUP BY game_id
                ) M2 ON M1.game_id = M2.game_id
                    AND M1.dd_year = M2.max_dd_year
                    AND M1.dd_term = M2.max_dd_term
            ) M ON G.game_id = M.game_id
            WHERE G.status = :status
              AND G.is_doing = 1
            ORDER BY G.game_id DESC
            """, nativeQuery = true)
    List<ClassInfoProjection> findAllClassListByStatus(@Param("status") Integer status);

    @Modifying
    @Query("UPDATE TbGame g SET g.numTeam = COALESCE(g.numTeam, 0) + 1 WHERE g.gameId = :gameId")
    void incrementNumTeam(@Param("gameId") Integer gameId);

    @Modifying
    @Query("UPDATE TbGame g SET g.numTeam = CASE WHEN COALESCE(g.numTeam, 0) > 0 THEN g.numTeam - 1 ELSE 0 END WHERE g.gameId = :gameId")
    void decrementNumTeam(@Param("gameId") Integer gameId);

    @Query(value = """
            SELECT G.name FROM tbgame G
            INNER JOIN gameteam GT ON G.game_id = GT.game_id
            INNER JOIN teamuser TU ON GT.team_id = TU.team_id
            WHERE TU.user_id = :userId
            LIMIT 1
            """, nativeQuery = true)
    String findGameNameByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT G.image_url FROM tbgame G
            INNER JOIN gameteam GT ON G.game_id = GT.game_id
            INNER JOIN teamuser TU ON GT.team_id = TU.team_id
            WHERE TU.user_id = :userId
            LIMIT 1
            """, nativeQuery = true)
    String findGameImageUrlByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT G.* FROM tbgame G
            INNER JOIN gameteam GT ON G.game_id = GT.game_id
            INNER JOIN teamuser TU ON GT.team_id = TU.team_id
            WHERE TU.user_id = :userId
            LIMIT 1
            """, nativeQuery = true)
    java.util.Optional<TbGame> findByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE TbGame g SET g.imageUrl = :imageUrl WHERE g.gameId = :gameId")
    void updateImageUrl(@Param("gameId") Integer gameId, @Param("imageUrl") String imageUrl);
}
