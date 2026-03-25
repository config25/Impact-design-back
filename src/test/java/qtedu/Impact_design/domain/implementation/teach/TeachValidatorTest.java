package qtedu.Impact_design.domain.implementation.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

class TeachValidatorTest {

    private final TeachValidator teachValidator = new TeachValidator();

    @Nested
    @DisplayName("validateClassSaveInput")
    class ValidateClassSaveInput {

        @Test
        @DisplayName("정상 입력이면 예외가 발생하지 않는다")
        void validInput() {
            assertThatNoException()
                    .isThrownBy(() -> teachValidator.validateClassSaveInput("수업명", 3, 5));
        }

        @Test
        @DisplayName("이름이 null이면 예외가 발생한다")
        void nullName() {
            assertThatThrownBy(() -> teachValidator.validateClassSaveInput(null, 3, 5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("클래스 이름은 필수");
        }

        @Test
        @DisplayName("이름이 빈 문자열이면 예외가 발생한다")
        void blankName() {
            assertThatThrownBy(() -> teachValidator.validateClassSaveInput("  ", 3, 5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("클래스 이름은 필수");
        }

        @Test
        @DisplayName("이름이 100자를 초과하면 예외가 발생한다")
        void nameTooLong() {
            String longName = "a".repeat(101);
            assertThatThrownBy(() -> teachValidator.validateClassSaveInput(longName, 3, 5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자 이하");
        }

        @Test
        @DisplayName("팀 수가 null이면 예외가 발생한다")
        void nullNumTeam() {
            assertThatThrownBy(() -> teachValidator.validateClassSaveInput("수업명", null, 5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("팀 수는 1 이상");
        }

        @Test
        @DisplayName("팀 수가 0이면 예외가 발생한다")
        void zeroNumTeam() {
            assertThatThrownBy(() -> teachValidator.validateClassSaveInput("수업명", 0, 5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("팀 수는 1 이상");
        }

        @Test
        @DisplayName("팀 수가 6을 초과하면 예외가 발생한다")
        void numTeamExceedsMax() {
            assertThatThrownBy(() -> teachValidator.validateClassSaveInput("수업명", 7, 5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최대 6개");
        }

        @Test
        @DisplayName("팀원 수가 10을 초과하면 예외가 발생한다")
        void numMemberExceedsMax() {
            assertThatThrownBy(() -> teachValidator.validateClassSaveInput("수업명", 3, 11))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최대 10명");
        }

        @Test
        @DisplayName("경계값 - 팀 6개, 팀원 10명은 통과한다")
        void boundaryValues() {
            assertThatNoException()
                    .isThrownBy(() -> teachValidator.validateClassSaveInput("수업명", 6, 10));
        }
    }

    @Nested
    @DisplayName("validateClassUpdateInput")
    class ValidateClassUpdateInput {

        @Test
        @DisplayName("정상 입력이면 예외가 발생하지 않는다")
        void validInput() {
            assertThatNoException()
                    .isThrownBy(() -> teachValidator.validateClassUpdateInput("수업명"));
        }

        @Test
        @DisplayName("이름이 null이면 예외가 발생한다")
        void nullName() {
            assertThatThrownBy(() -> teachValidator.validateClassUpdateInput(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("이름이 100자를 초과하면 예외가 발생한다")
        void nameTooLong() {
            assertThatThrownBy(() -> teachValidator.validateClassUpdateInput("a".repeat(101)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("validateEnddate")
    class ValidateEnddate {

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void nullEnddate() {
            assertThatThrownBy(() -> teachValidator.validateEnddate(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("종료일은 필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void blankEnddate() {
            assertThatThrownBy(() -> teachValidator.validateEnddate("  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("종료일은 필수");
        }

        @Test
        @DisplayName("잘못된 형식이면 예외가 발생한다")
        void invalidFormat() {
            assertThatThrownBy(() -> teachValidator.validateEnddate("2026/04/01"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("yyyy-MM-dd HH:mm");
        }

        @Test
        @DisplayName("과거 날짜이면 예외가 발생한다")
        void pastDate() {
            assertThatThrownBy(() -> teachValidator.validateEnddate("2020-01-01 00:00"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("현재 시간 이후");
        }

        @Test
        @DisplayName("미래 날짜이면 예외가 발생하지 않는다")
        void futureDate() {
            assertThatNoException()
                    .isThrownBy(() -> teachValidator.validateEnddate("2099-12-31 23:59"));
        }
    }
}
