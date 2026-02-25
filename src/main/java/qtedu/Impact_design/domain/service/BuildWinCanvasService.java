package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.request.buildwin.BuildWinCanvasSaveRequest;
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

    public BuildWinCanvasResponse saveBuildWinCanvas(Long userId, BuildWinCanvasSaveRequest request) {
        return buildWinCanvasAppender.append(userId, request);
    }

    public List<WinCanvasModel> getCanvasesByUserIds(List<Long> userIds) {
        return buildWinCanvasReader.readCanvasesByUserIds(userIds);
    }

    public List<FLetterOfIntent2Model> getEvaluationsByCanvasIds(List<Long> canvasIds) {
        return buildWinCanvasReader.readEvaluationsByCanvasIds(canvasIds);
    }

    public BuildWinCanvasResponse submitBuildWinCanvas(Long userId, BuildWinCanvasSaveRequest request) {
        return buildWinCanvasAppender.submit(userId, request);
    }
}
