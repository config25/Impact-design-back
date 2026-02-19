package qtedu.Impact_design.domain.model.team;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TbMissionDataModel {
    private final Integer missionDataId;
    private final Integer statusCeo;
    private final Integer statusMar;
    private final Integer statusPro;
    private final Integer statusFin;
    private final Integer statusCho;
    private final Integer teamId;
    private final Integer missionId;
}
