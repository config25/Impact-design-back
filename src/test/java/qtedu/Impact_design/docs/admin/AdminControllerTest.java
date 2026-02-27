package qtedu.Impact_design.docs.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.AdminController;
import qtedu.Impact_design.api.dto.response.teach.ClassInfoResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachListResponse;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.common.error.AuthorizationException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.service.AdminService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AdminController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class AdminControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private AdminService adminService;

    private ClassInfoResponse sampleClassInfo() {
        return ClassInfoResponse.builder()
                .gameId(1)
                .name("임팩트디자인 1반")
                .numTeam(4)
                .totalDd(100)
                .missionId(3)
                .sequence(2)
                .subject("사회문제 해결")
                .summary("임팩트디자인 수업입니다.")
                .startdate(LocalDateTime.of(2026, 3, 1, 9, 0))
                .enddate(LocalDateTime.of(2026, 6, 30, 18, 0))
                .ddYear(2026)
                .ddTerm(1)
                .statusCeo(null)
                .imageUrl("http://iptdesign.mycafe24.com/uploads/class/1/image.png")
                .build();
    }

    // ===== 1. 진행중인 전체 강의실 목록 조회 =====

    @Test
    @DisplayName("관리자 - 진행중인 전체 강의실 목록 조회")
    void adminTeach() throws Exception {
        authenticateAs(1L);
        given(adminService.getAllClassList(anyLong()))
                .willReturn(List.of(sampleClassInfo()));

        mockMvc.perform(get("/api/admin/teach"))
                .andExpect(status().isOk())
                .andDo(document("admin/teach",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data[].gameId").description("강의실 ID"),
                                fieldWithPath("data[].name").description("강의실 이름"),
                                fieldWithPath("data[].numTeam").description("팀 수"),
                                fieldWithPath("data[].totalDd").description("총 의사결정일 수"),
                                fieldWithPath("data[].missionId").description("현재 미션 ID"),
                                fieldWithPath("data[].sequence").description("미션 순서 번호"),
                                fieldWithPath("data[].subject").description("미션 제목"),
                                fieldWithPath("data[].summary").description("미션 요약 설명"),
                                fieldWithPath("data[].startdate").description("미션 시작일시"),
                                fieldWithPath("data[].enddate").description("미션 종료일시"),
                                fieldWithPath("data[].ddYear").description("의사결정 연도"),
                                fieldWithPath("data[].ddTerm").description("의사결정 분기/기간"),
                                fieldWithPath("data[].statusCeo").description("CEO 상태").optional(),
                                fieldWithPath("data[].imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    @Test
    @DisplayName("관리자 - 진행중인 전체 강의실 목록 조회 - 사용자 없음")
    void adminTeach_userNotFound() throws Exception {
        authenticateAs(1L);
        given(adminService.getAllClassList(anyLong()))
                .willThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(get("/api/admin/teach"))
                .andExpect(status().isNotFound())
                .andDo(document("admin/teach-user-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("USER_1"),
                                fieldWithPath("data.message").description("회원을 찾을 수 없음.")
                        )
                ));
    }

    @Test
    @DisplayName("관리자 - 진행중인 전체 강의실 목록 조회 - 권한 없음")
    void adminTeach_accessDenied() throws Exception {
        authenticateAs(1L);
        given(adminService.getAllClassList(anyLong()))
                .willThrow(new AuthorizationException(ErrorCode.ACCESS_DENIED));

        mockMvc.perform(get("/api/admin/teach"))
                .andExpect(status().isUnauthorized())
                .andDo(document("admin/teach-access-denied",
                        responseFields(
                                fieldWithPath("status").description("401"),
                                fieldWithPath("data.errorCode").description("AUTH_5"),
                                fieldWithPath("data.message").description("권한이 없습니다.")
                        )
                ));
    }

    // ===== 2. 전체 강의실 카테고리별 조회 =====

    @Test
    @DisplayName("관리자 - 전체 강의실 카테고리별 조회")
    void adminTeachList() throws Exception {
        authenticateAs(1L);

        ClassInfoResponse inProgressSample = sampleClassInfo();

        ClassInfoResponse settingSample = ClassInfoResponse.builder()
                .gameId(2)
                .name("임팩트디자인 2반")
                .numTeam(3)
                .totalDd(16)
                .missionId(null)
                .sequence(null)
                .subject(null)
                .summary("설정중인 수업입니다.")
                .startdate(null)
                .enddate(null)
                .ddYear(null)
                .ddTerm(null)
                .statusCeo(null)
                .imageUrl("http://iptdesign.mycafe24.com/uploads/class/1/%ED%98%84%EB%8C%80%EC%9E%90%EB%8F%99%EC%B0%A8.png")
                .build();

        ClassInfoResponse completedSample = ClassInfoResponse.builder()
                .gameId(3)
                .name("임팩트디자인 3반")
                .numTeam(5)
                .totalDd(16)
                .missionId(5)
                .sequence(4)
                .subject("최종 발표")
                .summary("종료된 수업입니다.")
                .startdate(LocalDateTime.of(2025, 9, 1, 9, 0))
                .enddate(LocalDateTime.of(2025, 12, 20, 18, 0))
                .ddYear(2025)
                .ddTerm(2)
                .statusCeo(null)
                .imageUrl("http://iptdesign.mycafe24.com/uploads/class/3/image.png")
                .build();

        TeachListResponse teachList = TeachListResponse.builder()
                .inProgress(List.of(inProgressSample))
                .setting(List.of(settingSample))
                .completed(List.of(completedSample))
                .etc(List.of())
                .build();

        given(adminService.getAllTeachList(anyLong())).willReturn(teachList);

        mockMvc.perform(get("/api/admin/teach/list"))
                .andExpect(status().isOk())
                .andDo(document("admin/teach-list",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.inProgress[]").description("진행중인 강의실 목록"),
                                fieldWithPath("data.inProgress[].gameId").description("강의실 ID"),
                                fieldWithPath("data.inProgress[].name").description("강의실 이름"),
                                fieldWithPath("data.inProgress[].numTeam").description("팀 수"),
                                fieldWithPath("data.inProgress[].totalDd").description("총 의사결정일 수"),
                                fieldWithPath("data.inProgress[].missionId").description("현재 미션 ID"),
                                fieldWithPath("data.inProgress[].sequence").description("미션 순서 번호"),
                                fieldWithPath("data.inProgress[].subject").description("미션 제목"),
                                fieldWithPath("data.inProgress[].summary").description("미션 요약 설명"),
                                fieldWithPath("data.inProgress[].startdate").description("미션 시작일"),
                                fieldWithPath("data.inProgress[].enddate").description("미션 종료일"),
                                fieldWithPath("data.inProgress[].ddYear").description("의사결정 연도"),
                                fieldWithPath("data.inProgress[].ddTerm").description("의사결정 분기/기간"),
                                fieldWithPath("data.inProgress[].statusCeo").description("CEO 상태").optional(),
                                fieldWithPath("data.inProgress[].imageUrl").description("이미지 URL").optional(),
                                fieldWithPath("data.setting[]").description("설정중인 강의실 목록"),
                                fieldWithPath("data.setting[].gameId").description("강의실 ID"),
                                fieldWithPath("data.setting[].name").description("강의실 이름"),
                                fieldWithPath("data.setting[].numTeam").description("팀 수"),
                                fieldWithPath("data.setting[].totalDd").description("총 의사결정일 수"),
                                fieldWithPath("data.setting[].missionId").description("현재 미션 ID").optional(),
                                fieldWithPath("data.setting[].sequence").description("미션 순서 번호").optional(),
                                fieldWithPath("data.setting[].subject").description("미션 제목").optional(),
                                fieldWithPath("data.setting[].summary").description("미션 요약 설명").optional(),
                                fieldWithPath("data.setting[].startdate").description("미션 시작일").optional(),
                                fieldWithPath("data.setting[].enddate").description("미션 종료일").optional(),
                                fieldWithPath("data.setting[].ddYear").description("의사결정 연도").optional(),
                                fieldWithPath("data.setting[].ddTerm").description("의사결정 분기/기간").optional(),
                                fieldWithPath("data.setting[].statusCeo").description("CEO 상태").optional(),
                                fieldWithPath("data.setting[].imageUrl").description("이미지 URL").optional(),
                                fieldWithPath("data.completed[]").description("종료된 강의실 목록"),
                                fieldWithPath("data.completed[].gameId").description("강의실 ID"),
                                fieldWithPath("data.completed[].name").description("강의실 이름"),
                                fieldWithPath("data.completed[].numTeam").description("팀 수"),
                                fieldWithPath("data.completed[].totalDd").description("총 의사결정일 수"),
                                fieldWithPath("data.completed[].missionId").description("현재 미션 ID"),
                                fieldWithPath("data.completed[].sequence").description("미션 순서 번호"),
                                fieldWithPath("data.completed[].subject").description("미션 제목"),
                                fieldWithPath("data.completed[].summary").description("미션 요약 설명"),
                                fieldWithPath("data.completed[].startdate").description("미션 시작일"),
                                fieldWithPath("data.completed[].enddate").description("미션 종료일"),
                                fieldWithPath("data.completed[].ddYear").description("의사결정 연도"),
                                fieldWithPath("data.completed[].ddTerm").description("의사결정 분기/기간"),
                                fieldWithPath("data.completed[].statusCeo").description("CEO 상태").optional(),
                                fieldWithPath("data.completed[].imageUrl").description("이미지 URL").optional(),
                                fieldWithPath("data.etc[]").description("기타 강의실 목록")
                        )
                ));
    }

    @Test
    @DisplayName("관리자 - 전체 강의실 카테고리별 조회 - 사용자 없음")
    void adminTeachList_userNotFound() throws Exception {
        authenticateAs(1L);
        given(adminService.getAllTeachList(anyLong()))
                .willThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(get("/api/admin/teach/list"))
                .andExpect(status().isNotFound())
                .andDo(document("admin/teach-list-user-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("USER_1"),
                                fieldWithPath("data.message").description("회원을 찾을 수 없음.")
                        )
                ));
    }

    @Test
    @DisplayName("관리자 - 전체 강의실 카테고리별 조회 - 권한 없음")
    void adminTeachList_accessDenied() throws Exception {
        authenticateAs(1L);
        given(adminService.getAllTeachList(anyLong()))
                .willThrow(new AuthorizationException(ErrorCode.ACCESS_DENIED));

        mockMvc.perform(get("/api/admin/teach/list"))
                .andExpect(status().isUnauthorized())
                .andDo(document("admin/teach-list-access-denied",
                        responseFields(
                                fieldWithPath("status").description("401"),
                                fieldWithPath("data.errorCode").description("AUTH_5"),
                                fieldWithPath("data.message").description("권한이 없습니다.")
                        )
                ));
    }
}
