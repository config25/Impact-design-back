package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qtedu.Impact_design.api.dto.request.flowcanvas.FlowCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.service.FlowCanvasService;

@RestController
@RequestMapping("/api/flow-canvas")
@RequiredArgsConstructor
public class FlowCanvasController {

    private final FlowCanvasService flowCanvasService;

    @GetMapping
    public ResponseEntity<HttpResponse<FlowCanvasResponse>> getFlowCanvas(
            @CurrentUser UserId userId
    ) {
        FlowCanvasResponse response = flowCanvasService.getFlowCanvas(userId.getId());
        return ResponseHelper.success(response);
    }

    @PostMapping
    public ResponseEntity<HttpResponse<FlowCanvasResponse>> saveFlowCanvas(
            @CurrentUser UserId userId,
            @RequestBody FlowCanvasSaveRequest request
    ) {
        FlowCanvasResponse response = flowCanvasService.saveFlowCanvas(userId.getId(), request);
        return ResponseHelper.success(response);
    }

    @PatchMapping("/submit")
    public ResponseEntity<HttpResponse<FlowCanvasResponse>> submitFlowCanvas(
            @CurrentUser UserId userId
    ) {
        FlowCanvasResponse response = flowCanvasService.submitFlowCanvas(userId.getId());
        return ResponseHelper.success(response);
    }
}
