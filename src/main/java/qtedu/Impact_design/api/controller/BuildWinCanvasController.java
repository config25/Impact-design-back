package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qtedu.Impact_design.api.dto.request.buildwin.BuildWinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.buildwin.BuildWinCanvasResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.service.BuildWinCanvasService;

@RestController
@RequestMapping("/api/build-win-canvas")
@RequiredArgsConstructor
public class BuildWinCanvasController {

    private final BuildWinCanvasService buildWinCanvasService;

    @GetMapping
    public ResponseEntity<HttpResponse<BuildWinCanvasResponse>> getBuildWinCanvas(
            @CurrentUser UserId userId
    ) {
        BuildWinCanvasResponse response = buildWinCanvasService.getBuildWinCanvas(userId.getId());
        return ResponseHelper.success(response);
    }

    @PostMapping
    public ResponseEntity<HttpResponse<BuildWinCanvasResponse>> saveBuildWinCanvas(
            @CurrentUser UserId userId,
            @RequestBody BuildWinCanvasSaveRequest request
    ) {
        BuildWinCanvasResponse response = buildWinCanvasService.saveBuildWinCanvas(userId.getId(), request);
        return ResponseHelper.success(response);
    }

    @PatchMapping("/submit")
    public ResponseEntity<HttpResponse<BuildWinCanvasResponse>> submitBuildWinCanvas(
            @CurrentUser UserId userId,
            @RequestBody BuildWinCanvasSaveRequest request
    ) {
        BuildWinCanvasResponse response = buildWinCanvasService.submitBuildWinCanvas(userId.getId(), request);
        return ResponseHelper.success(response);
    }
}
