package qtedu.Impact_design.domain.repository.win_canvas;

import qtedu.Impact_design.domain.model.win_canvas.TaskInputModel;

import java.util.List;
import java.util.Optional;

public interface TaskInputRepository {
    List<TaskInputModel> findByCanvasId(Long canvasId);
    Optional<TaskInputModel> findByCanvasIdAndOrderNo(Long canvasId, Integer orderNo);
    TaskInputModel save(TaskInputModel taskInputModel);
}
