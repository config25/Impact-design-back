package qtedu.Impact_design.api.dto.response.flowcanvas;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;
import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class FlowCanvasResponse {
    private String newVision;
    private List<GoalItem> goals;
    private Boolean submitted;

    @Getter
    @Builder
    public static class GoalItem {
        private Long goalId;
        private String goalTitle;
        private String goalDescription;
        private Integer orderNo;
        private List<TacticalItem> tacticals;
        private List<StrategicActivityItem> strategicActivities;

        public static GoalItem from(FlowCanvasModel model, List<TacticalModel> tacticals, List<StrategicActivityModel> activities) {
            return GoalItem.builder()
                    .goalId(model.getGoalId())
                    .goalTitle(model.getGoalTitle())
                    .goalDescription(model.getGoalDescription())
                    .orderNo(model.getOrderNo())
                    .tacticals(tacticals.stream().map(TacticalItem::from).collect(Collectors.toList()))
                    .strategicActivities(activities.stream().map(StrategicActivityItem::from).collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class TacticalItem {
        private Long metricId;
        private String tacticalMetric;
        private String tacticalGoal;
        private Integer orderNo;

        public static TacticalItem from(TacticalModel model) {
            return TacticalItem.builder()
                    .metricId(model.getMetricId())
                    .tacticalMetric(model.getTacticalMetric())
                    .tacticalGoal(model.getTacticalGoal())
                    .orderNo(model.getOrderNo())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StrategicActivityItem {
        private Long activityId;
        private String activityMetric;
        private String interCriteria;
        private Integer orderNo;

        public static StrategicActivityItem from(StrategicActivityModel model) {
            return StrategicActivityItem.builder()
                    .activityId(model.getActivityId())
                    .activityMetric(model.getActivityMetric())
                    .interCriteria(model.getInterCriteria())
                    .orderNo(model.getOrderNo())
                    .build();
        }
    }
}
