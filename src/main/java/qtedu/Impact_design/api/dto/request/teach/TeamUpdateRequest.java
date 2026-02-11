package qtedu.Impact_design.api.dto.request.teach;

import lombok.Getter;

@Getter
public class TeamUpdateRequest {
    private String teamName;
    private Integer sequence;
    private Integer isDoing;
    private Integer aiPlay;
}
