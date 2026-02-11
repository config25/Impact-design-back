package qtedu.Impact_design.api.dto.response.funding;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.domain.model.FLetterOfIntent2Model;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;

@Getter
@Builder
public class FundingInvestmentResponse {
    private Integer intentIndex;
    private Integer investmentTarget;
    private String teamName;
    private String businessName;
    private String investmentPrice;
    private Integer score1;
    private Integer score2;
    private Integer score3;
    private Integer score4;
    private Integer score5;
    private Integer score6;
    private Integer score7;
    private Integer score8;
    private Integer score9;
    private String opinion;
    private Boolean submitted;

    public static FundingInvestmentResponse from(FLetterOfIntentModel model, String teamName, String businessName) {
        return FundingInvestmentResponse.builder()
                .intentIndex(model.getIntentIndex())
                .investmentTarget(Integer.parseInt(model.getInvestmentTarget()))
                .teamName(teamName)
                .businessName(businessName)
                .investmentPrice(model.getInvestmentPrice())
                .score1(model.getScore1())
                .score2(model.getScore2())
                .score3(model.getScore3())
                .score4(model.getScore4())
                .score5(model.getScore5())
                .score6(model.getScore6())
                .score7(model.getScore7())
                .score8(model.getScore8())
                .score9(model.getScore9())
                .opinion(model.getOpinion())
                .submitted(Boolean.TRUE.equals(model.getSubmitted()))
                .build();
    }

    public static FundingInvestmentResponse from(FLetterOfIntent2Model model, String teamName, String businessName) {
        return FundingInvestmentResponse.builder()
                .intentIndex(model.getIntentIndex())
                .investmentTarget(Integer.parseInt(model.getInvestmentTarget()))
                .teamName(teamName)
                .businessName(businessName)
                .investmentPrice(model.getInvestmentPrice())
                .score1(model.getScore1())
                .score2(model.getScore2())
                .score3(model.getScore3())
                .score4(model.getScore4())
                .score5(model.getScore5())
                .score6(model.getScore6())
                .score7(model.getScore7())
                .score8(model.getScore8())
                .score9(model.getScore9())
                .opinion(model.getOpinion())
                .submitted(Boolean.TRUE.equals(model.getSubmitted()))
                .build();
    }

    public static FundingInvestmentResponse empty(Integer teamId, String teamName, String businessName) {
        return FundingInvestmentResponse.builder()
                .investmentTarget(teamId)
                .teamName(teamName)
                .businessName(businessName)
                .investmentPrice("0")
                .score1(5)
                .score2(5)
                .score3(10)
                .score4(10)
                .score5(10)
                .score6(10)
                .score7(20)
                .score8(10)
                .score9(20)
                .opinion("")
                .submitted(false)
                .build();
    }
}
