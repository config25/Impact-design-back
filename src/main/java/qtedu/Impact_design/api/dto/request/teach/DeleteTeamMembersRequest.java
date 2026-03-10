package qtedu.Impact_design.api.dto.request.teach;

import lombok.Getter;

import java.util.List;

@Getter
public class DeleteTeamMembersRequest {
    private List<Long> userIds;
}
