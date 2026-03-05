package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.request.wincanvas.WinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.buildwin.BuildWinCanvasResponse;
import qtedu.Impact_design.domain.implementation.buildwin.BuildWinCanvasAppender;
import qtedu.Impact_design.domain.implementation.buildwin.BuildWinCanvasReader;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BuildWinCanvasServiceTest {

    @InjectMocks
    private BuildWinCanvasService buildWinCanvasService;

    @Mock
    private BuildWinCanvasReader buildWinCanvasReader;
    @Mock
    private BuildWinCanvasAppender buildWinCanvasAppender;

    @Test
    @DisplayName("getBuildWinCanvas - reader에서 빌드윈캔버스를 조회한다")
    void getBuildWinCanvas() {
        BuildWinCanvasResponse expected = BuildWinCanvasResponse.builder()
                .canvasId(1L).userId(1L).taskName("태스크").submitted(false)
                .taskInputs(Collections.emptyList()).taskActivities(Collections.emptyList())
                .taskOutcomes(Collections.emptyList()).build();
        given(buildWinCanvasReader.read(1L)).willReturn(expected);

        BuildWinCanvasResponse result = buildWinCanvasService.getBuildWinCanvas(1L);

        assertThat(result.getCanvasId()).isEqualTo(1L);
        assertThat(result.getTaskName()).isEqualTo("태스크");
    }

    @Test
    @DisplayName("saveBuildWinCanvas - appender에 저장을 위임한다")
    void saveBuildWinCanvas() {
        WinCanvasSaveRequest request = new WinCanvasSaveRequest();
        BuildWinCanvasResponse expected = BuildWinCanvasResponse.builder()
                .canvasId(1L).submitted(false)
                .taskInputs(Collections.emptyList()).taskActivities(Collections.emptyList())
                .taskOutcomes(Collections.emptyList()).build();
        given(buildWinCanvasAppender.append(1L, request)).willReturn(expected);

        BuildWinCanvasResponse result = buildWinCanvasService.saveBuildWinCanvas(1L, request);

        assertThat(result.getSubmitted()).isFalse();
        then(buildWinCanvasAppender).should().append(1L, request);
    }

    @Test
    @DisplayName("submitBuildWinCanvas - appender에 제출을 위임한다")
    void submitBuildWinCanvas() {
        WinCanvasSaveRequest request = new WinCanvasSaveRequest();
        BuildWinCanvasResponse expected = BuildWinCanvasResponse.builder()
                .canvasId(1L).submitted(true)
                .taskInputs(Collections.emptyList()).taskActivities(Collections.emptyList())
                .taskOutcomes(Collections.emptyList()).build();
        given(buildWinCanvasAppender.submit(1L, request)).willReturn(expected);

        BuildWinCanvasResponse result = buildWinCanvasService.submitBuildWinCanvas(1L, request);

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
        given(buildWinCanvasReader.readCanvasesByUserIds(userIds)).willReturn(expected);

        List<WinCanvasModel> result = buildWinCanvasService.getCanvasesByUserIds(userIds);

        assertThat(result).hasSize(2);
    }
}
