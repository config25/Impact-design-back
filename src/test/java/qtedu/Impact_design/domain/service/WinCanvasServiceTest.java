package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.request.wincanvas.WinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.wincanvas.WinCanvasResponse;
import qtedu.Impact_design.domain.implementation.wincanvas.WinCanvasAppender;
import qtedu.Impact_design.domain.implementation.wincanvas.WinCanvasReader;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class WinCanvasServiceTest {

    @InjectMocks
    private WinCanvasService winCanvasService;

    @Mock
    private WinCanvasReader winCanvasReader;
    @Mock
    private WinCanvasAppender winCanvasAppender;

    @Test
    @DisplayName("getWinCanvas - reader에서 캔버스를 조회한다")
    void getWinCanvas() {
        WinCanvasResponse expected = WinCanvasResponse.builder()
                .canvasId(1L).userId(1L).taskName("태스크").submitted(false)
                .taskInputs(Collections.emptyList()).taskActivities(Collections.emptyList())
                .taskOutcomes(Collections.emptyList()).build();
        given(winCanvasReader.read(1L, CanvasType.QUICK)).willReturn(expected);

        WinCanvasResponse result = winCanvasService.getWinCanvas(1L, CanvasType.QUICK);

        assertThat(result.getCanvasId()).isEqualTo(1L);
        assertThat(result.getTaskName()).isEqualTo("태스크");
    }

    @Test
    @DisplayName("saveWinCanvas - appender에 저장을 위임한다")
    void saveWinCanvas() {
        WinCanvasSaveRequest request = new WinCanvasSaveRequest();
        WinCanvasResponse expected = WinCanvasResponse.builder()
                .canvasId(1L).submitted(false)
                .taskInputs(Collections.emptyList()).taskActivities(Collections.emptyList())
                .taskOutcomes(Collections.emptyList()).build();
        given(winCanvasAppender.append(1L, request, CanvasType.BUILD)).willReturn(expected);

        WinCanvasResponse result = winCanvasService.saveWinCanvas(1L, request, CanvasType.BUILD);

        assertThat(result.getSubmitted()).isFalse();
        then(winCanvasAppender).should().append(1L, request, CanvasType.BUILD);
    }

    @Test
    @DisplayName("submitWinCanvas - appender에 제출을 위임한다")
    void submitWinCanvas() {
        WinCanvasSaveRequest request = new WinCanvasSaveRequest();
        WinCanvasResponse expected = WinCanvasResponse.builder()
                .canvasId(1L).submitted(true)
                .taskInputs(Collections.emptyList()).taskActivities(Collections.emptyList())
                .taskOutcomes(Collections.emptyList()).build();
        given(winCanvasAppender.submit(1L, request, CanvasType.QUICK)).willReturn(expected);

        WinCanvasResponse result = winCanvasService.submitWinCanvas(1L, request, CanvasType.QUICK);

        assertThat(result.getSubmitted()).isTrue();
    }

    @Test
    @DisplayName("getCanvasesByUserIds - reader에서 여러 유저의 캔버스를 조회한다")
    void getCanvasesByUserIds() {
        List<Long> userIds = List.of(1L, 2L);
        List<WinCanvasModel> expected = List.of(
                WinCanvasModel.builder().canvasId(1L).userId(1L).build(),
                WinCanvasModel.builder().canvasId(2L).userId(2L).build()
        );
        given(winCanvasReader.readCanvasesByUserIds(userIds, CanvasType.BUILD)).willReturn(expected);

        List<WinCanvasModel> result = winCanvasService.getCanvasesByUserIds(userIds, CanvasType.BUILD);

        assertThat(result).hasSize(2);
    }
}
