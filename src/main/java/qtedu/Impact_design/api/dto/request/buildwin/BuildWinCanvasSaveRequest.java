package qtedu.Impact_design.api.dto.request.buildwin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import qtedu.Impact_design.domain.model.en.OutcomeType;

import java.util.List;

@Getter
@NoArgsConstructor
public class BuildWinCanvasSaveRequest {
    private String strategicGoal;
    private String taskName;
    private String taskDescription;
    private String crisisSignal;
    private String painTouchPoint;
    private List<TaskInputItem> taskInputs;
    private List<TaskActivityItem> taskActivities;
    private TeamworkItem teamwork;
    private List<TaskOutcomeItem> taskOutcomes;

    @Getter
    @NoArgsConstructor
    public static class TaskInputItem {
        private Long inputId;
        private String resourceName;
        private Integer quantity;
        private Integer orderNo;
    }

    @Getter
    @NoArgsConstructor
    public static class TaskActivityItem {
        private Long activityId;
        private String processStep;
        private String activityContent;
        private String duration;
        private Integer orderNo;
    }

    @Getter
    @NoArgsConstructor
    public static class TeamworkItem {
        private Long teamworkId;
        private String activityTeamwork;
        private String workType;
    }

    @Getter
    @NoArgsConstructor
    public static class TaskOutcomeItem {
        private Long outcomeNo;
        private OutcomeType outcomeType;
        private String outcomeContent;
        private Integer orderNo;
    }
}
