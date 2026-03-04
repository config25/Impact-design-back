package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.request.wincanvas.WinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.buildwin.BuildWinCanvasResponse;
import qtedu.Impact_design.domain.implementation.buildwin.BuildWinCanvasAppender;
import qtedu.Impact_design.domain.implementation.buildwin.BuildWinCanvasReader;
import qtedu.Impact_design.domain.model.FLetterOfIntent2Model;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildWinCanvasService {

    private final BuildWinCanvasReader buildWinCanvasReader;
    private final BuildWinCanvasAppender buildWinCanvasAppender;

    public BuildWinCanvasResponse getBuildWinCanvas(Long userId) {
        return buildWinCanvasReader.read(userId);
    }

    public BuildWinCanvasResponse saveBuildWinCanvas(Long userId, WinCanvasSaveRequest request) {
        return buildWinCanvasAppender.append(userId, request);
    }
    // 리포트용
    public List<WinCanvasModel> getCanvasesByUserIds(List<Long> userIds) {
        return buildWinCanvasReader.readCanvasesByUserIds(userIds);
    }

    public BuildWinCanvasResponse submitBuildWinCanvas(Long userId, WinCanvasSaveRequest request) {
        return buildWinCanvasAppender.submit(userId, request);
    }
}
