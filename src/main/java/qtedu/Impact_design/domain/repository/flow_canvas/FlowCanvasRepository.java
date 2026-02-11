package qtedu.Impact_design.domain.repository.flow_canvas;

import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;

import java.util.List;
import java.util.Optional;

public interface FlowCanvasRepository {
    List<FlowCanvasModel> findByUserId(Long userId);
    Optional<FlowCanvasModel> findById(Long goalId);
    Optional<FlowCanvasModel> findByUserIdAndOrderNo(Long userId, Integer orderNo);
    FlowCanvasModel save(FlowCanvasModel flowCanvas);
    void deleteAllByIdIn(List<Long> goalIds);
    List<FlowCanvasModel> findByUserIdIn(List<Long> userIds);
    boolean existsSubmittedByUserId(Long userId);
    void submitAllByUserId(Long userId);
}
