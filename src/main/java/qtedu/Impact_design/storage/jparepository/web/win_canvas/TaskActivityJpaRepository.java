package qtedu.Impact_design.storage.jparepository.web.win_canvas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.win_canvas.TaskActivity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskActivityJpaRepository extends JpaRepository<TaskActivity, Long> {
    List<TaskActivity> findByCanvasIdOrderByOrderNoAsc(Long canvasId);
    Optional<TaskActivity> findByCanvasIdAndOrderNo(Long canvasId, Integer orderNo);
}
