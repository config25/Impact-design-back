package qtedu.Impact_design.api.dto.response.teach;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeachListResponse {
    private List<ClassInfoResponse> inProgress;
    private List<ClassInfoResponse> setting;
    private List<ClassInfoResponse> completed;
    private List<ClassInfoResponse> etc;
}
