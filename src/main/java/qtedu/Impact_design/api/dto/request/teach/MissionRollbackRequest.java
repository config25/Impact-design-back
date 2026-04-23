package qtedu.Impact_design.api.dto.request.teach;

import lombok.Getter;
import qtedu.Impact_design.domain.model.en.RollbackStage;

@Getter
public class MissionRollbackRequest {
    private Integer teamId;
    private RollbackStage stage;
}
