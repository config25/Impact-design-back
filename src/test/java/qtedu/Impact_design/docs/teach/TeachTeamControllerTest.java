package qtedu.Impact_design.docs.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.TeachTeamController;
import qtedu.Impact_design.api.dto.response.teach.DeletedTeamResponse;
import qtedu.Impact_design.api.dto.response.teach.TeamInfoResponse;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.service.TeachTeamService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TeachTeamController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class TeachTeamControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private TeachTeamService teachTeamService;

    // ===== 1. 단계(step) 설정 저장 =====

    @Test
    @DisplayName("단계(step) 설정 저장")
    void saveStep() throws Exception {
        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "gameId", 1,
                "step", "A-1,B-1,C-1,D-1,E-1"
        ));

        mockMvc.perform(post("/api/teach/step")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(document("teach-team/save-step",
                        requestFields(
                                fieldWithPath("gameId").description("강의실 ID"),
                                fieldWithPath("step").description("공개 단계 (쉼표 구분)")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공")
                        )
                ));
    }

    @Test
    @DisplayName("단계(step) 설정 저장 - 게임 없음")
    void saveStep_gameNotFound() throws Exception {
        willThrow(new NotFoundException(ErrorCode.GAME_NOT_FOUND))
                .given(teachTeamService).saveStep(anyInt(), any());

        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "gameId", 999,
                "step", "A-1,B-1"
        ));

        mockMvc.perform(post("/api/teach/step")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andDo(document("teach-team/save-step-game-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("GAME_1"),
                                fieldWithPath("data.message").description("게임을 찾을 수 없습니다.")
                        )
                ));
    }

    // ===== 2. 팀 추가 =====

    @Test
    @DisplayName("팀 추가")
    void addTeam() throws Exception {
        given(teachTeamService.addTeam(anyInt()))
                .willReturn(10);

        mockMvc.perform(post("/api/teach/team?gameId={gameId}", "1"))
                .andExpect(status().isCreated())
                .andDo(document("teach-team/add-team",
                        queryParameters(
                                parameterWithName("gameId").description("강의실 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data").description("생성된 팀 ID")
                        )
                ));
    }

    @Test
    @DisplayName("팀 추가 - 게임 없음")
    void addTeam_gameNotFound() throws Exception {
        given(teachTeamService.addTeam(anyInt()))
                .willThrow(new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        mockMvc.perform(post("/api/teach/team?gameId={gameId}", "999"))
                .andExpect(status().isNotFound())
                .andDo(document("teach-team/add-team-game-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("GAME_1"),
                                fieldWithPath("data.message").description("게임을 찾을 수 없습니다.")
                        )
                ));
    }

    @Test
    @DisplayName("팀 추가 - 최대 팀 수 초과")
    void addTeam_maxTeamExceeded() throws Exception {
        given(teachTeamService.addTeam(anyInt()))
                .willThrow(new ConflictException(ErrorCode.MAX_TEAM_EXCEEDED));

        mockMvc.perform(post("/api/teach/team?gameId={gameId}", "1"))
                .andExpect(status().isConflict())
                .andDo(document("teach-team/add-team-max-exceeded",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("TEAM_4"),
                                fieldWithPath("data.message").description("팀은 최대 6개까지 생성할 수 있습니다.")
                        )
                ));
    }

    // ===== 3. 평가팀 추가 =====

    @Test
    @DisplayName("평가팀 추가")
    void addEvaluationTeam() throws Exception {
        given(teachTeamService.addEvaluationTeam(anyInt()))
                .willReturn(11);

        mockMvc.perform(post("/api/teach/evaluation-team?gameId={gameId}", "1"))
                .andExpect(status().isCreated())
                .andDo(document("teach-team/add-evaluation-team",
                        queryParameters(
                                parameterWithName("gameId").description("강의실 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data").description("생성된 평가팀 ID")
                        )
                ));
    }

    @Test
    @DisplayName("평가팀 추가 - 최대 팀 수 초과")
    void addEvaluationTeam_maxTeamExceeded() throws Exception {
        given(teachTeamService.addEvaluationTeam(anyInt()))
                .willThrow(new ConflictException(ErrorCode.MAX_TEAM_EXCEEDED));

        mockMvc.perform(post("/api/teach/evaluation-team?gameId={gameId}", "1"))
                .andExpect(status().isConflict())
                .andDo(document("teach-team/add-evaluation-team-max-exceeded",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("TEAM_4"),
                                fieldWithPath("data.message").description("팀은 최대 6개까지 생성할 수 있습니다.")
                        )
                ));
    }

    // ===== 4. 팀원 추가 =====

    @Test
    @DisplayName("팀원 추가")
    void addTeamMember() throws Exception {
        given(teachTeamService.addTeamMember(anyInt(), anyInt()))
                .willReturn("a11");

        mockMvc.perform(post("/api/teach/team/{teamId}/members?gameId={gameId}", 1, "1"))
                .andExpect(status().isCreated())
                .andDo(document("teach-team/add-team-member",
                        pathParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        queryParameters(
                                parameterWithName("gameId").description("강의실 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data").description("생성된 로그인 ID")
                        )
                ));
    }

    @Test
    @DisplayName("팀원 추가 - 팀 없음")
    void addTeamMember_teamNotFound() throws Exception {
        given(teachTeamService.addTeamMember(anyInt(), anyInt()))
                .willThrow(new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        mockMvc.perform(post("/api/teach/team/{teamId}/members?gameId={gameId}", 999, "1"))
                .andExpect(status().isNotFound())
                .andDo(document("teach-team/add-team-member-team-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("TEAM_1"),
                                fieldWithPath("data.message").description("팀을 찾을 수 없습니다.")
                        )
                ));
    }

    @Test
    @DisplayName("팀원 추가 - 최대 팀원 수 초과")
    void addTeamMember_maxMemberExceeded() throws Exception {
        given(teachTeamService.addTeamMember(anyInt(), anyInt()))
                .willThrow(new ConflictException(ErrorCode.MAX_MEMBER_EXCEEDED));

        mockMvc.perform(post("/api/teach/team/{teamId}/members?gameId={gameId}", 1, "1"))
                .andExpect(status().isConflict())
                .andDo(document("teach-team/add-team-member-max-exceeded",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("TEAM_5"),
                                fieldWithPath("data.message").description("팀원은 최대 10명까지 추가할 수 있습니다.")
                        )
                ));
    }

    // ===== 5. 팀 삭제 =====

    @Test
    @DisplayName("팀 삭제")
    void deleteTeam() throws Exception {
        mockMvc.perform(delete("/api/teach/team/{teamId}?gameId={gameId}", 1, "1"))
                .andExpect(status().isOk())
                .andDo(document("teach-team/delete-team",
                        pathParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        queryParameters(
                                parameterWithName("gameId").description("강의실 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공")
                        )
                ));
    }

    @Test
    @DisplayName("팀 삭제 - 팀 없음")
    void deleteTeam_teamNotFound() throws Exception {
        willThrow(new NotFoundException(ErrorCode.TEAM_NOT_FOUND))
                .given(teachTeamService).deleteTeam(anyInt(), anyInt());

        mockMvc.perform(delete("/api/teach/team/{teamId}?gameId={gameId}", 999, "1"))
                .andExpect(status().isNotFound())
                .andDo(document("teach-team/delete-team-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("TEAM_1"),
                                fieldWithPath("data.message").description("팀을 찾을 수 없습니다.")
                        )
                ));
    }

    // ===== 6. 삭제된 팀 복원 =====

    @Test
    @DisplayName("삭제된 팀 복원")
    void restoreTeam() throws Exception {
        mockMvc.perform(post("/api/teach/team/{teamId}/restore?gameId={gameId}", 1, "1"))
                .andExpect(status().isOk())
                .andDo(document("teach-team/restore-team",
                        pathParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        queryParameters(
                                parameterWithName("gameId").description("강의실 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공")
                        )
                ));
    }

    @Test
    @DisplayName("삭제된 팀 복원 - 팀 없음")
    void restoreTeam_teamNotFound() throws Exception {
        willThrow(new NotFoundException(ErrorCode.TEAM_NOT_FOUND))
                .given(teachTeamService).restoreTeam(anyInt(), anyInt());

        mockMvc.perform(post("/api/teach/team/{teamId}/restore?gameId={gameId}", 999, "1"))
                .andExpect(status().isNotFound())
                .andDo(document("teach-team/restore-team-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("TEAM_1"),
                                fieldWithPath("data.message").description("팀을 찾을 수 없습니다.")
                        )
                ));
    }

    // ===== 7. 삭제된 팀 목록 조회 =====

    @Test
    @DisplayName("삭제된 팀 목록 조회")
    void getDeletedTeams() throws Exception {
        given(teachTeamService.getDeletedTeams(anyInt()))
                .willReturn(List.of(
                        DeletedTeamResponse.builder()
                                .teamId(5)
                                .teamName("팀5")
                                .sequence(5)
                                .build()
                ));

        mockMvc.perform(get("/api/teach/deleted-teams?gameId={gameId}", "1"))
                .andExpect(status().isOk())
                .andDo(document("teach-team/deleted-teams",
                        queryParameters(
                                parameterWithName("gameId").description("강의실 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data[].teamId").description("팀 ID"),
                                fieldWithPath("data[].teamName").description("팀 이름"),
                                fieldWithPath("data[].sequence").description("팀 순서")
                        )
                ));
    }

    // ===== 8. 학생 팀 이동 =====

    @Test
    @DisplayName("학생 팀 이동")
    void updateTeamMember() throws Exception {
        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "userId", 10,
                "teamId", 2
        ));

        mockMvc.perform(put("/api/teach/team-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(document("teach-team/update-team-member",
                        requestFields(
                                fieldWithPath("userId").description("이동할 학생 ID"),
                                fieldWithPath("teamId").description("이동할 팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공")
                        )
                ));
    }

    @Test
    @DisplayName("학생 팀 이동 - 대표작성자 이동 불가")
    void updateTeamMember_writerCannotMove() throws Exception {
        willThrow(new ConflictException(ErrorCode.WRITER_CANNOT_MOVE))
                .given(teachTeamService).updateTeamMember(anyLong(), anyInt());

        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "userId", 10,
                "teamId", 2
        ));

        mockMvc.perform(put("/api/teach/team-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andDo(document("teach-team/update-team-member-writer-cannot-move",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("TEAM_3"),
                                fieldWithPath("data.message").description("대표작성자는 팀을 이동할 수 없습니다.")
                        )
                ));
    }

    // ===== 9. 팀 상세 정보 조회 =====

    @Test
    @DisplayName("팀 상세 정보 조회")
    void getTeamInfo() throws Exception {
        TeamInfoResponse response = TeamInfoResponse.builder()
                .teamId(1)
                .teamName("팀1")
                .sequence(1)
                .isDoing(1)
                .teamCategory(0)
                .numUser(2)
                .members(List.of(
                        TeamInfoResponse.MemberInfo.builder()
                                .userId(10L)
                                .loginId("a11")
                                .name("홍길동")
                                .writer("1")
                                .build(),
                        TeamInfoResponse.MemberInfo.builder()
                                .userId(11L)
                                .loginId("a12")
                                .name("김철수")
                                .writer(null)
                                .build()
                ))
                .build();

        given(teachTeamService.getTeamInfo(anyInt()))
                .willReturn(response);

        mockMvc.perform(get("/api/teach/team/{teamId}", 1))
                .andExpect(status().isOk())
                .andDo(document("teach-team/get-team-info",
                        pathParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.teamId").description("팀 ID"),
                                fieldWithPath("data.teamName").description("팀 이름"),
                                fieldWithPath("data.sequence").description("팀 순서"),
                                fieldWithPath("data.isDoing").description("활성 여부"),
                                fieldWithPath("data.teamCategory").description("팀 카테고리 (0: 일반, 1: 평가팀) (DB는 팀 구분 컬럼)"),
                                fieldWithPath("data.numUser").description("팀원 수"),
                                fieldWithPath("data.members[]").description("팀원 목록"),
                                fieldWithPath("data.members[].userId").description("사용자 ID"),
                                fieldWithPath("data.members[].loginId").description("로그인 ID"),
                                fieldWithPath("data.members[].name").description("이름"),
                                fieldWithPath("data.members[].mail").description("이메일").optional(),
                                fieldWithPath("data.members[].writer").description("대표작성자 여부 (\"1\": 대표작성자)").optional()
                        )
                ));
    }

    @Test
    @DisplayName("팀 상세 정보 조회 - 팀 없음")
    void getTeamInfo_teamNotFound() throws Exception {
        given(teachTeamService.getTeamInfo(anyInt()))
                .willThrow(new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        mockMvc.perform(get("/api/teach/team/{teamId}", 999))
                .andExpect(status().isNotFound())
                .andDo(document("teach-team/get-team-info-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("TEAM_1"),
                                fieldWithPath("data.message").description("팀을 찾을 수 없습니다.")
                        )
                ));
    }

    // ===== 10. 팀 정보 수정 =====

    @Test
    @DisplayName("팀 정보 수정")
    void updateTeamInfo() throws Exception {
        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "teamName", "수정된 팀",
                "sequence", 2,
                "isDoing", 1,
                "aiPlay", 0
        ));

        mockMvc.perform(put("/api/teach/team/{teamId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(document("teach-team/update-team-info",
                        pathParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        requestFields(
                                fieldWithPath("teamName").description("팀 이름"),
                                fieldWithPath("sequence").description("팀 순서"),
                                fieldWithPath("isDoing").description("활성 여부"),
                                fieldWithPath("aiPlay").description("AI 플레이 여부")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공")
                        )
                ));
    }

    @Test
    @DisplayName("팀 정보 수정 - 팀 없음")
    void updateTeamInfo_teamNotFound() throws Exception {
        willThrow(new NotFoundException(ErrorCode.TEAM_NOT_FOUND))
                .given(teachTeamService).updateTeamInfo(anyInt(), any(), any(), any(), any());

        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "teamName", "수정된 팀"
        ));

        mockMvc.perform(put("/api/teach/team/{teamId}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andDo(document("teach-team/update-team-info-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("TEAM_1"),
                                fieldWithPath("data.message").description("팀을 찾을 수 없습니다.")
                        )
                ));
    }

    // ===== 11. 선택한 팀원 삭제 =====

    @Test
    @DisplayName("선택한 팀원 삭제")
    void deleteTeamMembers() throws Exception {
        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "userIds", List.of(10, 11)
        ));

        mockMvc.perform(delete("/api/teach/team-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(document("teach-team/delete-team-members",
                        requestFields(
                                fieldWithPath("userIds[]").description("삭제할 사용자 ID 목록")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공")
                        )
                ));
    }

    // ===== 12. 대표작성자 지정 =====

    @Test
    @DisplayName("대표작성자 지정")
    void setTeamWriter() throws Exception {
        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "userId", 10
        ));

        mockMvc.perform(post("/api/teach/team/{teamId}/writer", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(document("teach-team/set-writer",
                        pathParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("대표작성자로 지정할 사용자 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공")
                        )
                ));
    }

    @Test
    @DisplayName("대표작성자 지정 - 팀 소속 아님")
    void setTeamWriter_teamNotFound() throws Exception {
        willThrow(new NotFoundException(ErrorCode.TEAM_NOT_FOUND))
                .given(teachTeamService).setTeamWriter(anyInt(), anyLong());

        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "userId", 999
        ));

        mockMvc.perform(post("/api/teach/team/{teamId}/writer", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andDo(document("teach-team/set-writer-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("TEAM_2"),
                                fieldWithPath("data.message").description("팀을 찾을 수 없습니다.")
                        )
                ));
    }
}
