package qtedu.Impact_design.api.dto.response.quickwin;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.domain.model.en.OutcomeType;
import qtedu.Impact_design.domain.model.win_canvas.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class QuickWinCanvasResponse {
    private Long canvasId;
    private String strategicGoal;
    private String taskName;
    private String taskDescription;
    private String crisisSignal;
    private String painTouchPoint;
    private Long userId;
    private Boolean submitted;
    private List<TaskInputItem> taskInputs;
    private List<TaskActivityItem> taskActivities;
    private TeamworkItem teamwork;
    private List<TaskOutcomeItem> taskOutcomes;

    @Getter
    @Builder
    public static class TaskInputItem {
        private Long inputId;
        private String resourceName;
        private Integer quantity;
        private Integer orderNo;

        public static TaskInputItem from(TaskInputModel model) {
            return TaskInputItem.builder()
                    .inputId(model.getInputId())
                    .resourceName(model.getResourceName())
                    .quantity(model.getQuantity())
                    .orderNo(model.getOrderNo())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class TaskActivityItem {
        private Long activityId;
        private String processStep;
        private String activityContent;
        private String duration;
        private Integer orderNo;

        public static TaskActivityItem from(TaskActivityModel model) {
            return TaskActivityItem.builder()
                    .activityId(model.getActivityId())
                    .processStep(model.getProcessStep())
                    .activityContent(model.getActivityContent())
                    .duration(model.getDuration())
                    .orderNo(model.getOrderNo())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class TeamworkItem {
        private Long teamworkId;
        private String activityTeamwork;
        private String workType;

        public static TeamworkItem from(TeamworkModel model) {
            return TeamworkItem.builder()
                    .teamworkId(model.getTeamworkId())
                    .activityTeamwork(model.getActivityTeamwork())
                    .workType(model.getWorkType())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class TaskOutcomeItem {
        private Long outcomeNo;
        private OutcomeType outcomeType;
        private String outcomeContent;
        private Integer orderNo;

        public static TaskOutcomeItem from(TaskOutcomeModel model) {
            return TaskOutcomeItem.builder()
                    .outcomeNo(model.getOutcomeNo())
                    .outcomeType(model.getOutcomeType())
                    .outcomeContent(model.getOutcomeContent())
                    .orderNo(model.getOrderNo())
                    .build();
        }
    }

    public static QuickWinCanvasResponse from(WinCanvasModel canvas,
                                              List<TaskInputModel> inputs,
                                              List<TaskActivityModel> activities,
                                              TeamworkModel teamwork,
                                              List<TaskOutcomeModel> outcomes) {
        return QuickWinCanvasResponse.builder()
                .canvasId(canvas.getCanvasId())
                .strategicGoal(canvas.getStrategicGoal())
                .taskName(canvas.getTaskName())
                .taskDescription(canvas.getTaskDescription())
                .crisisSignal(canvas.getCrisisSignal())
                .painTouchPoint(canvas.getPainTouchPoint())
                .userId(canvas.getUserId())
                .submitted(canvas.getSubmitted())
                .taskInputs(inputs.stream().map(TaskInputItem::from).collect(Collectors.toList()))
                .taskActivities(activities.stream().map(TaskActivityItem::from).collect(Collectors.toList()))
                .teamwork(teamwork != null ? TeamworkItem.from(teamwork) : null)
                .taskOutcomes(outcomes.stream().map(TaskOutcomeItem::from).collect(Collectors.toList()))
                .build();
    }

}
