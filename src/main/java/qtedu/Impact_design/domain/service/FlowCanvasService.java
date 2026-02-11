package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.request.flowcanvas.FlowCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.domain.implementation.flowcanvas.FlowCanvasAppender;
import qtedu.Impact_design.domain.implementation.flowcanvas.FlowCanvasReader;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;
import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlowCanvasService {

    private final FlowCanvasReader flowCanvasReader;
    private final FlowCanvasAppender flowCanvasAppender;

    public FlowCanvasResponse getFlowCanvas(Long userId) {
        return flowCanvasReader.read(userId);
    }

    public FlowCanvasResponse saveFlowCanvas(Long userId, FlowCanvasSaveRequest request) {
        return flowCanvasAppender.append(userId, request);
    }

    public List<FlowCanvasModel> getFlowCanvasesByUserIds(List<Long> userIds) {
        return flowCanvasReader.readByUserIds(userIds);
    }

    public List<TacticalModel> getTacticalsByGoalIds(List<Long> goalIds) {
        return flowCanvasReader.readTacticalsByGoalIds(goalIds);
    }

    public List<StrategicActivityModel> getStrategicActivitiesByGoalIds(List<Long> goalIds) {
        return flowCanvasReader.readStrategicActivitiesByGoalIds(goalIds);
    }

    public FlowCanvasResponse submitFlowCanvas(Long userId) {
        flowCanvasAppender.submit(userId);
        return flowCanvasReader.read(userId);
    }
}
