package qtedu.Impact_design.domain.implementation.buildwin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.buildwin.BuildWinCanvasResponse;
import qtedu.Impact_design.domain.model.FLetterOfIntent2Model;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.win_canvas.*;
import qtedu.Impact_design.domain.repository.FLetterOfIntent2Repository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.win_canvas.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BuildWinCanvasReader {

    private final WinCanvasRepository winCanvasRepository;
    private final TaskInputRepository taskInputRepository;
    private final TaskActivityRepository taskActivityRepository;
    private final TeamworkRepository teamworkRepository;
    private final TaskOutcomeRepository taskOutcomeRepository;
    private final FLetterOfIntent2Repository fLetterOfIntent2Repository;
    private final TbGameRepository tbGameRepository;

    public BuildWinCanvasResponse read(Long userId) {
        Optional<WinCanvasModel> canvasOpt = winCanvasRepository.findByUserIdAndCanvasType(userId, CanvasType.BUILD);
        if (canvasOpt.isEmpty()) {
            return null;
        }

        WinCanvasModel canvas = canvasOpt.get();
        Long canvasId = canvas.getCanvasId();

        List<TaskInputModel> inputs = taskInputRepository.findByCanvasId(canvasId);
        List<TaskActivityModel> activities = taskActivityRepository.findByCanvasId(canvasId);
        TeamworkModel teamwork = teamworkRepository.findByCanvasId(canvasId).orElse(null);
        List<TaskOutcomeModel> outcomes = taskOutcomeRepository.findByCanvasId(canvasId);
        String imageUrl = tbGameRepository.findGameImageUrlByUserId(userId);

        return BuildWinCanvasResponse.from(canvas, inputs, activities, teamwork, outcomes, imageUrl);
    }

    public List<WinCanvasModel> readCanvasesByUserIds(List<Long> userIds) {
        return winCanvasRepository.findByUserIdInAndCanvasType(userIds, CanvasType.BUILD);
    }

    public List<FLetterOfIntent2Model> readEvaluationsByCanvasIds(List<Long> canvasIds) {
        return canvasIds.isEmpty() ? Collections.emptyList() : fLetterOfIntent2Repository.findByCanvasIdIn(canvasIds);
    }
}
