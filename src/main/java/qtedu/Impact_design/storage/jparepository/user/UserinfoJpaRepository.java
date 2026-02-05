package qtedu.Impact_design.storage.jparepository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.user.Userinfo;

import java.util.Optional;

@Repository
public interface UserinfoJpaRepository extends JpaRepository<Userinfo, Long> {

    Optional<Userinfo> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
