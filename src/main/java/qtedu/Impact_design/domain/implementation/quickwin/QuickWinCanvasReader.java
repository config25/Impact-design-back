package qtedu.Impact_design.domain.implementation.quickwin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.quickwin.QuickWinCanvasResponse;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.win_canvas.*;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.win_canvas.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QuickWinCanvasReader {

    private final WinCanvasRepository winCanvasRepository;
    private final TaskInputRepository taskInputRepository;
    private final TaskActivityRepository taskActivityRepository;
    private final TeamworkRepository teamworkRepository;
    private final TaskOutcomeRepository taskOutcomeRepository;
    private final FLetterOfIntentRepository fLetterOfIntentRepository;
    private final TbGameRepository tbGameRepository;

    public QuickWinCanvasResponse read(Long userId) {
        String imageUrl = tbGameRepository.findGameImageUrlByUserId(userId);
        Optional<WinCanvasModel> canvasOpt = winCanvasRepository.findByUserIdAndCanvasType(userId, CanvasType.QUICK);
        if (canvasOpt.isEmpty()) {
            return QuickWinCanvasResponse.builder().imageUrl(imageUrl).build();
        }

        WinCanvasModel canvas = canvasOpt.get();
        Long canvasId = canvas.getCanvasId();

        List<TaskInputModel> inputs = taskInputRepository.findByCanvasId(canvasId);
        List<TaskActivityModel> activities = taskActivityRepository.findByCanvasId(canvasId);
        TeamworkModel teamwork = teamworkRepository.findByCanvasId(canvasId).orElse(null);
        List<TaskOutcomeModel> outcomes = taskOutcomeRepository.findByCanvasId(canvasId);

        return QuickWinCanvasResponse.from(canvas, inputs, activities, teamwork, outcomes, imageUrl);
    }

    public List<WinCanvasModel> readCanvasesByUserIds(List<Long> userIds) {
        return winCanvasRepository.findByUserIdInAndCanvasType(userIds, CanvasType.QUICK);
    }

    public List<FLetterOfIntentModel> readEvaluationsByCanvasIds(List<Long> canvasIds) {
        return canvasIds.isEmpty() ? Collections.emptyList() : fLetterOfIntentRepository.findByCanvasIdIn(canvasIds);
    }
}
