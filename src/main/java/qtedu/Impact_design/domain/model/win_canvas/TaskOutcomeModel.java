package qtedu.Impact_design.domain.model.win_canvas;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.domain.model.en.OutcomeType;

@Getter
@Builder
public class TaskOutcomeModel {
    private final Long outcomeNo;
    private final OutcomeType outcomeType;
    private final String outcomeContent;
    private final Integer orderNo;
    private final Long canvasId;
}
