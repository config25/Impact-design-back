package qtedu.Impact_design.storage.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.LoggedIn;

import java.util.Optional;

@Repository
public interface LoggedInJpaRepository extends JpaRepository<LoggedIn, Long> {

    Optional<LoggedIn> findByUserNo(Long userNo);

    void deleteByUserNo(Long userNo);
}
