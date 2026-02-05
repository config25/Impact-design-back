package qtedu.Impact_design.api.dto.request.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private String loginId;
    private String password;
    private String code;
    private Integer teamId;
}
