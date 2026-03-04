package qtedu.Impact_design.domain.implementation.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.common.error.AuthorizationException;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.en.UserRole;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final UserinfoRepository userinfoRepository;
    private final PasswordEncoder passwordEncoder;

    public void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new AuthorizationException(ErrorCode.WRONG_PASSWORD);
        }
    }

    public void validateStudentRole(UserinfoModel userinfo) {
        if (userinfo.getUserRole() != UserRole.STUDENT) {
            throw new AuthorizationException(ErrorCode.ACCESS_DENIED);
        }
    }

    public void validateTeacherRole(UserinfoModel userinfo) {
        if (userinfo.getUserRole() != UserRole.ADMIN && userinfo.getUserRole() != UserRole.TEACHER) {
            throw new AuthorizationException(ErrorCode.ACCESS_DENIED);
        }
    }

    public void checkLoginIdDuplicate(String loginId) {
        if (userinfoRepository.existsByLoginId(loginId)) {
            throw new ConflictException(ErrorCode.USER_ALREADY_CREATED);
        }
    }
}
