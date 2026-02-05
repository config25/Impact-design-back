package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qtedu.Impact_design.api.dto.request.identitycanvas.IdentityCanvasRequest;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.service.IdentityCanvasService;

@RestController
@RequestMapping("/api/identity-canvas")
@RequiredArgsConstructor
public class IdentityCanvasController {

    private final IdentityCanvasService identityCanvasService;

    @GetMapping
    public ResponseEntity<HttpResponse<IdentityCanvasResponse>> getIdentityCanvas(
            @CurrentUser UserId userId
    ) {
        IdentityCanvasResponse response = identityCanvasService.getIdentityCanvas(userId.getId());
        return ResponseHelper.success(response);
    }

    @PostMapping
    public ResponseEntity<HttpResponse<IdentityCanvasResponse>> saveIdentityCanvas(
            @CurrentUser UserId userId,
            @RequestBody IdentityCanvasRequest request
    ) {
        IdentityCanvasResponse response = identityCanvasService.saveIdentityCanvas(userId.getId(), request);
        return ResponseHelper.success(response);
    }
}
