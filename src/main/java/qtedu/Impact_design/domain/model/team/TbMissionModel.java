package qtedu.Impact_design.domain.model.team;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TbMissionModel {
    private final Integer missionId;
    private final Integer sequence;
    private final String subject;
    private final String summary;
    private final LocalDateTime startdate;
    private final LocalDateTime enddate;
    private final Integer ddYear;
    private final Integer ddTerm;
    private final Integer mlevel;
    private final Integer gameId;
    private final String toinform;
}
