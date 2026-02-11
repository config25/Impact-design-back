package qtedu.Impact_design.domain.model.win_canvas;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.domain.model.en.CanvasType;

@Getter
@Builder
public class WinCanvasModel {
    private final Long canvasId;
    private final CanvasType canvasType;
    private final String strategicGoal;
    private final String taskName;
    private final String taskDescription;
    private final String crisisSignal;
    private final String painTouchPoint;
    private final Long userId;
    private final Boolean submitted;
}
