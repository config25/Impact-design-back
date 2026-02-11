package qtedu.Impact_design.api.dto.response.teach;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeletedTeamResponse {
    private Integer teamId;
    private String teamName;
    private Integer sequence;
}
