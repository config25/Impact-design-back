package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import qtedu.Impact_design.api.dto.request.teach.ClassSaveRequest;
import qtedu.Impact_design.api.dto.request.teach.NextStageRequest;
import qtedu.Impact_design.api.dto.response.teach.ClassInfoResponse;
import qtedu.Impact_design.api.dto.response.teach.StudentListResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachDetail2Response;
import qtedu.Impact_design.api.dto.response.teach.TeachDetailResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachListResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.common.response.SuccessOnlyResponse;
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

    /**
     * 클래스 상세 조회 (teach_detail)
     */
    @GetMapping("/detail")
    public ResponseEntity<HttpResponse<TeachDetailResponse>> teachDetail(
            @RequestParam Integer gameId
    ) {
        TeachDetailResponse response = teachService.getTeachDetail(gameId);
        return ResponseHelper.success(response);
    }

    /**
     * 클래스 상세 조회 2 (teach_detail2)
     * gameId가 없으면 첫 번째 진행중인 클래스 사용
     */
    @GetMapping("/detail2")
    public ResponseEntity<HttpResponse<TeachDetail2Response>> teachDetail2(
            @CurrentUser UserId userId,
            @RequestParam(required = false) Integer gameId
    ) {
        TeachDetail2Response response = teachService.getTeachDetail2(userId.getId(), gameId);
        return ResponseHelper.success(response);
    }

    /**
     * 학생 목록 조회 (student_list)
     */
    @GetMapping("/student-list")
    public ResponseEntity<HttpResponse<StudentListResponse>> studentList(
            @RequestParam Integer gameId
    ) {
        StudentListResponse response = teachService.getStudentList(gameId);
        return ResponseHelper.success(response);
    }

    /**
     * 클래스 생성 (teach_save)
     */
    @PostMapping(value = "/class", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpResponse<Integer>> createClass(
            @CurrentUser UserId userId,
            @RequestPart("request") ClassSaveRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        Integer gameId = teachService.createClass(userId.getId(), request, image);
        return ResponseHelper.success(gameId);
    }

    /**
     * 클래스 수정 (teach_save)
     */
    @PutMapping(value = "/class/{gameId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpResponse<Integer>> updateClass(
            @PathVariable Integer gameId,
            @RequestPart("request") ClassSaveRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        Integer updatedGameId = teachService.updateClass(gameId, request, image);
        return ResponseHelper.success(updatedGameId);
    }

    /**
     * 강의실 시작 (status: 1 → 10)
     */
    @PostMapping("/class/{gameId}/start")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> startClass(
            @PathVariable Integer gameId,
            @RequestBody NextStageRequest request
    ) {
        teachService.startClass(gameId, request.getEnddate());
        return ResponseHelper.successOnly();
    }

    /**
     * 강의실 종료 (status → 100, eStatus → 0)
     */
    @PostMapping("/class/{gameId}/end")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> endClass(
            @PathVariable Integer gameId
    ) {
        teachService.endClass(gameId);
        return ResponseHelper.successOnly();
    }

    /**
     * 강의실 복원 (status: 100 → 10)
     */
    @PostMapping("/class/{gameId}/restore")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> restoreClass(
            @PathVariable Integer gameId,
            @RequestBody NextStageRequest request
    ) {
        teachService.restoreClass(gameId, request.getEnddate());
        return ResponseHelper.successOnly();
    }

    /**
     * 다음 단계 시작 (새 미션 분기 생성 + 팀 연결 + 게임 상태 업데이트)
     * 아직 미연결(필터링으로 백엔드 다 뜯어고쳐야함)
     */
    @PostMapping("/class/{gameId}/next-stage")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> startNextStage(
            @PathVariable Integer gameId,
            @RequestBody NextStageRequest request
    ) {
        teachService.startNextStage(gameId, request.getEnddate());
        return ResponseHelper.successOnly();
    }

}
