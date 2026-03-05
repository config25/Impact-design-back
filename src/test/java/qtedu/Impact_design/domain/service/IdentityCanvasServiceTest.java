package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.request.identitycanvas.IdentityCanvasRequest;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.domain.implementation.identitycanvas.IdentityCanvasAppender;
import qtedu.Impact_design.domain.implementation.identitycanvas.IdentityCanvasReader;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class IdentityCanvasServiceTest {

    @InjectMocks
    private IdentityCanvasService identityCanvasService;

    @Mock
    private IdentityCanvasReader identityCanvasReader;
    @Mock
    private IdentityCanvasAppender identityCanvasAppender;

    @Test
    @DisplayName("getIdentityCanvas - reader에서 캔버스를 조회한다")
    void getIdentityCanvas() {
        IdentityCanvasResponse expected = IdentityCanvasResponse.builder()
                .identityId(1L).userId(1L).mission("미션").vision("비전").submitted(false).build();
        given(identityCanvasReader.read(1L)).willReturn(expected);

        IdentityCanvasResponse result = identityCanvasService.getIdentityCanvas(1L);

        assertThat(result.getIdentityId()).isEqualTo(1L);
        assertThat(result.getMission()).isEqualTo("미션");
    }

    @Test
    @DisplayName("saveIdentityCanvas - appender에 저장을 위임한다")
    void saveIdentityCanvas() {
        IdentityCanvasRequest request = new IdentityCanvasRequest();
        IdentityCanvasResponse expected = IdentityCanvasResponse.builder()
                .identityId(1L).userId(1L).submitted(false).build();
        given(identityCanvasAppender.append(1L, request)).willReturn(expected);

        IdentityCanvasResponse result = identityCanvasService.saveIdentityCanvas(1L, request);

        assertThat(result.getIdentityId()).isEqualTo(1L);
        then(identityCanvasAppender).should().append(1L, request);
    }

    @Test
    @DisplayName("getIdentityCanvasesByUserIds - reader에서 여러 유저의 캔버스를 조회한다")
    void getIdentityCanvasesByUserIds() {
        List<Long> userIds = List.of(1L, 2L);
        List<IdentityCanvasModel> expected = List.of(
                IdentityCanvasModel.builder().identityId(1L).userId(1L).build(),
                IdentityCanvasModel.builder().identityId(2L).userId(2L).build()
        );
        given(identityCanvasReader.readByUserIds(userIds)).willReturn(expected);

        List<IdentityCanvasModel> result = identityCanvasService.getIdentityCanvasesByUserIds(userIds);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("submitIdentityCanvas - appender에 제출을 위임한다")
    void submitIdentityCanvas() {
        IdentityCanvasRequest request = new IdentityCanvasRequest();
        IdentityCanvasResponse expected = IdentityCanvasResponse.builder()
                .identityId(1L).userId(1L).submitted(true).build();
        given(identityCanvasAppender.submit(1L, request)).willReturn(expected);

        IdentityCanvasResponse result = identityCanvasService.submitIdentityCanvas(1L, request);

        assertThat(result.getSubmitted()).isTrue();
    }
}
