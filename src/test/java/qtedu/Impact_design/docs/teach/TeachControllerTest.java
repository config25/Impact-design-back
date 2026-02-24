package qtedu.Impact_design.docs.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.TeachController;
import qtedu.Impact_design.api.dto.response.teach.*;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.InvalidDateException;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.service.TeachService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TeachController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class TeachControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private TeachService teachService;

    // ===== 샘플 데이터 =====

    private ClassInfoResponse sampleClassInfo() {
        return ClassInfoResponse.builder()
                .gameId(1)
                .name("임팩트 디자인 수업")
                .numTeam(4)
                .totalDd(16)
                .missionId(1)
                .sequence(1)
                .subject("미션 1")
                .summary("수업 요약")
                .startdate(LocalDateTime.of(2026, 2, 1, 9, 0))
                .enddate(LocalDateTime.of(2026, 3, 1, 18, 0))
                .ddYear(1)
                .ddTerm(1)
                .statusCeo(20)
                .imageUrl("http://localhost:8080/uploads/class/1/image.png")
                .build();
    }

    private TeachDetailResponse sampleTeachDetail() {
        TeachDetailResponse.MissionInfo mission = TeachDetailResponse.MissionInfo.builder()
                .missionId(1)
                .sequence(1)
                .subject("미션 1")
                .summary("미션 요약")
                .startdate(LocalDateTime.of(2026, 2, 1, 9, 0))
                .enddate(LocalDateTime.of(2026, 3, 1, 18, 0))
                .ddYear(1)
                .ddTerm(1)
                .build();

        TeachDetailResponse.TeamInfo team = TeachDetailResponse.TeamInfo.builder()
                .teamId(1)
                .teamName("팀1")
                .sequence(1)
                .status(0)
                .numUser(3)
                .submitA("Y")
                .submitB("N")
                .submitC("N")
                .submitD("N")
                .submitE("N")
                .submitF("N")
                .build();

        TeachDetailResponse.GameLogoInfo gameLogo = TeachDetailResponse.GameLogoInfo.builder()
                .contentsId(1)
                .subject("로고")
                .extDir("uploads")
                .orgFilename("logo.png")
                .newFilename("logo_new.png")
                .build();

        return TeachDetailResponse.builder()
                .gameId(1)
                .name("임팩트 디자인 수업")
                .numTeam(4)
                .totalDd(16)
                .status(10)
                .summary("수업 요약")
                .code("ABC123")
                .step("IMPACT_CHECK,IDENTITY")
                .stepArr(List.of("IMPACT_CHECK", "IDENTITY"))
                .mission(mission)
                .teams(List.of(team))
                .gameLogo(gameLogo)
                .imageUrl("http://localhost:8080/uploads/class/1/image.png")
                .build();
    }

    private StudentListResponse sampleStudentList() {
        StudentListResponse.GameInfo gameInfo = StudentListResponse.GameInfo.builder()
                .gameId(1)
                .name("임팩트 디자인 수업")
                .numTeam(4)
                .totalDd(16)
                .status(10)
                .summary("수업 요약")
                .code("ABC123")
                .step("IMPACT_CHECK,IDENTITY")
                .build();

        StudentListResponse.TeamInfo teamInfo = StudentListResponse.TeamInfo.builder()
                .teamId(1)
                .name("팀1")
                .sequence(1)
                .numUser(3)
                .build();

        StudentListResponse.StudentInfo studentInfo = StudentListResponse.StudentInfo.builder()
                .userId(10L)
                .loginId("a1001")
                .name("홍길동")
                .teamId(1)
                .teamName("팀1")
                .build();

        return StudentListResponse.builder()
                .game(gameInfo)
                .teamList(List.of(teamInfo))
                .studentList(List.of(studentInfo))
                .build();
    }

    // ===== 1. 강의실 목록 조회 (teach index) =====

    @Test
    @DisplayName("강의실 목록 조회")
    void teach() throws Exception {
        authenticateAs(1L);
        given(teachService.getTeachIndex(anyLong()))
                .willReturn(List.of(sampleClassInfo()));

        mockMvc.perform(get("/api/teach"))
                .andExpect(status().isOk())
                .andDo(document("teach/index",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data[].gameId").description("게임 ID"),
                                fieldWithPath("data[].name").description("강의실 이름"),
                                fieldWithPath("data[].numTeam").description("팀 수"),
                                fieldWithPath("data[].totalDd").description("총 D-day"),
                                fieldWithPath("data[].missionId").description("미션 ID").optional(),
                                fieldWithPath("data[].sequence").description("미션 순서").optional(),
                                fieldWithPath("data[].subject").description("미션 주제").optional(),
                                fieldWithPath("data[].summary").description("요약").optional(),
                                fieldWithPath("data[].startdate").description("시작일").optional(),
                                fieldWithPath("data[].enddate").description("종료일").optional(),
                                fieldWithPath("data[].ddYear").description("D-day 년도").optional(),
                                fieldWithPath("data[].ddTerm").description("D-day 분기").optional(),
                                fieldWithPath("data[].statusCeo").description("CEO 상태").optional(),
                                fieldWithPath("data[].imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    // ===== 2. 강의실 카테고리별 목록 조회 (teach list) =====

    @Test
    @DisplayName("강의실 카테고리별 목록 조회")
    void teachList() throws Exception {
        authenticateAs(1L);
        TeachListResponse response = TeachListResponse.builder()
                .inProgress(List.of(sampleClassInfo()))
                .setting(List.of())
                .completed(List.of())
                .etc(List.of())
                .build();
        given(teachService.getTeachList(anyLong()))
                .willReturn(response);

        mockMvc.perform(get("/api/teach/list"))
                .andExpect(status().isOk())
                .andDo(document("teach/list",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.inProgress[]").description("진행중 강의실 목록"),
                                fieldWithPath("data.inProgress[].gameId").description("게임 ID"),
                                fieldWithPath("data.inProgress[].name").description("강의실 이름"),
                                fieldWithPath("data.inProgress[].numTeam").description("팀 수"),
                                fieldWithPath("data.inProgress[].totalDd").description("총 D-day"),
                                fieldWithPath("data.inProgress[].missionId").description("미션 ID").optional(),
                                fieldWithPath("data.inProgress[].sequence").description("미션 순서").optional(),
                                fieldWithPath("data.inProgress[].subject").description("미션 주제").optional(),
                                fieldWithPath("data.inProgress[].summary").description("요약").optional(),
                                fieldWithPath("data.inProgress[].startdate").description("시작일").optional(),
                                fieldWithPath("data.inProgress[].enddate").description("종료일").optional(),
                                fieldWithPath("data.inProgress[].ddYear").description("D-day 년도").optional(),
                                fieldWithPath("data.inProgress[].ddTerm").description("D-day 분기").optional(),
                                fieldWithPath("data.inProgress[].statusCeo").description("CEO 상태").optional(),
                                fieldWithPath("data.inProgress[].imageUrl").description("이미지 URL").optional(),
                                fieldWithPath("data.setting[]").description("설정중 강의실 목록"),
                                fieldWithPath("data.completed[]").description("완료된 강의실 목록"),
                                fieldWithPath("data.etc[]").description("기타 강의실 목록")
                        )
                ));
    }

    // ===== 3. 클래스 상세 조회 =====

    @Test
    @DisplayName("클래스 상세 조회")
    void teachDetail() throws Exception {
        given(teachService.getTeachDetail(anyInt()))
                .willReturn(sampleTeachDetail());

        mockMvc.perform(get("/api/teach/detail")
                        .param("gameId", "1"))
                .andExpect(status().isOk())
                .andDo(document("teach/detail",
                        queryParameters(
                                parameterWithName("gameId").description("게임 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.gameId").description("게임 ID"),
                                fieldWithPath("data.name").description("강의실 이름"),
                                fieldWithPath("data.numTeam").description("팀 수"),
                                fieldWithPath("data.totalDd").description("총 D-day"),
                                fieldWithPath("data.status").description("게임 상태"),
                                fieldWithPath("data.summary").description("요약"),
                                fieldWithPath("data.code").description("참가 코드"),
                                fieldWithPath("data.step").description("공개 단계"),
                                fieldWithPath("data.stepArr[]").description("공개 단계 배열"),
                                fieldWithPath("data.mission").description("최신 미션 정보").optional(),
                                fieldWithPath("data.mission.missionId").description("미션 ID"),
                                fieldWithPath("data.mission.sequence").description("미션 순서"),
                                fieldWithPath("data.mission.subject").description("미션 주제").optional(),
                                fieldWithPath("data.mission.summary").description("미션 요약").optional(),
                                fieldWithPath("data.mission.startdate").description("시작일"),
                                fieldWithPath("data.mission.enddate").description("종료일"),
                                fieldWithPath("data.mission.ddYear").description("D-day 년도"),
                                fieldWithPath("data.mission.ddTerm").description("D-day 분기"),
                                fieldWithPath("data.teams[]").description("팀 목록"),
                                fieldWithPath("data.teams[].teamId").description("팀 ID"),
                                fieldWithPath("data.teams[].teamName").description("팀 이름"),
                                fieldWithPath("data.teams[].sequence").description("팀 순서"),
                                fieldWithPath("data.teams[].status").description("팀 상태"),
                                fieldWithPath("data.teams[].numUser").description("팀원 수"),
                                fieldWithPath("data.teams[].submitA").description("성과관리 현황진단 제출 여부"),
                                fieldWithPath("data.teams[].submitB").description("정체성 설계 제출 여부"),
                                fieldWithPath("data.teams[].submitC").description("성과경로 설계 제출 여부"),
                                fieldWithPath("data.teams[].submitD").description("전술적 실행과제 제출 여부"),
                                fieldWithPath("data.teams[].submitE").description("전략적 실행과제 제출 여부"),
                                fieldWithPath("data.teams[].submitF").description("실행과제 검증 제출 여부"),
                                fieldWithPath("data.gameLogo").description("게임 로고 정보").optional(),
                                fieldWithPath("data.gameLogo.contentsId").description("콘텐츠 ID"),
                                fieldWithPath("data.gameLogo.subject").description("제목"),
                                fieldWithPath("data.gameLogo.extDir").description("확장 디렉토리"),
                                fieldWithPath("data.gameLogo.orgFilename").description("원본 파일명"),
                                fieldWithPath("data.gameLogo.newFilename").description("새 파일명"),
                                fieldWithPath("data.imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    @Test
    @DisplayName("클래스 상세 조회 - 게임 없음")
    void teachDetail_gameNotFound() throws Exception {
        given(teachService.getTeachDetail(anyInt()))
                .willThrow(new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        mockMvc.perform(get("/api/teach/detail")
                        .param("gameId", "999"))
                .andExpect(status().isNotFound())
                .andDo(document("teach/detail-game-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("GAME_1"),
                                fieldWithPath("data.message").description("게임을 찾을 수 없습니다.")
                        )
                ));
    }

    // ===== 4. 클래스 상세 조회 2 =====

    @Test
    @DisplayName("클래스 상세 조회 2")
    void teachDetail2() throws Exception {
        authenticateAs(1L);
        TeachDetail2Response response = TeachDetail2Response.builder()
                .gameId(1)
                .name("임팩트 디자인 수업")
                .numTeam(4)
                .totalDd(16)
                .status(10)
                .summary("수업 요약")
                .code("ABC123")
                .step("IMPACT_CHECK,IDENTITY")
                .stepArr(List.of("IMPACT_CHECK", "IDENTITY"))
                .mission(TeachDetailResponse.MissionInfo.builder()
                        .missionId(1).sequence(1).subject("미션 1").summary("미션 요약")
                        .startdate(LocalDateTime.of(2026, 2, 1, 9, 0))
                        .enddate(LocalDateTime.of(2026, 3, 1, 18, 0))
                        .ddYear(1).ddTerm(1).build())
                .teams(List.of(TeachDetailResponse.TeamInfo.builder()
                        .teamId(1).teamName("팀1").sequence(1).status(0).numUser(3)
                        .submitA("Y").submitB("N").submitC("N").submitD("N").submitE("N").submitF("N")
                        .build()))
                .gameLogo(null)
                .imageUrl("http://localhost:8080/uploads/class/1/image.png")
                .classList(List.of(sampleClassInfo()))
                .build();

        given(teachService.getTeachDetail2(anyLong(), any()))
                .willReturn(response);

        mockMvc.perform(get("/api/teach/detail2")
                        .param("gameId", "1"))
                .andExpect(status().isOk())
                .andDo(document("teach/detail2",
                        queryParameters(
                                parameterWithName("gameId").description("게임 ID (없으면 첫 번째 진행중 클래스)").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.gameId").description("게임 ID"),
                                fieldWithPath("data.name").description("강의실 이름"),
                                fieldWithPath("data.numTeam").description("팀 수"),
                                fieldWithPath("data.totalDd").description("총 D-day"),
                                fieldWithPath("data.status").description("게임 상태"),
                                fieldWithPath("data.summary").description("요약"),
                                fieldWithPath("data.code").description("참가 코드"),
                                fieldWithPath("data.step").description("공개 단계"),
                                fieldWithPath("data.stepArr[]").description("공개 단계 배열"),
                                fieldWithPath("data.mission").description("최신 미션 정보").optional(),
                                fieldWithPath("data.mission.missionId").description("미션 ID"),
                                fieldWithPath("data.mission.sequence").description("미션 순서"),
                                fieldWithPath("data.mission.subject").description("미션 주제").optional(),
                                fieldWithPath("data.mission.summary").description("미션 요약").optional(),
                                fieldWithPath("data.mission.startdate").description("시작일"),
                                fieldWithPath("data.mission.enddate").description("종료일"),
                                fieldWithPath("data.mission.ddYear").description("D-day 년도"),
                                fieldWithPath("data.mission.ddTerm").description("D-day 분기"),
                                fieldWithPath("data.teams[]").description("팀 목록"),
                                fieldWithPath("data.teams[].teamId").description("팀 ID"),
                                fieldWithPath("data.teams[].teamName").description("팀 이름"),
                                fieldWithPath("data.teams[].sequence").description("팀 순서"),
                                fieldWithPath("data.teams[].status").description("팀 상태"),
                                fieldWithPath("data.teams[].numUser").description("팀원 수"),
                                fieldWithPath("data.teams[].submitA").description("성과관리 현황진단 제출 여부"),
                                fieldWithPath("data.teams[].submitB").description("정체성 설계 제출 여부"),
                                fieldWithPath("data.teams[].submitC").description("성과경로 설계 제출 여부"),
                                fieldWithPath("data.teams[].submitD").description("전술적 실행과제 제출 여부"),
                                fieldWithPath("data.teams[].submitE").description("전략적 실행과제 제출 여부"),
                                fieldWithPath("data.teams[].submitF").description("실행과제 검증 제출 여부"),
                                fieldWithPath("data.gameLogo").description("게임 로고 정보").optional(),
                                fieldWithPath("data.imageUrl").description("이미지 URL").optional(),
                                fieldWithPath("data.classList[]").description("교사의 진행중 클래스 목록"),
                                fieldWithPath("data.classList[].gameId").description("게임 ID"),
                                fieldWithPath("data.classList[].name").description("강의실 이름"),
                                fieldWithPath("data.classList[].numTeam").description("팀 수"),
                                fieldWithPath("data.classList[].totalDd").description("총 D-day"),
                                fieldWithPath("data.classList[].missionId").description("미션 ID").optional(),
                                fieldWithPath("data.classList[].sequence").description("미션 순서").optional(),
                                fieldWithPath("data.classList[].subject").description("미션 주제").optional(),
                                fieldWithPath("data.classList[].summary").description("요약").optional(),
                                fieldWithPath("data.classList[].startdate").description("시작일").optional(),
                                fieldWithPath("data.classList[].enddate").description("종료일").optional(),
                                fieldWithPath("data.classList[].ddYear").description("D-day 년도").optional(),
                                fieldWithPath("data.classList[].ddTerm").description("D-day 분기").optional(),
                                fieldWithPath("data.classList[].statusCeo").description("CEO 상태").optional(),
                                fieldWithPath("data.classList[].imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    @Test
    @DisplayName("클래스 상세 조회 2 - 게임 없음")
    void teachDetail2_gameNotFound() throws Exception {
        authenticateAs(1L);
        given(teachService.getTeachDetail2(anyLong(), any()))
                .willThrow(new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        mockMvc.perform(get("/api/teach/detail2"))
                .andExpect(status().isNotFound())
                .andDo(document("teach/detail2-game-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("GAME_1"),
                                fieldWithPath("data.message").description("게임을 찾을 수 없습니다.")
                        )
                ));
    }

    // ===== 5. 학생 목록 조회 =====

    @Test
    @DisplayName("학생 목록 조회")
    void studentList() throws Exception {
        given(teachService.getStudentList(anyInt()))
                .willReturn(sampleStudentList());

        mockMvc.perform(get("/api/teach/student-list")
                        .param("gameId", "1"))
                .andExpect(status().isOk())
                .andDo(document("teach/student-list",
                        queryParameters(
                                parameterWithName("gameId").description("게임 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.game.gameId").description("게임 ID"),
                                fieldWithPath("data.game.name").description("강의실 이름"),
                                fieldWithPath("data.game.numTeam").description("팀 수"),
                                fieldWithPath("data.game.totalDd").description("총 D-day"),
                                fieldWithPath("data.game.status").description("게임 상태"),
                                fieldWithPath("data.game.summary").description("요약"),
                                fieldWithPath("data.game.code").description("참가 코드"),
                                fieldWithPath("data.game.step").description("공개 단계"),
                                fieldWithPath("data.teamList[]").description("팀 목록"),
                                fieldWithPath("data.teamList[].teamId").description("팀 ID"),
                                fieldWithPath("data.teamList[].name").description("팀 이름"),
                                fieldWithPath("data.teamList[].sequence").description("팀 순서"),
                                fieldWithPath("data.teamList[].numUser").description("팀원 수"),
                                fieldWithPath("data.studentList[]").description("학생 목록"),
                                fieldWithPath("data.studentList[].userId").description("사용자 ID"),
                                fieldWithPath("data.studentList[].loginId").description("로그인 ID"),
                                fieldWithPath("data.studentList[].name").description("학생 이름"),
                                fieldWithPath("data.studentList[].mail").description("이메일").optional(),
                                fieldWithPath("data.studentList[].teamId").description("소속 팀 ID").optional(),
                                fieldWithPath("data.studentList[].teamName").description("소속 팀 이름").optional()
                        )
                ));
    }

    @Test
    @DisplayName("학생 목록 조회 - 게임 없음")
    void studentList_gameNotFound() throws Exception {
        given(teachService.getStudentList(anyInt()))
                .willThrow(new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        mockMvc.perform(get("/api/teach/student-list")
                        .param("gameId", "999"))
                .andExpect(status().isNotFound())
                .andDo(document("teach/student-list-game-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("GAME_1"),
                                fieldWithPath("data.message").description("게임을 찾을 수 없습니다.")
                        )
                ));
    }

    // ===== 6. 클래스 생성 =====

    @Test
    @DisplayName("클래스 생성")
    void createClass() throws Exception {
        authenticateAs(1L);
        given(teachService.createClass(anyLong(), any(), any()))
                .willReturn(1);

        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "새 강의실",
                "numTeam", 4,
                "numMember", 10,
                "worldType", 1,
                "popupId", 0,
                "summary", "강의실 설명",
                "classType", "BASIC",
                "target", "대학생",
                "projectDate", "2026-06-30"
        ));

        MockMultipartFile requestPart = new MockMultipartFile(
                "request", "request", "application/json", requestJson.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile imagePart = new MockMultipartFile(
                "image", "image.png", "image/png", "fake-image".getBytes());

        mockMvc.perform(multipart("/api/teach/class")
                        .file(requestPart)
                        .file(imagePart))
                .andExpect(status().isOk())
                .andDo(document("teach/create-class",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data").description("생성된 게임 ID")
                        )
                ));
    }

    @Test
    @DisplayName("클래스 생성 - 유효하지 않은 날짜")
    void createClass_invalidDate() throws Exception {
        authenticateAs(1L);
        given(teachService.createClass(anyLong(), any(), any()))
                .willThrow(new InvalidDateException(ErrorCode.INVALID_DATE));

        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "새 강의실",
                "numTeam", 4,
                "projectDate", "invalid-date"
        ));

        MockMultipartFile requestPart = new MockMultipartFile(
                "request", "request", "application/json", requestJson.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/teach/class")
                        .file(requestPart))
                .andExpect(status().isBadRequest())
                .andDo(document("teach/create-class-invalid-date",
                        responseFields(
                                fieldWithPath("status").description("400"),
                                fieldWithPath("data.errorCode").description("DATE_1"),
                                fieldWithPath("data.message").description("유효하지 않은 날짜입니다.")
                        )
                ));
    }

    // ===== 7. 클래스 수정 =====

    @Test
    @DisplayName("클래스 수정")
    void updateClass() throws Exception {
        given(teachService.updateClass(anyInt(), any(), any()))
                .willReturn(1);

        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "수정된 강의실",
                "numTeam", 6,
                "summary", "수정된 설명"
        ));

        MockMultipartFile requestPart = new MockMultipartFile(
                "request", "request", "application/json", requestJson.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/teach/class/{gameId}", 1)
                        .file(requestPart)
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk())
                .andDo(document("teach/update-class",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data").description("수정된 게임 ID")
                        )
                ));
    }

    @Test
    @DisplayName("클래스 수정 - 게임 없음")
    void updateClass_gameNotFound() throws Exception {
        given(teachService.updateClass(anyInt(), any(), any()))
                .willThrow(new NotFoundException(ErrorCode.GAME_NOT_FOUND));

        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "수정된 강의실"
        ));

        MockMultipartFile requestPart = new MockMultipartFile(
                "request", "request", "application/json", requestJson.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/teach/class/{gameId}", 999)
                        .file(requestPart)
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isNotFound())
                .andDo(document("teach/update-class-game-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("GAME_1"),
                                fieldWithPath("data.message").description("게임을 찾을 수 없습니다.")
                        )
                ));
    }

    // ===== 8. 강의실 시작 =====

    @Test
    @DisplayName("강의실 시작")
    void startClass() throws Exception {
        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "enddate", "2026-03-01 18:00"
        ));

        mockMvc.perform(post("/api/teach/class/{gameId}/start", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(document("teach/start-class",
                        pathParameters(
                                parameterWithName("gameId").description("게임 ID")
                        ),
                        requestFields(
                                fieldWithPath("enddate").description("제출 기한 (yyyy-MM-dd HH:mm)")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공")
                        )
                ));
    }

    @Test
    @DisplayName("강의실 시작 - 게임 없음")
    void startClass_gameNotFound() throws Exception {
        willThrow(new NotFoundException(ErrorCode.GAME_NOT_FOUND))
                .given(teachService).startClass(anyInt(), any());

        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "enddate", "2026-03-01 18:00"
        ));

        mockMvc.perform(post("/api/teach/class/{gameId}/start", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andDo(document("teach/start-class-game-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("GAME_1"),
                                fieldWithPath("data.message").description("게임을 찾을 수 없습니다.")
                        )
                ));
    }

    @Test
    @DisplayName("강의실 시작 - 유효하지 않은 날짜")
    void startClass_invalidDate() throws Exception {
        willThrow(new InvalidDateException(ErrorCode.INVALID_DATE))
                .given(teachService).startClass(anyInt(), any());

        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "enddate", "invalid"
        ));

        mockMvc.perform(post("/api/teach/class/{gameId}/start", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andDo(document("teach/start-class-invalid-date",
                        responseFields(
                                fieldWithPath("status").description("400"),
                                fieldWithPath("data.errorCode").description("DATE_1"),
                                fieldWithPath("data.message").description("유효하지 않은 날짜입니다.")
                        )
                ));
    }

    // ===== 9. 강의실 종료 =====

    @Test
    @DisplayName("강의실 종료")
    void endClass() throws Exception {
        mockMvc.perform(post("/api/teach/class/{gameId}/end", 1))
                .andExpect(status().isOk())
                .andDo(document("teach/end-class",
                        pathParameters(
                                parameterWithName("gameId").description("게임 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공")
                        )
                ));
    }

    @Test
    @DisplayName("강의실 종료 - 게임 없음")
    void endClass_gameNotFound() throws Exception {
        willThrow(new NotFoundException(ErrorCode.GAME_NOT_FOUND))
                .given(teachService).endClass(anyInt());

        mockMvc.perform(post("/api/teach/class/{gameId}/end", 999))
                .andExpect(status().isNotFound())
                .andDo(document("teach/end-class-game-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("GAME_1"),
                                fieldWithPath("data.message").description("게임을 찾을 수 없습니다.")
                        )
                ));
    }

    // ===== 10. 강의실 복원 =====

    @Test
    @DisplayName("강의실 복원")
    void restoreClass() throws Exception {
        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "enddate", "2026-03-01 18:00"
        ));

        mockMvc.perform(post("/api/teach/class/{gameId}/restore", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(document("teach/restore-class",
                        pathParameters(
                                parameterWithName("gameId").description("게임 ID")
                        ),
                        requestFields(
                                fieldWithPath("enddate").description("제출 기한 (yyyy-MM-dd HH:mm)")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공")
                        )
                ));
    }

    @Test
    @DisplayName("강의실 복원 - 게임 없음")
    void restoreClass_gameNotFound() throws Exception {
        willThrow(new NotFoundException(ErrorCode.GAME_NOT_FOUND))
                .given(teachService).restoreClass(anyInt(), any());

        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "enddate", "2026-03-01 18:00"
        ));

        mockMvc.perform(post("/api/teach/class/{gameId}/restore", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andDo(document("teach/restore-class-game-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("GAME_1"),
                                fieldWithPath("data.message").description("게임을 찾을 수 없습니다.")
                        )
                ));
    }

    // ===== 11. 다음 단계 시작 =====

    @Test
    @DisplayName("다음 단계 시작")
    void startNextStage() throws Exception {
        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "enddate", "2026-04-01 18:00"
        ));

        mockMvc.perform(post("/api/teach/class/{gameId}/next-stage", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(document("teach/next-stage",
                        pathParameters(
                                parameterWithName("gameId").description("게임 ID")
                        ),
                        requestFields(
                                fieldWithPath("enddate").description("제출 기한 (yyyy-MM-dd HH:mm)")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공")
                        )
                ));
    }

    @Test
    @DisplayName("다음 단계 시작 - 게임 없음")
    void startNextStage_gameNotFound() throws Exception {
        willThrow(new NotFoundException(ErrorCode.GAME_NOT_FOUND))
                .given(teachService).startNextStage(anyInt(), any());

        String requestJson = objectMapper.writeValueAsString(java.util.Map.of(
                "enddate", "2026-04-01 18:00"
        ));

        mockMvc.perform(post("/api/teach/class/{gameId}/next-stage", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andDo(document("teach/next-stage-game-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("GAME_1"),
                                fieldWithPath("data.message").description("게임을 찾을 수 없습니다.")
                        )
                ));
    }
}
