package qtedu.Impact_design.domain.repository.win_canvas;

import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;

import java.util.List;
import java.util.Optional;

public interface WinCanvasRepository {
    Optional<WinCanvasModel> findByUserIdAndCanvasType(Long userId, CanvasType canvasType);
    WinCanvasModel save(WinCanvasModel winCanvasModel);
    List<WinCanvasModel> findByUserIdInAndCanvasType(List<Long> userIds, CanvasType canvasType);
}
