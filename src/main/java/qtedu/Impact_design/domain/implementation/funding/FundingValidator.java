package qtedu.Impact_design.domain.implementation.funding;

import org.springframework.stereotype.Component;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;

@Component
public class FundingValidator {

    public void validateInvestmentInput(Integer investmentTarget, String investmentPrice,
                                        Integer score1, Integer score2, Integer score3,
                                        Integer score4, Integer score5, Integer score6,
                                        Integer score7, Integer score8, Integer score9) {
        if (investmentTarget == null) {
            throw new IllegalArgumentException("투자 대상은 필수입니다.");
        }
        if (investmentPrice == null || investmentPrice.isBlank()) {
            throw new IllegalArgumentException("투자 금액은 필수입니다.");
        }
        long price = parsePrice(investmentPrice);
        if (price <= 0) {
            throw new IllegalArgumentException("투자 금액은 0보다 커야 합니다.");
        }
        validateScoreRange("Problem-A1", score1);
        validateScoreRange("Problem-A2", score2);
        validateScoreRange("Solution-B1", score3);
        validateScoreRange("Solution-B2", score4);
        validateScoreRange("Solution-B3", score5);
        validateScoreRange("Scale up-C1", score6);
        validateScoreRange("Scale up-C2", score7);
        validateScoreRange("Effect-D1", score8);
        validateScoreRange("Effect-D2", score9);
    }

    private void validateScoreRange(String fieldName, Integer score) {
        if (score != null && score < 0) {
            throw new IllegalArgumentException(fieldName + " 점수는 0 이상이어야 합니다.");
        }
    }

    public void validateSubmitInput(Integer investmentTarget, String investmentPrice,
                                     Integer score1, Integer score2, Integer score3,
                                     Integer score4, Integer score5, Integer score6,
                                     Integer score7, Integer score8, Integer score9) {
        validateInvestmentInput(investmentTarget, investmentPrice,
                score1, score2, score3, score4, score5, score6, score7, score8, score9);
        requireScore("Problem-A1", score1);
        requireScore("Problem-A2", score2);
        requireScore("Solution-B1", score3);
        requireScore("Solution-B2", score4);
        requireScore("Solution-B3", score5);
        requireScore("Scale up-C1", score6);
        requireScore("Scale up-C2", score7);
        requireScore("Effect-D1", score8);
        requireScore("Effect-D2", score9);
    }

    private void requireScore(String fieldName, Integer score) {
        if (score == null) {
            throw new IllegalArgumentException(fieldName + " 점수는 필수입니다.");
        }
    }

    public void validateInvestmentLimit(long totalExcludingCurrent, String newPrice) {
        long newAmount = parsePrice(newPrice);
        if (totalExcludingCurrent + newAmount > 100_000_000L) {
            throw new ConflictException(ErrorCode.INVESTMENT_LIMIT_EXCEEDED);
        }
    }

    public long parsePrice(String price) {
        if (price == null || price.isBlank()) return 0L;
        try {
            return Long.parseLong(price.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
