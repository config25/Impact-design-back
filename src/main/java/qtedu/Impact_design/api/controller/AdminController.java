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
import qtedu.Impact_design.domain.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 관리자용 - 진행중인 전체 강의실 목록 조회
     */
    @GetMapping("/teach")
    public ResponseEntity<HttpResponse<List<ClassInfoResponse>>> adminTeach(
            @CurrentUser UserId userId
    ) {
        List<ClassInfoResponse> response = adminService.getAllClassList(userId.getId());
        return ResponseHelper.success(response);
    }

    /**
     * 관리자용 - 전체 강의실 카테고리별 조회
     */
    @GetMapping("/teach/list")
    public ResponseEntity<HttpResponse<TeachListResponse>> adminTeachList(
            @CurrentUser UserId userId
    ) {
        TeachListResponse response = adminService.getAllTeachList(userId.getId());
        return ResponseHelper.success(response);
    }
}
