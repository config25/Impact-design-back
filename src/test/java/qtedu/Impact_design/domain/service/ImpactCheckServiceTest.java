package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.request.impactcheck.ImpactCheckRequest;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.domain.implementation.impactcheck.ImpactCheckAppender;
import qtedu.Impact_design.domain.implementation.impactcheck.ImpactCheckReader;
import qtedu.Impact_design.domain.model.ImpactCheckModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ImpactCheckServiceTest {

    @InjectMocks
    private ImpactCheckService impactCheckService;

    @Mock
    private ImpactCheckReader impactCheckReader;
    @Mock
    private ImpactCheckAppender impactCheckAppender;

    @Test
    @DisplayName("getImpactCheck - reader에서 임팩트체크를 조회한다")
    void getImpactCheck() {
        ImpactCheckResponse expected = ImpactCheckResponse.builder()
                .answerId(1L).userId(1L).q1Score(5).submitted(false).build();
        given(impactCheckReader.read(1L)).willReturn(expected);

        ImpactCheckResponse result = impactCheckService.getImpactCheck(1L);

        assertThat(result.getAnswerId()).isEqualTo(1L);
        assertThat(result.getQ1Score()).isEqualTo(5);
    }

    @Test
    @DisplayName("saveImpactCheck - appender에 저장을 위임한다")
    void saveImpactCheck() {
        ImpactCheckRequest request = new ImpactCheckRequest();
        ImpactCheckResponse expected = ImpactCheckResponse.builder()
                .answerId(1L).userId(1L).submitted(false).build();
        given(impactCheckAppender.append(1L, request)).willReturn(expected);

        ImpactCheckResponse result = impactCheckService.saveImpactCheck(1L, request);

        assertThat(result.getAnswerId()).isEqualTo(1L);
        then(impactCheckAppender).should().append(1L, request);
    }

    @Test
    @DisplayName("submitImpactCheck - appender에 제출을 위임한다")
    void submitImpactCheck() {
        ImpactCheckRequest request = new ImpactCheckRequest();
        ImpactCheckResponse expected = ImpactCheckResponse.builder()
                .answerId(1L).userId(1L).submitted(true).build();
        given(impactCheckAppender.submit(1L, request)).willReturn(expected);

        ImpactCheckResponse result = impactCheckService.submitImpactCheck(1L, request);

        assertThat(result.getSubmitted()).isTrue();
    }

    @Test
    @DisplayName("getImpactChecksByUserIds - reader에서 여러 유저의 임팩트체크를 조회한다")
    void getImpactChecksByUserIds() {
        List<Long> userIds = List.of(1L, 2L);
        List<ImpactCheckModel> expected = List.of(
                ImpactCheckModel.builder().answerId(1L).userId(1L).build(),
                ImpactCheckModel.builder().answerId(2L).userId(2L).build()
        );
        given(impactCheckReader.readByUserIds(userIds)).willReturn(expected);

        List<ImpactCheckModel> result = impactCheckService.getImpactChecksByUserIds(userIds);

        assertThat(result).hasSize(2);
    }
}
