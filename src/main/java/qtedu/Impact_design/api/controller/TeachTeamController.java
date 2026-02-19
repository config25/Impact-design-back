package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qtedu.Impact_design.api.dto.request.teach.SaveStepRequest;
import qtedu.Impact_design.api.dto.request.teach.TeamMemberUpdateRequest;
import qtedu.Impact_design.api.dto.request.teach.TeamUpdateRequest;
import qtedu.Impact_design.api.dto.response.teach.DeletedTeamResponse;
import qtedu.Impact_design.api.dto.response.teach.TeamInfoResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.common.response.SuccessOnlyResponse;
import qtedu.Impact_design.domain.service.TeachTeamService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teach")
@RequiredArgsConstructor
public class TeachTeamController {

    private final TeachTeamService teachTeamService;

    /**
     * 단계(step) 설정 저장
     */
    @PostMapping("/step")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> saveStep(
            @RequestBody SaveStepRequest request
    ) {
        teachTeamService.saveStep(request.getGameId(), request.getStep());
        return ResponseHelper.successOnly();
    }

    /**
     * 팀 추가
     */
    @PostMapping("/team")
    public ResponseEntity<HttpResponse<Integer>> addTeam(
            @RequestParam Integer gameId
    ) {
        Integer teamId = teachTeamService.addTeam(gameId);
        return ResponseHelper.success(teamId);
    }

    /**
     * 평가팀 추가
     */
    @PostMapping("/evaluation-team")
    public ResponseEntity<HttpResponse<Integer>> addEvaluationTeam(
            @RequestParam Integer gameId
    ) {
        Integer teamId = teachTeamService.addEvaluationTeam(gameId);
        return ResponseHelper.success(teamId);
    }

    /**
     * 팀원 추가 (아이디/비번 자동생성, 동일값)
     */
    @PostMapping("/team/{teamId}/members")
    public ResponseEntity<HttpResponse<String>> addTeamMember(
            @PathVariable Integer teamId,
            @RequestParam Integer gameId
    ) {
        String loginId = teachTeamService.addTeamMember(teamId, gameId);
        return ResponseHelper.success(loginId);
    }

    /**
     * 팀 삭제 (소프트 삭제)
     */
    @DeleteMapping("/team/{teamId}")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> deleteTeam(
            @PathVariable Integer teamId,
            @RequestParam Integer gameId
    ) {
        teachTeamService.deleteTeam(teamId, gameId);
        return ResponseHelper.successOnly();
    }

    /**
     * 삭제된 팀 복원
     */
    @PostMapping("/team/{teamId}/restore")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> restoreTeam(
            @PathVariable Integer teamId,
            @RequestParam Integer gameId
    ) {
        teachTeamService.restoreTeam(teamId, gameId);
        return ResponseHelper.successOnly();
    }

    /**
     * 삭제된 팀 목록 조회
     */
    @GetMapping("/deleted-teams")
    public ResponseEntity<HttpResponse<List<DeletedTeamResponse>>> getDeletedTeams(
            @RequestParam Integer gameId
    ) {
        List<DeletedTeamResponse> response = teachTeamService.getDeletedTeams(gameId);
        return ResponseHelper.success(response);
    }

    /**
     * 학생 팀 이동
     */
    @PutMapping("/team-member")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> updateTeamMember(
            @RequestBody TeamMemberUpdateRequest request
    ) {
        teachTeamService.updateTeamMember(request.getUserId(), request.getTeamId());
        return ResponseHelper.successOnly();
    }

    /**
     * 팀 상세 정보 조회
     */
    @GetMapping("/team/{teamId}")
    public ResponseEntity<HttpResponse<TeamInfoResponse>> getTeamInfo(
            @PathVariable Integer teamId
    ) {
        TeamInfoResponse response = teachTeamService.getTeamInfo(teamId);
        return ResponseHelper.success(response);
    }

    /**
     * 팀 정보 수정
     */
    @PutMapping("/team/{teamId}")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> updateTeamInfo(
            @PathVariable Integer teamId,
            @RequestBody TeamUpdateRequest request
    ) {
        teachTeamService.updateTeamInfo(teamId, request.getTeamName(), request.getSequence(), request.getIsDoing(), request.getAiPlay());
        return ResponseHelper.successOnly();
    }

    /**
     * 선택한 팀원 삭제
     */
    @DeleteMapping("/team-members")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> deleteTeamMembers(
            @RequestBody Map<String, List<Long>> request
    ) {
        teachTeamService.deleteTeamMembers(request.get("userIds"));
        return ResponseHelper.successOnly();
    }

    /**
     * 대표작성자 지정 (CXINNO에 아직 미구현)
     */
    @PostMapping("/team/{teamId}/writer")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> setTeamWriter(
            @PathVariable Integer teamId,
            @RequestBody Map<String, Long> request
    ) {
        teachTeamService.setTeamWriter(teamId, request.get("userId"));
        return ResponseHelper.successOnly();
    }
}
