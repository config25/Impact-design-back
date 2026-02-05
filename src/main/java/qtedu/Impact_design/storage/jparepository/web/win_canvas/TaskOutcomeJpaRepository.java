package qtedu.Impact_design.storage.jparepository.web.win_canvas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.win_canvas.TaskOutcome;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskOutcomeJpaRepository extends JpaRepository<TaskOutcome, Long> {
    List<TaskOutcome> findByCanvasIdOrderByOrderNoAsc(Long canvasId);
    Optional<TaskOutcome> findByCanvasIdAndOrderNo(Long canvasId, Integer orderNo);
}
