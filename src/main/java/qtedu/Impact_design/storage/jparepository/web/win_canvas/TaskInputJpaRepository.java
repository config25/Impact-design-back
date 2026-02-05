package qtedu.Impact_design.storage.jparepository.web.win_canvas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.win_canvas.TaskInput;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskInputJpaRepository extends JpaRepository<TaskInput, Long> {
    List<TaskInput> findByCanvasIdOrderByOrderNoAsc(Long canvasId);
    Optional<TaskInput> findByCanvasIdAndOrderNo(Long canvasId, Integer orderNo);
}
