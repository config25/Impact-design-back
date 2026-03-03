package qtedu.Impact_design.api.dto.response.game;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentDashboardResponse {
    private String className;
    private String classImage;
    private String teamName;
    private String userName;
}
