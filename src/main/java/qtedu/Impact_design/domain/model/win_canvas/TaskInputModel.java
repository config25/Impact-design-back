package qtedu.Impact_design.domain.model.win_canvas;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskInputModel {
    private final Long inputId;
    private final String resourceName;
    private final Integer quantity;
    private final Integer orderNo;
    private final Long canvasId;
}
