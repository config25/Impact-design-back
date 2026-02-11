package qtedu.Impact_design.domain.model.flow_canvas;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FlowCanvasModel {
    private final Long goalId;
    private final String goalTitle;
    private final String goalDescription;
    private final Integer orderNo;
    private final Long userId;
    private final Boolean submitted;
}
