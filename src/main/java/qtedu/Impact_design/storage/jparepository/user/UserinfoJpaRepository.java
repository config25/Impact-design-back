package qtedu.Impact_design.storage.jparepository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.implementation.teach.StudentWithTeamProjection;
import qtedu.Impact_design.storage.jpaentity.user.Userinfo;

import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserinfoJpaRepository extends JpaRepository<Userinfo, Long> {

    Optional<Userinfo> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    Optional<Userinfo> findFirstByUserIdInAndWriter(List<Long> userIds, String writer);

    @Query(value = """
        SELECT u.user_id AS userId, u.id AS loginId, u.user_name AS userName,
               t.team_id AS teamId, t.name AS teamName
        FROM userinfo u
        LEFT JOIN teamuser tu ON u.user_id = tu.user_id
        LEFT JOIN tbteam t ON tu.team_id = t.team_id
        WHERE u.code = :code
        ORDER BY t.name
        """, nativeQuery = true)
    List<StudentWithTeamProjection> findStudentsWithTeamByCode(@Param("code") String code);

    @Query(value = "SELECT u.id FROM userinfo u WHERE u.id LIKE :pattern ORDER BY u.id DESC LIMIT 1", nativeQuery = true)
    Optional<String> findLastLoginIdByPattern(@Param("pattern") String pattern);

    @Modifying
    @Query("UPDATE Userinfo u SET u.writer = null WHERE u.userId IN :userIds")
    void clearWriterByUserIds(@Param("userIds") List<Long> userIds);

    @Modifying
    @Query("UPDATE Userinfo u SET u.writer = '1' WHERE u.userId = :userId")
    void setWriter(@Param("userId") Long userId);
}
