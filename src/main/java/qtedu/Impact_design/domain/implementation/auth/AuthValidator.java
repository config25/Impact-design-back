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

    public void validateLoginInput(String loginId, String password) {
        if (loginId == null || loginId.isBlank()) {
            throw new IllegalArgumentException("로그인 ID는 필수입니다.");
        }
        if (loginId.length() > 50) {
            throw new IllegalArgumentException("로그인 ID는 50자 이하여야 합니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
    }

    public void validateSignupInput(String loginId, String password, String code) {
        validateLoginInput(loginId, password);
        if (password.length() > 100) {
            throw new IllegalArgumentException("비밀번호는 100자 이하여야 합니다.");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("참여 코드는 필수입니다.");
        }
    }

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
