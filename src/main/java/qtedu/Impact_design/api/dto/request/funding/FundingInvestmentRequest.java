package qtedu.Impact_design.api.dto.request.funding;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FundingInvestmentRequest {
    private Integer investmentTarget;  // 투자 대상 팀 ID
    private String investmentPrice;    // 투자 금액
    private Integer score1;            // Problem - A1
    private Integer score2;            // Problem - A2
    private Integer score3;            // Solution - B1
    private Integer score4;            // Solution - B2
    private Integer score5;            // Solution - B3
    private Integer score6;            // Scale up - C1
    private Integer score7;            // Scale up - C2
    private Integer score8;            // Effect - D1
    private Integer score9;            // Effect - D2
    private String opinion;            // 종합의견
    private Boolean submit;            // 제출 여부 (true = 제출, false = 임시저장)
}
