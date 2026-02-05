package qtedu.Impact_design.api.dto.request.flowcanvas;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FlowCanvasSaveRequest {
    private List<GoalItem> goals;

    @Getter
    @NoArgsConstructor
    public static class GoalItem {
        private Long goalId;
        private String goalTitle;
        private String goalDescription;
        private Integer orderNo;
        private List<TacticalItem> tacticals;
        private List<StrategicActivityItem> strategicActivities;
    }

    @Getter
    @NoArgsConstructor
    public static class TacticalItem {
        private Long metricId;
        private String tacticalMetric;
        private String tacticalGoal;
        private Integer orderNo;
    }

    @Getter
    @NoArgsConstructor
    public static class StrategicActivityItem {
        private Long activityId;
        private String activityMetric;
        private String interCriteria;
        private Integer orderNo;
    }
}
