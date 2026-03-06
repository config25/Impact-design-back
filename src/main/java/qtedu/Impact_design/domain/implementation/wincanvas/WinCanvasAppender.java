package qtedu.Impact_design.domain.implementation.wincanvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.request.wincanvas.WinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.wincanvas.WinCanvasResponse;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

@Component
@RequiredArgsConstructor
public class WinCanvasAppender {

    private final WinCanvasRepository winCanvasRepository;
    private final WinCanvasMerger winCanvasMerger;
    private final WinCanvasReader winCanvasReader;

    @Transactional
    public WinCanvasResponse append(Long userId, WinCanvasSaveRequest request, CanvasType canvasType) {
        if (winCanvasRepository.existsSubmittedByUserIdAndCanvasType(userId, canvasType)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }

        Long existingId = winCanvasRepository.findByUserIdAndCanvasType(userId, canvasType)
                .map(WinCanvasModel::getCanvasId).orElse(null);

        WinCanvasModel canvas = winCanvasRepository.save(WinCanvasModel.builder()
                .canvasId(existingId)
                .userId(userId)
                .canvasType(canvasType)
                .strategicGoal(request.getStrategicGoal())
                .taskName(request.getTaskName())
                .taskDescription(request.getTaskDescription())
                .crisisSignal(request.getCrisisSignal())
                .painTouchPoint(request.getPainTouchPoint())
                .build());

        Long canvasId = canvas.getCanvasId();

        winCanvasMerger.mergeTaskInputs(canvasId, request);
        winCanvasMerger.mergeTaskActivities(canvasId, request);
        winCanvasMerger.mergeTeamwork(canvasId, request);
        winCanvasMerger.mergeTaskOutcomes(canvasId, request);

        return winCanvasReader.read(userId, canvasType);
    }

    @Transactional
    public WinCanvasResponse submit(Long userId, WinCanvasSaveRequest request, CanvasType canvasType) {
        if (winCanvasRepository.existsSubmittedByUserIdAndCanvasType(userId, canvasType)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }
        append(userId, request, canvasType);
        winCanvasRepository.submitByUserIdAndCanvasType(userId, canvasType);
        return winCanvasReader.read(userId, canvasType);
    }
}
