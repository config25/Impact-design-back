package qtedu.Impact_design.storage.jparepository.web;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.ImpactCheck;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImpactCheckJpaRepository extends JpaRepository<ImpactCheck, Long> {
    Optional<ImpactCheck> findByUserId(Long userId);
    List<ImpactCheck> findByUserIdIn(List<Long> userIds);
    boolean existsByUserIdAndSubmitted(Long userId, Boolean submitted);
}
