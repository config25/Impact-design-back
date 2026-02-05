package qtedu.Impact_design.domain.model.win_canvas;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskActivityModel {
    private final Long activityId;
    private final String processStep;
    private final String activityContent;
    private final String duration;
    private final Integer orderNo;
    private final Long canvasId;
}
