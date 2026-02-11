package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qtedu.Impact_design.api.dto.request.impactcheck.ImpactCheckRequest;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.service.ImpactCheckService;

@RestController
@RequestMapping("/api/impact-check")
@RequiredArgsConstructor
public class ImpactCheckController {

    private final ImpactCheckService impactCheckService;

    @GetMapping
    public ResponseEntity<HttpResponse<ImpactCheckResponse>> getImpactCheck(
            @CurrentUser UserId userId
    ) {
        ImpactCheckResponse response = impactCheckService.getImpactCheck(userId.getId());
        return ResponseHelper.success(response);
    }

    @PostMapping
    public ResponseEntity<HttpResponse<ImpactCheckResponse>> saveImpactCheck(
            @CurrentUser UserId userId,
            @RequestBody ImpactCheckRequest request
    ) {
        ImpactCheckResponse response = impactCheckService.saveImpactCheck(userId.getId(), request);
        return ResponseHelper.success(response);
    }

    @PatchMapping("/submit")
    public ResponseEntity<HttpResponse<ImpactCheckResponse>> submitImpactCheck(
            @CurrentUser UserId userId
    ) {
        ImpactCheckResponse response = impactCheckService.submitImpactCheck(userId.getId());
        return ResponseHelper.success(response);
    }
}
