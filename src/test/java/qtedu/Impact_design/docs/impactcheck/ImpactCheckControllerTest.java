package qtedu.Impact_design.docs.impactcheck;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.ImpactCheckController;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.service.ImpactCheckService;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ImpactCheckController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class ImpactCheckControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private ImpactCheckService impactCheckService;

    private ImpactCheckResponse sampleResponse() {
        return ImpactCheckResponse.builder()
                .answerId(1L)
                .q1Score(5)
                .q2Score(4)
                .q3Score(3)
                .q4Score(5)
                .q5Score(4)
                .q6Score(3)
                .q7Score(5)
                .q8Score(4)
                .q9Score(3)
                .q10Score(5)
                .q11Score(4)
                .q12Score(3)
                .q13Text("사회 문제 해결에 관심이 생겼습니다.")
                .q14Text("팀원들과의 협업이 좋았습니다.")
                .q15Text("시간이 부족했습니다.")
                .q16Text("더 많은 사례를 보고 싶습니다.")
                .userId(1L)
                .submitted(false)
                .gameName("임팩트디자인 1반")
                .build();
    }

    private Map<String, Object> sampleRequestBody() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("q1Score", 5);
        body.put("q2Score", 4);
        body.put("q3Score", 3);
        body.put("q4Score", 5);
        body.put("q5Score", 4);
        body.put("q6Score", 3);
        body.put("q7Score", 5);
        body.put("q8Score", 4);
        body.put("q9Score", 3);
        body.put("q10Score", 5);
        body.put("q11Score", 4);
        body.put("q12Score", 3);
        body.put("q13Text", "사회 문제 해결에 관심이 생겼습니다.");
        body.put("q14Text", "팀원들과의 협업이 좋았습니다.");
        body.put("q15Text", "시간이 부족했습니다.");
        body.put("q16Text", "더 많은 사례를 보고 싶습니다.");
        return body;
    }

    // ===== 1. 임팩트 체크 조회 =====

    @Test
    @DisplayName("임팩트 체크 조회")
    void getImpactCheck() throws Exception {
        authenticateAs(1L);
        given(impactCheckService.getImpactCheck(anyLong()))
                .willReturn(sampleResponse());

        mockMvc.perform(get("/api/impact-check"))
                .andExpect(status().isOk())
                .andDo(document("impact-check/get",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.answerId").description("응답 ID"),
                                fieldWithPath("data.q1Score").description("1번 점수"),
                                fieldWithPath("data.q2Score").description("2번 점수"),
                                fieldWithPath("data.q3Score").description("3번 점수"),
                                fieldWithPath("data.q4Score").description("4번 점수"),
                                fieldWithPath("data.q5Score").description("5번 점수"),
                                fieldWithPath("data.q6Score").description("6번 점수"),
                                fieldWithPath("data.q7Score").description("7번 점수"),
                                fieldWithPath("data.q8Score").description("8번 점수"),
                                fieldWithPath("data.q9Score").description("9번 점수"),
                                fieldWithPath("data.q10Score").description("10번 점수"),
                                fieldWithPath("data.q11Score").description("11번 점수"),
                                fieldWithPath("data.q12Score").description("12번 점수"),
                                fieldWithPath("data.q13Text").description("13번 텍스트 답변"),
                                fieldWithPath("data.q14Text").description("14번 텍스트 답변"),
                                fieldWithPath("data.q15Text").description("15번 텍스트 답변"),
                                fieldWithPath("data.q16Text").description("16번 텍스트 답변"),
                                fieldWithPath("data.userId").description("사용자 ID"),
                                fieldWithPath("data.submitted").description("제출 여부"),
                                fieldWithPath("data.gameName").description("강의실 이름").optional()
                        )
                ));
    }

    // ===== 2. 임팩트 체크 저장 =====

    @Test
    @DisplayName("임팩트 체크 저장")
    void saveImpactCheck() throws Exception {
        authenticateAs(1L);
        given(impactCheckService.saveImpactCheck(anyLong(), any()))
                .willReturn(sampleResponse());

        mockMvc.perform(post("/api/impact-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestBody())))
                .andExpect(status().isOk())
                .andDo(document("impact-check/save",
                        requestFields(
                                fieldWithPath("q1Score").description("1번 점수"),
                                fieldWithPath("q2Score").description("2번 점수"),
                                fieldWithPath("q3Score").description("3번 점수"),
                                fieldWithPath("q4Score").description("4번 점수"),
                                fieldWithPath("q5Score").description("5번 점수"),
                                fieldWithPath("q6Score").description("6번 점수"),
                                fieldWithPath("q7Score").description("7번 점수"),
                                fieldWithPath("q8Score").description("8번 점수"),
                                fieldWithPath("q9Score").description("9번 점수"),
                                fieldWithPath("q10Score").description("10번 점수"),
                                fieldWithPath("q11Score").description("11번 점수"),
                                fieldWithPath("q12Score").description("12번 점수"),
                                fieldWithPath("q13Text").description("13번 텍스트 답변"),
                                fieldWithPath("q14Text").description("14번 텍스트 답변"),
                                fieldWithPath("q15Text").description("15번 텍스트 답변"),
                                fieldWithPath("q16Text").description("16번 텍스트 답변")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.answerId").description("응답 ID"),
                                fieldWithPath("data.q1Score").description("1번 점수"),
                                fieldWithPath("data.q2Score").description("2번 점수"),
                                fieldWithPath("data.q3Score").description("3번 점수"),
                                fieldWithPath("data.q4Score").description("4번 점수"),
                                fieldWithPath("data.q5Score").description("5번 점수"),
                                fieldWithPath("data.q6Score").description("6번 점수"),
                                fieldWithPath("data.q7Score").description("7번 점수"),
                                fieldWithPath("data.q8Score").description("8번 점수"),
                                fieldWithPath("data.q9Score").description("9번 점수"),
                                fieldWithPath("data.q10Score").description("10번 점수"),
                                fieldWithPath("data.q11Score").description("11번 점수"),
                                fieldWithPath("data.q12Score").description("12번 점수"),
                                fieldWithPath("data.q13Text").description("13번 텍스트 답변"),
                                fieldWithPath("data.q14Text").description("14번 텍스트 답변"),
                                fieldWithPath("data.q15Text").description("15번 텍스트 답변"),
                                fieldWithPath("data.q16Text").description("16번 텍스트 답변"),
                                fieldWithPath("data.userId").description("사용자 ID"),
                                fieldWithPath("data.submitted").description("제출 여부"),
                                fieldWithPath("data.gameName").description("강의실 이름").optional()
                        )
                ));
    }

    @Test
    @DisplayName("임팩트 체크 저장 - 이미 제출 완료")
    void saveImpactCheck_alreadySubmitted() throws Exception {
        authenticateAs(1L);
        given(impactCheckService.saveImpactCheck(anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.ALREADY_SUBMITTED));

        mockMvc.perform(post("/api/impact-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestBody())))
                .andExpect(status().isConflict())
                .andDo(document("impact-check/save-already-submitted",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("CANVAS_1"),
                                fieldWithPath("data.message").description("이미 제출 완료된 캔버스입니다.")
                        )
                ));
    }

    // ===== 3. 임팩트 체크 제출 =====

    @Test
    @DisplayName("임팩트 체크 제출")
    void submitImpactCheck() throws Exception {
        authenticateAs(1L);
        ImpactCheckResponse submitted = ImpactCheckResponse.builder()
                .answerId(1L)
                .q1Score(5).q2Score(4).q3Score(3).q4Score(5).q5Score(4).q6Score(3)
                .q7Score(5).q8Score(4).q9Score(3).q10Score(5).q11Score(4).q12Score(3)
                .q13Text("사회 문제 해결에 관심이 생겼습니다.")
                .q14Text("팀원들과의 협업이 좋았습니다.")
                .q15Text("시간이 부족했습니다.")
                .q16Text("더 많은 사례를 보고 싶습니다.")
                .userId(1L)
                .submitted(true)
                .gameName("임팩트디자인 1반")
                .build();

        given(impactCheckService.submitImpactCheck(anyLong(), any()))
                .willReturn(submitted);

        mockMvc.perform(patch("/api/impact-check/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestBody())))
                .andExpect(status().isOk())
                .andDo(document("impact-check/submit",
                        requestFields(
                                fieldWithPath("q1Score").description("1번 점수"),
                                fieldWithPath("q2Score").description("2번 점수"),
                                fieldWithPath("q3Score").description("3번 점수"),
                                fieldWithPath("q4Score").description("4번 점수"),
                                fieldWithPath("q5Score").description("5번 점수"),
                                fieldWithPath("q6Score").description("6번 점수"),
                                fieldWithPath("q7Score").description("7번 점수"),
                                fieldWithPath("q8Score").description("8번 점수"),
                                fieldWithPath("q9Score").description("9번 점수"),
                                fieldWithPath("q10Score").description("10번 점수"),
                                fieldWithPath("q11Score").description("11번 점수"),
                                fieldWithPath("q12Score").description("12번 점수"),
                                fieldWithPath("q13Text").description("13번 텍스트 답변"),
                                fieldWithPath("q14Text").description("14번 텍스트 답변"),
                                fieldWithPath("q15Text").description("15번 텍스트 답변"),
                                fieldWithPath("q16Text").description("16번 텍스트 답변")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.answerId").description("응답 ID"),
                                fieldWithPath("data.q1Score").description("1번 점수"),
                                fieldWithPath("data.q2Score").description("2번 점수"),
                                fieldWithPath("data.q3Score").description("3번 점수"),
                                fieldWithPath("data.q4Score").description("4번 점수"),
                                fieldWithPath("data.q5Score").description("5번 점수"),
                                fieldWithPath("data.q6Score").description("6번 점수"),
                                fieldWithPath("data.q7Score").description("7번 점수"),
                                fieldWithPath("data.q8Score").description("8번 점수"),
                                fieldWithPath("data.q9Score").description("9번 점수"),
                                fieldWithPath("data.q10Score").description("10번 점수"),
                                fieldWithPath("data.q11Score").description("11번 점수"),
                                fieldWithPath("data.q12Score").description("12번 점수"),
                                fieldWithPath("data.q13Text").description("13번 텍스트 답변"),
                                fieldWithPath("data.q14Text").description("14번 텍스트 답변"),
                                fieldWithPath("data.q15Text").description("15번 텍스트 답변"),
                                fieldWithPath("data.q16Text").description("16번 텍스트 답변"),
                                fieldWithPath("data.userId").description("사용자 ID"),
                                fieldWithPath("data.submitted").description("제출 여부"),
                                fieldWithPath("data.gameName").description("게임 이름").optional()
                        )
                ));
    }

    @Test
    @DisplayName("임팩트 체크 제출 - 이미 제출 완료")
    void submitImpactCheck_alreadySubmitted() throws Exception {
        authenticateAs(1L);
        given(impactCheckService.submitImpactCheck(anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.ALREADY_SUBMITTED));

        mockMvc.perform(patch("/api/impact-check/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestBody())))
                .andExpect(status().isConflict())
                .andDo(document("impact-check/submit-already-submitted",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("CANVAS_1"),
                                fieldWithPath("data.message").description("이미 제출 완료된 캔버스입니다.")
                        )
                ));
    }
}
