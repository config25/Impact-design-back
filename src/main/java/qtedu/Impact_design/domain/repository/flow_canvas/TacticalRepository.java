package qtedu.Impact_design.domain.repository.flow_canvas;

import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;

import java.util.List;
import java.util.Optional;

public interface TacticalRepository {
    List<TacticalModel> findByGoalIdIn(List<Long> goalIds);
    Optional<TacticalModel> findByGoalIdAndOrderNo(Long goalId, Integer orderNo);
    TacticalModel save(TacticalModel tactical);
    void deleteAllByIdIn(List<Long> metricIds);
    void deleteByGoalIdIn(List<Long> goalIds);
}
