package qtedu.Impact_design.domain.implementation.funding;

import org.springframework.stereotype.Component;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;

@Component
public class FundingValidator {

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
