package qtedu.Impact_design.domain.model.win_canvas;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamworkModel {
    private final Long teamworkId;
    private final String activityTeamwork;
    private final String workType;
    private final Long canvasId;
}
