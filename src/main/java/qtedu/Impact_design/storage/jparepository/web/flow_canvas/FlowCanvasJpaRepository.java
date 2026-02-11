package qtedu.Impact_design.storage.jparepository.web.flow_canvas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.flow_canvas.FlowCanvas;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlowCanvasJpaRepository extends JpaRepository<FlowCanvas, Long> {
    List<FlowCanvas> findByUserIdOrderByOrderNoAsc(Long userId);
    Optional<FlowCanvas> findByUserIdAndOrderNo(Long userId, Integer orderNo);
    List<FlowCanvas> findByUserIdIn(List<Long> userIds);
    boolean existsByUserIdAndSubmittedTrue(Long userId);
    boolean existsByUserIdAndSubmitted(Long userId, Boolean submitted);
    List<FlowCanvas> findByUserId(Long userId);
}
