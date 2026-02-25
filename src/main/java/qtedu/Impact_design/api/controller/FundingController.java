package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qtedu.Impact_design.api.dto.request.funding.FundingInvestmentRequest;
import qtedu.Impact_design.api.dto.response.funding.*;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.service.FundingService;

@RestController
@RequestMapping("/api/funding")
@RequiredArgsConstructor
public class FundingController {

    private final FundingService fundingService;

    /**
     * 투자 대상 팀 목록 조회 (내 팀 제외)
     * canvasType: build 또는 quick
     */
    @GetMapping("/{canvasType}/teams")
    public ResponseEntity<HttpResponse<FundingTeamListResponse>> getTeamList(
            @PathVariable String canvasType,
            @CurrentUser UserId userId
    ) {
        FundingTeamListResponse response = fundingService.getTeamList(canvasType, userId.getId());
        return ResponseHelper.success(response);
    }

    /**
     * 특정 팀에 대한 투자 정보 조회
     */
    @GetMapping("/{canvasType}/investment")
    public ResponseEntity<HttpResponse<FundingInvestmentResponse>> getInvestment(
            @PathVariable String canvasType,
            @CurrentUser UserId userId,
            @RequestParam Integer teamId
    ) {
        FundingInvestmentResponse response = fundingService.getInvestment(canvasType, userId.getId(), teamId);
        return ResponseHelper.success(response);
    }

    /**
     * 투자 의향서 저장/수정
     */
    @PostMapping("/{canvasType}/investment")
    public ResponseEntity<HttpResponse<FundingInvestmentResponse>> saveInvestment(
            @PathVariable String canvasType,
            @CurrentUser UserId userId,
            @RequestBody FundingInvestmentRequest request
    ) {
        FundingInvestmentResponse response = fundingService.saveInvestment(canvasType, userId.getId(), request);
        return ResponseHelper.success(response);
    }

    /**
     * 내 투자 포트폴리오 조회
     */
    @GetMapping("/{canvasType}/portfolio")
    public ResponseEntity<HttpResponse<FundingPortfolioResponse>> getPortfolio(
            @PathVariable String canvasType,
            @CurrentUser UserId userId
    ) {
        FundingPortfolioResponse response = fundingService.getPortfolio(canvasType, userId.getId());
        return ResponseHelper.success(response);
    }

    /**
     * 전체 팀 평가 점수 조회 (차트용)
     */
    @GetMapping("/{canvasType}/scores")
    public ResponseEntity<HttpResponse<FundingScoresResponse>> getScores(
            @PathVariable String canvasType,
            @CurrentUser UserId userId
    ) {
        FundingScoresResponse response = fundingService.getScores(canvasType, userId.getId());
        return ResponseHelper.success(response);
    }

    /**
     * 제출 완료
     */
    @PatchMapping("/{canvasType}/submit")
    public ResponseEntity<HttpResponse<FundingSubmitResponse>> submit(
            @PathVariable String canvasType,
            @CurrentUser UserId userId,
            @RequestBody FundingInvestmentRequest request
    ) {
        FundingSubmitResponse response = fundingService.submit(canvasType, userId.getId(), request);
        return ResponseHelper.success(response);
    }

    /**
     * 내 팀의 결과 조회 (F3 - BUILD/QUICK 모두 한번에)
     */
    @GetMapping("/my-result")
    public ResponseEntity<HttpResponse<FundingMyResultResponse>> getMyResult(
            @CurrentUser UserId userId
    ) {
        FundingMyResultResponse response = fundingService.getMyResult(userId.getId());
        return ResponseHelper.success(response);
    }
}
