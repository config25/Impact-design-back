package qtedu.Impact_design.domain.implementation.funding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FundingValidatorTest {

    private final FundingValidator fundingValidator = new FundingValidator();

    @Nested
    @DisplayName("parsePrice")
    class ParsePrice {

        @Test
        @DisplayName("콤마가 포함된 금액을 정상 파싱한다")
        void parsesPriceWithCommas() {
            assertThat(fundingValidator.parsePrice("1,000,000")).isEqualTo(1_000_000L);
        }

        @Test
        @DisplayName("콤마 없는 금액을 정상 파싱한다")
        void parsesPriceWithoutCommas() {
            assertThat(fundingValidator.parsePrice("5000000")).isEqualTo(5_000_000L);
        }

        @Test
        @DisplayName("null이면 0을 반환한다")
        void returnsZeroForNull() {
            assertThat(fundingValidator.parsePrice(null)).isEqualTo(0L);
        }

        @Test
        @DisplayName("빈 문자열이면 0을 반환한다")
        void returnsZeroForBlank() {
            assertThat(fundingValidator.parsePrice("")).isEqualTo(0L);
            assertThat(fundingValidator.parsePrice("   ")).isEqualTo(0L);
        }

        @Test
        @DisplayName("숫자가 아닌 문자열이면 0을 반환한다")
        void returnsZeroForInvalidString() {
            assertThat(fundingValidator.parsePrice("abc")).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("validateInvestmentLimit")
    class ValidateInvestmentLimit {

        @Test
        @DisplayName("총 투자금이 1억 이하이면 예외가 발생하지 않는다")
        void doesNotThrowWhenWithinLimit() {
            fundingValidator.validateInvestmentLimit(90_000_000L, "10,000,000");
        }

        @Test
        @DisplayName("총 투자금이 1억을 초과하면 ConflictException이 발생한다")
        void throwsWhenExceedingLimit() {
            assertThatThrownBy(() ->
                    fundingValidator.validateInvestmentLimit(90_000_000L, "10,000,001"))
                    .isInstanceOf(ConflictException.class)
                    .satisfies(e -> assertThat(((ConflictException) e).getErrorCode())
                            .isEqualTo(ErrorCode.INVESTMENT_LIMIT_EXCEEDED));
        }

        @Test
        @DisplayName("새 금액이 null이면 기존 금액만으로 판단한다")
        void handlesNullNewPrice() {
            fundingValidator.validateInvestmentLimit(99_999_999L, null);
        }
    }
}
