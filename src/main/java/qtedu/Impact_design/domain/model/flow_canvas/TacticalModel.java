package qtedu.Impact_design.domain.model.flow_canvas;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TacticalModel {
    private final Long metricId;
    private final String tacticalMetric;
    private final String tacticalGoal;
    private final Integer orderNo;
    private final Long goalId;
}
