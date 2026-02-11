package qtedu.Impact_design.api.dto.response.funding;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FundingPortfolioResponse {
    private static final Long MAX_BUDGET = 100_000_000L;

    private List<PortfolioItem> investments;
    private Long totalInvested;
    private Long remainingBudget;
    private Long maxBudget;

    public static FundingPortfolioResponse from(List<PortfolioItem> investments) {
        Long totalInvested = investments.stream()
                .mapToLong(PortfolioItem::getInvestmentPrice)
                .sum();

        return FundingPortfolioResponse.builder()
                .investments(investments)
                .totalInvested(totalInvested)
                .remainingBudget(MAX_BUDGET - totalInvested)
                .maxBudget(MAX_BUDGET)
                .build();
    }

    @Getter
    @Builder
    public static class PortfolioItem {
        private Integer teamId;
        private String teamName;
        private Long investmentPrice;

        public static PortfolioItem from(Integer teamId, String teamName, Long investmentPrice) {
            return PortfolioItem.builder()
                    .teamId(teamId)
                    .teamName(teamName)
                    .investmentPrice(investmentPrice)
                    .build();
        }
    }
}
