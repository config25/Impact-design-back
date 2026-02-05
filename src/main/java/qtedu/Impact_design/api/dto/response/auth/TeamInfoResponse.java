package qtedu.Impact_design.api.dto.response.auth;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.domain.model.team.TbTeamModel;

@Getter
@Builder
public class TeamInfoResponse {
    private Integer teamId;
    private String name;

    public static TeamInfoResponse from(TbTeamModel model) {
        return TeamInfoResponse.builder()
                .teamId(model.getTeamId())
                .name(model.getName())
                .build();
    }
}
