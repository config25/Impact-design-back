package qtedu.Impact_design.domain.implementation.funding;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.request.funding.FundingInvestmentRequest;
import qtedu.Impact_design.api.dto.response.funding.FundingSubmitResponse;

@Component
@RequiredArgsConstructor
public class FundingUpdater {

    private final FundingAppender fundingAppender;

    @Transactional
    public FundingSubmitResponse submit(String canvasType, Long userId, FundingInvestmentRequest request) {
        fundingAppender.saveInvestment(canvasType, userId, request);
        return FundingSubmitResponse.builder().submitted(true).build();
    }
}
