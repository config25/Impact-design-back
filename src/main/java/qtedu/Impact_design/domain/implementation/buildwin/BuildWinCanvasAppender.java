package qtedu.Impact_design.domain.implementation.buildwin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.request.wincanvas.WinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.buildwin.BuildWinCanvasResponse;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.domain.implementation.wincanvas.WinCanvasMerger;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

@Component
@RequiredArgsConstructor
public class BuildWinCanvasAppender {

    private final WinCanvasRepository winCanvasRepository;
    private final WinCanvasMerger winCanvasMerger;
    private final BuildWinCanvasReader buildWinCanvasReader;

    @Transactional
    public BuildWinCanvasResponse append(Long userId, WinCanvasSaveRequest request) {
        if (winCanvasRepository.existsSubmittedByUserIdAndCanvasType(userId, CanvasType.BUILD)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }

        WinCanvasModel canvas = winCanvasRepository.findByUserIdAndCanvasType(userId, CanvasType.BUILD)
                .map(existing -> winCanvasRepository.save(WinCanvasModel.builder()
                        .canvasId(existing.getCanvasId())
                        .userId(userId)
                        .canvasType(CanvasType.BUILD)
                        .strategicGoal(request.getStrategicGoal())
                        .taskName(request.getTaskName())
                        .taskDescription(request.getTaskDescription())
                        .crisisSignal(request.getCrisisSignal())
                        .painTouchPoint(request.getPainTouchPoint())
                        .build()))
                .orElseGet(() -> winCanvasRepository.save(WinCanvasModel.builder()
                        .userId(userId)
                        .canvasType(CanvasType.BUILD)
                        .strategicGoal(request.getStrategicGoal())
                        .taskName(request.getTaskName())
                        .taskDescription(request.getTaskDescription())
                        .crisisSignal(request.getCrisisSignal())
                        .painTouchPoint(request.getPainTouchPoint())
                        .build()));

        Long canvasId = canvas.getCanvasId();

        winCanvasMerger.mergeTaskInputs(canvasId, request);
        winCanvasMerger.mergeTaskActivities(canvasId, request);
        winCanvasMerger.mergeTeamwork(canvasId, request);
        winCanvasMerger.mergeTaskOutcomes(canvasId, request);

        return buildWinCanvasReader.read(userId);
    }

    @Transactional
    public BuildWinCanvasResponse submit(Long userId, WinCanvasSaveRequest request) {
        if (winCanvasRepository.existsSubmittedByUserIdAndCanvasType(userId, CanvasType.BUILD)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }
        append(userId, request);
        winCanvasRepository.submitByUserIdAndCanvasType(userId, CanvasType.BUILD);
        return buildWinCanvasReader.read(userId);
    }
}
