package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qtedu.Impact_design.api.dto.request.wincanvas.WinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.wincanvas.WinCanvasResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.service.WinCanvasService;

@RestController
@RequiredArgsConstructor
public class WinCanvasController {

    private final WinCanvasService winCanvasService;

    // ── Quick Win Canvas ──

    @GetMapping("/api/quick-win-canvas")
    public ResponseEntity<HttpResponse<WinCanvasResponse>> getQuickWinCanvas(@CurrentUser UserId userId) {
        return ResponseHelper.success(winCanvasService.getWinCanvas(userId.getId(), CanvasType.QUICK));
    }

    @PostMapping("/api/quick-win-canvas")
    public ResponseEntity<HttpResponse<WinCanvasResponse>> saveQuickWinCanvas(
            @CurrentUser UserId userId, @RequestBody WinCanvasSaveRequest request) {
        return ResponseHelper.success(winCanvasService.saveWinCanvas(userId.getId(), request, CanvasType.QUICK));
    }

    @PatchMapping("/api/quick-win-canvas/submit")
    public ResponseEntity<HttpResponse<WinCanvasResponse>> submitQuickWinCanvas(
            @CurrentUser UserId userId, @RequestBody WinCanvasSaveRequest request) {
        return ResponseHelper.success(winCanvasService.submitWinCanvas(userId.getId(), request, CanvasType.QUICK));
    }

    // ── Build Win Canvas ──

    @GetMapping("/api/build-win-canvas")
    public ResponseEntity<HttpResponse<WinCanvasResponse>> getBuildWinCanvas(@CurrentUser UserId userId) {
        return ResponseHelper.success(winCanvasService.getWinCanvas(userId.getId(), CanvasType.BUILD));
    }

    @PostMapping("/api/build-win-canvas")
    public ResponseEntity<HttpResponse<WinCanvasResponse>> saveBuildWinCanvas(
            @CurrentUser UserId userId, @RequestBody WinCanvasSaveRequest request) {
        return ResponseHelper.success(winCanvasService.saveWinCanvas(userId.getId(), request, CanvasType.BUILD));
    }

    @PatchMapping("/api/build-win-canvas/submit")
    public ResponseEntity<HttpResponse<WinCanvasResponse>> submitBuildWinCanvas(
            @CurrentUser UserId userId, @RequestBody WinCanvasSaveRequest request) {
        return ResponseHelper.success(winCanvasService.submitWinCanvas(userId.getId(), request, CanvasType.BUILD));
    }
}
