package qtedu.Impact_design.domain.implementation.quickwin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.request.wincanvas.WinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.quickwin.QuickWinCanvasResponse;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.domain.implementation.wincanvas.WinCanvasMerger;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

@Component
@RequiredArgsConstructor
public class QuickWinCanvasAppender {

    private final WinCanvasRepository winCanvasRepository;
    private final WinCanvasMerger winCanvasMerger;
    private final QuickWinCanvasReader quickWinCanvasReader;

    @Transactional
    public QuickWinCanvasResponse append(Long userId, WinCanvasSaveRequest request) {
        if (winCanvasRepository.existsSubmittedByUserIdAndCanvasType(userId, CanvasType.QUICK)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }

        WinCanvasModel canvas = winCanvasRepository.findByUserIdAndCanvasType(userId, CanvasType.QUICK)
                .map(existing -> winCanvasRepository.save(WinCanvasModel.builder()
                        .canvasId(existing.getCanvasId())
                        .userId(userId)
                        .canvasType(CanvasType.QUICK)
                        .strategicGoal(request.getStrategicGoal())
                        .taskName(request.getTaskName())
                        .taskDescription(request.getTaskDescription())
                        .crisisSignal(request.getCrisisSignal())
                        .painTouchPoint(request.getPainTouchPoint())
                        .build()))
                .orElseGet(() -> winCanvasRepository.save(WinCanvasModel.builder()
                        .userId(userId)
                        .canvasType(CanvasType.QUICK)
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

        return quickWinCanvasReader.read(userId);
    }

    @Transactional
    public QuickWinCanvasResponse submit(Long userId, WinCanvasSaveRequest request) {
        if (winCanvasRepository.existsSubmittedByUserIdAndCanvasType(userId, CanvasType.QUICK)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }
        append(userId, request);
        winCanvasRepository.submitByUserIdAndCanvasType(userId, CanvasType.QUICK);
        return quickWinCanvasReader.read(userId);
    }
}
