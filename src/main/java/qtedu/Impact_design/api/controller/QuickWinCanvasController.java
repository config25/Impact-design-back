package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qtedu.Impact_design.api.dto.request.wincanvas.WinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.quickwin.QuickWinCanvasResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.service.QuickWinCanvasService;

@RestController
@RequestMapping("/api/quick-win-canvas")
@RequiredArgsConstructor
public class QuickWinCanvasController {

    private final QuickWinCanvasService quickWinCanvasService;

    @GetMapping
    public ResponseEntity<HttpResponse<QuickWinCanvasResponse>> getQuickWinCanvas(
            @CurrentUser UserId userId
    ) {
        QuickWinCanvasResponse response = quickWinCanvasService.getQuickWinCanvas(userId.getId());
        return ResponseHelper.success(response);
    }

    @PostMapping
    public ResponseEntity<HttpResponse<QuickWinCanvasResponse>> saveQuickWinCanvas(
            @CurrentUser UserId userId,
            @RequestBody WinCanvasSaveRequest request
    ) {
        QuickWinCanvasResponse response = quickWinCanvasService.saveQuickWinCanvas(userId.getId(), request);
        return ResponseHelper.success(response);
    }

    @PatchMapping("/submit")
    public ResponseEntity<HttpResponse<QuickWinCanvasResponse>> submitQuickWinCanvas(
            @CurrentUser UserId userId,
            @RequestBody WinCanvasSaveRequest request
    ) {
        QuickWinCanvasResponse response = quickWinCanvasService.submitQuickWinCanvas(userId.getId(), request);
        return ResponseHelper.success(response);
    }
}
