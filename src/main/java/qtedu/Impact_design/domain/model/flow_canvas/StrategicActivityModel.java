package qtedu.Impact_design.domain.model.flow_canvas;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StrategicActivityModel {
    private final Long activityId;
    private final String activityMetric;
    private final String interCriteria;
    private final Integer orderNo;
    private final Long goalId;
}
