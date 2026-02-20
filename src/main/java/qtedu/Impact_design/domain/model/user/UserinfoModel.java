package qtedu.Impact_design.domain.model.user;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.domain.model.en.UserRole;

@Getter
@Builder
public class UserinfoModel {
    private final Long userId;
    private final String loginId;
    private final String password;
    private final String userName;
    private final String code;
    private final UserRole userRole;
    private final String writer;

    public static UserinfoModel createStudent(String loginId, String encodedPassword, String code) {
        return UserinfoModel.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .userName(loginId)
                .code(code)
                .userRole(UserRole.STUDENT)
                .writer("0")
                .build();
    }

    public boolean isWriter() {
        return "1".equals(writer);
    }
}
