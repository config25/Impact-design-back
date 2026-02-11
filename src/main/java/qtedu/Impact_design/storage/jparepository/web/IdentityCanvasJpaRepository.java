package qtedu.Impact_design.storage.jparepository.web;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.IdentityCanvas;

import java.util.List;
import java.util.Optional;

@Repository
public interface IdentityCanvasJpaRepository extends JpaRepository<IdentityCanvas, Long> {
    Optional<IdentityCanvas> findByUserId(Long userId);
    List<IdentityCanvas> findByUserIdIn(List<Long> userIds);
    boolean existsByUserIdAndSubmittedTrue(Long userId);
    boolean existsByUserIdAndSubmitted(Long userId, Boolean submitted);
}
