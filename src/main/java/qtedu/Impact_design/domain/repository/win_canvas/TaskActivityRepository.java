package qtedu.Impact_design.domain.repository.win_canvas;

import qtedu.Impact_design.domain.model.win_canvas.TaskActivityModel;

import java.util.List;
import java.util.Optional;

public interface TaskActivityRepository {
    List<TaskActivityModel> findByCanvasId(Long canvasId);
    Optional<TaskActivityModel> findByCanvasIdAndOrderNo(Long canvasId, Integer orderNo);
    TaskActivityModel save(TaskActivityModel taskActivityModel);
}
