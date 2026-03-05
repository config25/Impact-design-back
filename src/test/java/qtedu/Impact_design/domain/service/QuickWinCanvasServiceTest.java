package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.request.wincanvas.WinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.quickwin.QuickWinCanvasResponse;
import qtedu.Impact_design.domain.implementation.quickwin.QuickWinCanvasAppender;
import qtedu.Impact_design.domain.implementation.quickwin.QuickWinCanvasReader;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class QuickWinCanvasServiceTest {

    @InjectMocks
    private QuickWinCanvasService quickWinCanvasService;

    @Mock
    private QuickWinCanvasReader quickWinCanvasReader;
    @Mock
    private QuickWinCanvasAppender quickWinCanvasAppender;

    @Test
    @DisplayName("getQuickWinCanvas - reader에서 퀵윈캔버스를 조회한다")
    void getQuickWinCanvas() {
        QuickWinCanvasResponse expected = QuickWinCanvasResponse.builder()
                .canvasId(1L).userId(1L).taskName("퀵태스크").submitted(false)
                .taskInputs(Collections.emptyList()).taskActivities(Collections.emptyList())
                .taskOutcomes(Collections.emptyList()).build();
        given(quickWinCanvasReader.read(1L)).willReturn(expected);

        QuickWinCanvasResponse result = quickWinCanvasService.getQuickWinCanvas(1L);

        assertThat(result.getCanvasId()).isEqualTo(1L);
        assertThat(result.getTaskName()).isEqualTo("퀵태스크");
    }

    @Test
    @DisplayName("saveQuickWinCanvas - appender에 저장을 위임한다")
    void saveQuickWinCanvas() {
        WinCanvasSaveRequest request = new WinCanvasSaveRequest();
        QuickWinCanvasResponse expected = QuickWinCanvasResponse.builder()
                .canvasId(1L).submitted(false)
                .taskInputs(Collections.emptyList()).taskActivities(Collections.emptyList())
                .taskOutcomes(Collections.emptyList()).build();
        given(quickWinCanvasAppender.append(1L, request)).willReturn(expected);

        QuickWinCanvasResponse result = quickWinCanvasService.saveQuickWinCanvas(1L, request);

        assertThat(result.getSubmitted()).isFalse();
        then(quickWinCanvasAppender).should().append(1L, request);
    }

    @Test
    @DisplayName("submitQuickWinCanvas - appender에 제출을 위임한다")
    void submitQuickWinCanvas() {
        WinCanvasSaveRequest request = new WinCanvasSaveRequest();
        QuickWinCanvasResponse expected = QuickWinCanvasResponse.builder()
                .canvasId(1L).submitted(true)
                .taskInputs(Collections.emptyList()).taskActivities(Collections.emptyList())
                .taskOutcomes(Collections.emptyList()).build();
        given(quickWinCanvasAppender.submit(1L, request)).willReturn(expected);

        QuickWinCanvasResponse result = quickWinCanvasService.submitQuickWinCanvas(1L, request);

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
        given(quickWinCanvasReader.readCanvasesByUserIds(userIds)).willReturn(expected);

        List<WinCanvasModel> result = quickWinCanvasService.getCanvasesByUserIds(userIds);

        assertThat(result).hasSize(2);
    }
}
