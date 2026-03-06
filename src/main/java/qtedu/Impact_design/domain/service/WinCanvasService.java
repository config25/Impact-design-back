package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.request.wincanvas.WinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.wincanvas.WinCanvasResponse;
import qtedu.Impact_design.domain.implementation.wincanvas.WinCanvasAppender;
import qtedu.Impact_design.domain.implementation.wincanvas.WinCanvasReader;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WinCanvasService {

    private final WinCanvasReader winCanvasReader;
    private final WinCanvasAppender winCanvasAppender;

    public WinCanvasResponse getWinCanvas(Long userId, CanvasType canvasType) {
        return winCanvasReader.read(userId, canvasType);
    }

    public WinCanvasResponse saveWinCanvas(Long userId, WinCanvasSaveRequest request, CanvasType canvasType) {
        return winCanvasAppender.append(userId, request, canvasType);
    }

    public WinCanvasResponse submitWinCanvas(Long userId, WinCanvasSaveRequest request, CanvasType canvasType) {
        return winCanvasAppender.submit(userId, request, canvasType);
    }

    public List<WinCanvasModel> getCanvasesByUserIds(List<Long> userIds, CanvasType canvasType) {
        return winCanvasReader.readCanvasesByUserIds(userIds, canvasType);
    }
}
