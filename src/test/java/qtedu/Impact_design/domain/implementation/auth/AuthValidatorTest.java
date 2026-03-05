package qtedu.Impact_design.domain.implementation.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import qtedu.Impact_design.common.error.AuthorizationException;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.en.UserRole;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthValidatorTest {

    @InjectMocks
    private AuthValidator authValidator;

    @Mock
    private UserinfoRepository userinfoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("validatePassword")
    class ValidatePassword {

        @Test
        @DisplayName("비밀번호가 일치하면 예외가 발생하지 않는다")
        void doesNotThrowWhenPasswordMatches() {
            given(passwordEncoder.matches("raw", "encoded")).willReturn(true);

            assertDoesNotThrow(() -> authValidator.validatePassword("raw", "encoded"));
        }

        @Test
        @DisplayName("비밀번호가 불일치하면 AuthorizationException이 발생한다")
        void throwsWhenPasswordDoesNotMatch() {
            given(passwordEncoder.matches("wrong", "encoded")).willReturn(false);

            assertThatThrownBy(() -> authValidator.validatePassword("wrong", "encoded"))
                    .isInstanceOf(AuthorizationException.class)
                    .satisfies(e -> {
                        AuthorizationException ex = (AuthorizationException) e;
                        assert ex.getErrorCode() == ErrorCode.WRONG_PASSWORD;
                    });
        }
    }

    @Nested
    @DisplayName("validateStudentRole")
    class ValidateStudentRole {

        @Test
        @DisplayName("STUDENT 역할이면 예외가 발생하지 않는다")
        void doesNotThrowForStudent() {
            UserinfoModel student = UserinfoModel.builder().userRole(UserRole.STUDENT).build();

            assertDoesNotThrow(() -> authValidator.validateStudentRole(student));
        }

        @Test
        @DisplayName("STUDENT가 아니면 AuthorizationException이 발생한다")
        void throwsForNonStudent() {
            UserinfoModel teacher = UserinfoModel.builder().userRole(UserRole.TEACHER).build();

            assertThatThrownBy(() -> authValidator.validateStudentRole(teacher))
                    .isInstanceOf(AuthorizationException.class);
        }
    }

    @Nested
    @DisplayName("validateTeacherRole")
    class ValidateTeacherRole {

        @Test
        @DisplayName("TEACHER 역할이면 예외가 발생하지 않는다")
        void doesNotThrowForTeacher() {
            UserinfoModel teacher = UserinfoModel.builder().userRole(UserRole.TEACHER).build();

            assertDoesNotThrow(() -> authValidator.validateTeacherRole(teacher));
        }

        @Test
        @DisplayName("ADMIN 역할이면 예외가 발생하지 않는다")
        void doesNotThrowForAdmin() {
            UserinfoModel admin = UserinfoModel.builder().userRole(UserRole.ADMIN).build();

            assertDoesNotThrow(() -> authValidator.validateTeacherRole(admin));
        }

        @Test
        @DisplayName("STUDENT 역할이면 AuthorizationException이 발생한다")
        void throwsForStudent() {
            UserinfoModel student = UserinfoModel.builder().userRole(UserRole.STUDENT).build();

            assertThatThrownBy(() -> authValidator.validateTeacherRole(student))
                    .isInstanceOf(AuthorizationException.class);
        }
    }

    @Nested
    @DisplayName("checkLoginIdDuplicate")
    class CheckLoginIdDuplicate {

        @Test
        @DisplayName("중복되지 않은 loginId면 예외가 발생하지 않는다")
        void doesNotThrowForUniqueLoginId() {
            given(userinfoRepository.existsByLoginId("newUser")).willReturn(false);

            assertDoesNotThrow(() -> authValidator.checkLoginIdDuplicate("newUser"));
        }

        @Test
        @DisplayName("이미 존재하는 loginId면 ConflictException이 발생한다")
        void throwsForDuplicateLoginId() {
            given(userinfoRepository.existsByLoginId("existingUser")).willReturn(true);

            assertThatThrownBy(() -> authValidator.checkLoginIdDuplicate("existingUser"))
                    .isInstanceOf(ConflictException.class)
                    .satisfies(e -> {
                        ConflictException ex = (ConflictException) e;
                        assert ex.getErrorCode() == ErrorCode.USER_ALREADY_CREATED;
                    });
        }
    }
}
