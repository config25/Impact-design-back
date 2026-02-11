package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.request.funding.FundingInvestmentRequest;
import qtedu.Impact_design.api.dto.response.funding.*;
import qtedu.Impact_design.api.dto.response.funding.FundingMyResultResponse;
import qtedu.Impact_design.domain.implementation.funding.FundingAppender;
import qtedu.Impact_design.domain.implementation.funding.FundingReader;

@Service
@RequiredArgsConstructor
public class FundingService {

    private final FundingReader fundingReader;
    private final FundingAppender fundingAppender;

    public FundingTeamListResponse getTeamList(String canvasType, Long userId) {
        return fundingReader.getTeamList(canvasType, userId);
    }

    public FundingInvestmentResponse getInvestment(String canvasType, Long userId, Integer targetTeamId) {
        return fundingReader.getInvestment(canvasType, userId, targetTeamId);
    }

    public FundingInvestmentResponse saveInvestment(String canvasType, Long userId, FundingInvestmentRequest request) {
        return fundingAppender.saveInvestment(canvasType, userId, request);
    }

    public FundingPortfolioResponse getPortfolio(String canvasType, Long userId) {
        return fundingReader.getPortfolio(canvasType, userId);
    }

    public FundingScoresResponse getScores(String canvasType, Long userId) {
        return fundingReader.getScores(canvasType, userId);
    }

    public void submit(String canvasType, Long userId) {
        fundingAppender.submit(canvasType, userId);
    }

    public boolean isSubmitted(String canvasType, Long userId) {
        return fundingReader.isSubmitted(canvasType, userId);
    }

    public FundingMyResultResponse getMyResult(Long userId) {
        return fundingReader.getMyResult(userId);
    }
}
