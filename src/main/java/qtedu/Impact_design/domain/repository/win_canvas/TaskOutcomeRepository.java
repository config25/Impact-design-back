package qtedu.Impact_design.domain.repository.win_canvas;

import qtedu.Impact_design.domain.model.win_canvas.TaskOutcomeModel;

import java.util.List;
import java.util.Optional;

public interface TaskOutcomeRepository {
    List<TaskOutcomeModel> findByCanvasId(Long canvasId);
    Optional<TaskOutcomeModel> findByCanvasIdAndOrderNo(Long canvasId, Integer orderNo);
    TaskOutcomeModel save(TaskOutcomeModel taskOutcomeModel);
}
