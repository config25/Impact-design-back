package qtedu.Impact_design.domain.repository.flow_canvas;

import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;

import java.util.List;
import java.util.Optional;

public interface StrategicActivityRepository {
    List<StrategicActivityModel> findByGoalIdIn(List<Long> goalIds);
    Optional<StrategicActivityModel> findByGoalIdAndOrderNo(Long goalId, Integer orderNo);
    StrategicActivityModel save(StrategicActivityModel strategicActivity);
    void deleteAllByIdIn(List<Long> activityIds);
    void deleteByGoalIdIn(List<Long> goalIds);
}
