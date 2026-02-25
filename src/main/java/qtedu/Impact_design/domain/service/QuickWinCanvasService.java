package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.request.quickwin.QuickWinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.quickwin.QuickWinCanvasResponse;
import qtedu.Impact_design.domain.implementation.quickwin.QuickWinCanvasAppender;
import qtedu.Impact_design.domain.implementation.quickwin.QuickWinCanvasReader;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuickWinCanvasService {

    private final QuickWinCanvasReader quickWinCanvasReader;
    private final QuickWinCanvasAppender quickWinCanvasAppender;

    public QuickWinCanvasResponse getQuickWinCanvas(Long userId) {
        return quickWinCanvasReader.read(userId);
    }

    public QuickWinCanvasResponse saveQuickWinCanvas(Long userId, QuickWinCanvasSaveRequest request) {
        return quickWinCanvasAppender.append(userId, request);
    }

    public List<WinCanvasModel> getCanvasesByUserIds(List<Long> userIds) {
        return quickWinCanvasReader.readCanvasesByUserIds(userIds);
    }

    public List<FLetterOfIntentModel> getEvaluationsByCanvasIds(List<Long> canvasIds) {
        return quickWinCanvasReader.readEvaluationsByCanvasIds(canvasIds);
    }

    public QuickWinCanvasResponse submitQuickWinCanvas(Long userId, QuickWinCanvasSaveRequest request) {
        return quickWinCanvasAppender.submit(userId, request);
    }
}
