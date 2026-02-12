package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qtedu.Impact_design.api.dto.response.buildwin.BuildWinCanvasResponse;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.api.dto.response.funding.FundingInvestmentResponse;
import qtedu.Impact_design.api.dto.response.funding.FundingMyResultResponse;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.api.dto.response.quickwin.QuickWinCanvasResponse;
import qtedu.Impact_design.api.dto.response.teach.TeamSubmissionListResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.service.TeachSubmissionService;

import java.util.List;

/**
 * 강사용 제출물 열람 API
 */
@RestController
@RequestMapping("/api/teach/submission")
@RequiredArgsConstructor
public class TeachSubmissionController {

    private final TeachSubmissionService teachSubmissionService;

    /**
     * 게임별 팀 제출 현황 목록
     */
    @GetMapping("/list")
    public ResponseEntity<HttpResponse<List<TeamSubmissionListResponse>>> getSubmissionList(
            @RequestParam Integer gameId
    ) {
        List<TeamSubmissionListResponse> response = teachSubmissionService.getSubmissionList(gameId);
        return ResponseHelper.success(response);
    }

    /**
     * A: 성과관리 현황진단 (ImpactCheck) 열람
     */
    @GetMapping("/impact-check")
    public ResponseEntity<HttpResponse<ImpactCheckResponse>> getImpactCheck(
            @RequestParam Integer teamId
    ) {
        ImpactCheckResponse response = teachSubmissionService.getImpactCheck(teamId);
        return ResponseHelper.success(response);
    }

    /**
     * B: 정체성 설계 (IdentityCanvas) 열람
     */
    @GetMapping("/identity-canvas")
    public ResponseEntity<HttpResponse<IdentityCanvasResponse>> getIdentityCanvas(
            @RequestParam Integer teamId
    ) {
        IdentityCanvasResponse response = teachSubmissionService.getIdentityCanvas(teamId);
        return ResponseHelper.success(response);
    }

    /**
     * C: 성과경로 설계 (FlowCanvas) 열람
     */
    @GetMapping("/flow-canvas")
    public ResponseEntity<HttpResponse<FlowCanvasResponse>> getFlowCanvas(
            @RequestParam Integer teamId
    ) {
        FlowCanvasResponse response = teachSubmissionService.getFlowCanvas(teamId);
        return ResponseHelper.success(response);
    }

    /**
     * D: 전술적 실행과제 (WinCanvas - QUICK) 열람
     */
    @GetMapping("/quick-win")
    public ResponseEntity<HttpResponse<QuickWinCanvasResponse>> getQuickWinCanvas(
            @RequestParam Integer teamId
    ) {
        QuickWinCanvasResponse response = teachSubmissionService.getQuickWinCanvas(teamId);
        return ResponseHelper.success(response);
    }

    /**
     * E: 전략적 실행과제 (WinCanvas - BUILD) 열람
     */
    @GetMapping("/build-win")
    public ResponseEntity<HttpResponse<BuildWinCanvasResponse>> getBuildWinCanvas(
            @RequestParam Integer teamId
    ) {
        BuildWinCanvasResponse response = teachSubmissionService.getBuildWinCanvas(teamId);
        return ResponseHelper.success(response);
    }

    /**
     * F: 실행과제 검증 (FLetterOfIntent) 열람
     * canvasType: build(F-1) 또는 quick(F-2)
     */
    @GetMapping("/funding/{canvasType}")
    public ResponseEntity<HttpResponse<List<FundingInvestmentResponse>>> getFunding(
            @PathVariable String canvasType,
            @RequestParam Integer teamId
    ) {
        List<FundingInvestmentResponse> response = teachSubmissionService.getFunding(canvasType, teamId);
        return ResponseHelper.success(response);
    }

    /**
     * F-3: 팀별 결과 열람 (BUILD/QUICK 모두)
     */
    @GetMapping("/funding/result")
    public ResponseEntity<HttpResponse<FundingMyResultResponse>> getFundingResult(
            @RequestParam Integer teamId
    ) {
        FundingMyResultResponse response = teachSubmissionService.getFundingResult(teamId);
        return ResponseHelper.success(response);
    }
}
