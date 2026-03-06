package qtedu.Impact_design.domain.implementation.wincanvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.wincanvas.WinCanvasResponse;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.win_canvas.*;
import qtedu.Impact_design.domain.repository.win_canvas.*;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WinCanvasReader {

    private final WinCanvasRepository winCanvasRepository;
    private final TaskInputRepository taskInputRepository;
    private final TaskActivityRepository taskActivityRepository;
    private final TeamworkRepository teamworkRepository;
    private final TaskOutcomeRepository taskOutcomeRepository;

    public WinCanvasResponse read(Long userId, CanvasType canvasType) {
        Optional<WinCanvasModel> canvasOpt = winCanvasRepository.findByUserIdAndCanvasType(userId, canvasType);
        if (canvasOpt.isEmpty()) {
            return WinCanvasResponse.builder().build();
        }

        WinCanvasModel canvas = canvasOpt.get();
        Long canvasId = canvas.getCanvasId();

        List<TaskInputModel> inputs = taskInputRepository.findByCanvasId(canvasId);
        List<TaskActivityModel> activities = taskActivityRepository.findByCanvasId(canvasId);
        TeamworkModel teamwork = teamworkRepository.findByCanvasId(canvasId).orElse(null);
        List<TaskOutcomeModel> outcomes = taskOutcomeRepository.findByCanvasId(canvasId);

        return WinCanvasResponse.from(canvas, inputs, activities, teamwork, outcomes);
    }

    public List<WinCanvasModel> readCanvasesByUserIds(List<Long> userIds, CanvasType canvasType) {
        return winCanvasRepository.findByUserIdInAndCanvasType(userIds, canvasType);
    }
}
