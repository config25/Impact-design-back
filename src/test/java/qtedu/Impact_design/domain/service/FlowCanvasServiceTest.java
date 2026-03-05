package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.request.flowcanvas.FlowCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.domain.implementation.flowcanvas.FlowCanvasAppender;
import qtedu.Impact_design.domain.implementation.flowcanvas.FlowCanvasReader;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;
import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FlowCanvasServiceTest {

    @InjectMocks
    private FlowCanvasService flowCanvasService;

    @Mock
    private FlowCanvasReader flowCanvasReader;
    @Mock
    private FlowCanvasAppender flowCanvasAppender;

    @Test
    @DisplayName("getFlowCanvas - reader에서 플로우캔버스를 조회한다")
    void getFlowCanvas() {
        FlowCanvasResponse expected = FlowCanvasResponse.builder()
                .newVision("비전").submitted(false).goals(Collections.emptyList()).build();
        given(flowCanvasReader.read(1L)).willReturn(expected);

        FlowCanvasResponse result = flowCanvasService.getFlowCanvas(1L);

        assertThat(result.getNewVision()).isEqualTo("비전");
    }

    @Test
    @DisplayName("saveFlowCanvas - appender에 저장을 위임한다")
    void saveFlowCanvas() {
        FlowCanvasSaveRequest request = new FlowCanvasSaveRequest();
        FlowCanvasResponse expected = FlowCanvasResponse.builder()
                .submitted(false).goals(Collections.emptyList()).build();
        given(flowCanvasAppender.append(1L, request)).willReturn(expected);

        FlowCanvasResponse result = flowCanvasService.saveFlowCanvas(1L, request);

        assertThat(result.getSubmitted()).isFalse();
        then(flowCanvasAppender).should().append(1L, request);
    }

    @Test
    @DisplayName("submitFlowCanvas - appender에 제출을 위임한다")
    void submitFlowCanvas() {
        FlowCanvasSaveRequest request = new FlowCanvasSaveRequest();
        FlowCanvasResponse expected = FlowCanvasResponse.builder()
                .submitted(true).goals(Collections.emptyList()).build();
        given(flowCanvasAppender.submit(1L, request)).willReturn(expected);

        FlowCanvasResponse result = flowCanvasService.submitFlowCanvas(1L, request);

        assertThat(result.getSubmitted()).isTrue();
    }

    @Test
    @DisplayName("getFlowCanvasesByUserIds - reader에서 여러 유저의 캔버스를 조회한다")
    void getFlowCanvasesByUserIds() {
        List<Long> userIds = List.of(1L, 2L);
        List<FlowCanvasModel> expected = List.of(
                FlowCanvasModel.builder().goalId(1L).userId(1L).build(),
                FlowCanvasModel.builder().goalId(2L).userId(2L).build()
        );
        given(flowCanvasReader.readByUserIds(userIds)).willReturn(expected);

        List<FlowCanvasModel> result = flowCanvasService.getFlowCanvasesByUserIds(userIds);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("getTacticalsByGoalIds - reader에서 전술 목록을 조회한다")
    void getTacticalsByGoalIds() {
        List<Long> goalIds = List.of(1L);
        List<TacticalModel> expected = List.of(
                TacticalModel.builder().metricId(1L).build()
        );
        given(flowCanvasReader.readTacticalsByGoalIds(goalIds)).willReturn(expected);

        List<TacticalModel> result = flowCanvasService.getTacticalsByGoalIds(goalIds);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getStrategicActivitiesByGoalIds - reader에서 전략활동 목록을 조회한다")
    void getStrategicActivitiesByGoalIds() {
        List<Long> goalIds = List.of(1L);
        List<StrategicActivityModel> expected = List.of(
                StrategicActivityModel.builder().activityId(1L).build()
        );
        given(flowCanvasReader.readStrategicActivitiesByGoalIds(goalIds)).willReturn(expected);

        List<StrategicActivityModel> result = flowCanvasService.getStrategicActivitiesByGoalIds(goalIds);

        assertThat(result).hasSize(1);
    }
}
