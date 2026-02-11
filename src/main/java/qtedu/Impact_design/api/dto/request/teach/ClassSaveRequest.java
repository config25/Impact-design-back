package qtedu.Impact_design.api.dto.request.teach;

import lombok.Getter;

@Getter
public class ClassSaveRequest {
    private String name;
    private Integer numTeam;
    private Integer numMember;
    private Integer worldType;
    private Integer popupId;
    private String summary;
    private String classType;
    private String enddate;
}
