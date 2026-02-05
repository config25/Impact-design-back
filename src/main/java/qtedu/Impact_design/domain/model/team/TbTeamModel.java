package qtedu.Impact_design.domain.model.team;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TbTeamModel {
    private final Integer teamId;
    private final String name;
    private final Integer sequence;
    private final Integer status;
    private final Integer aiPlay;
    private final String code;
    private final Integer isDoing;
    private final Integer teamGubun;
    private final Integer numUser;
}
