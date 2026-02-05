package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qtedu.Impact_design.api.dto.response.teach.ClassInfoResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachListResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.service.TeachService;

import java.util.List;

@RestController
@RequestMapping("/api/teach")
@RequiredArgsConstructor
public class TeachController {

    private final TeachService teachService;

    @GetMapping
    public ResponseEntity<HttpResponse<List<ClassInfoResponse>>> teach(
            @CurrentUser UserId userId
    ) {
        List<ClassInfoResponse> response = teachService.getTeachIndex(userId.getId());
        return ResponseHelper.success(response);
    }

    @GetMapping("/list")
    public ResponseEntity<HttpResponse<TeachListResponse>> teachList(
            @CurrentUser UserId userId
    ) {
        TeachListResponse response = teachService.getTeachList(userId.getId());
        return ResponseHelper.success(response);
    }
}
