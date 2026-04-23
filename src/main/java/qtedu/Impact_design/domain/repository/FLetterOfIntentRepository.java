package qtedu.Impact_design.domain.repository;

import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.model.en.CanvasType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FLetterOfIntentRepository {
    List<FLetterOfIntentModel> findByCanvasIdIn(List<Long> canvasIds);

    List<FLetterOfIntentModel> findByUserId(Long userId, CanvasType canvasType);

    Optional<FLetterOfIntentModel> findByUserIdAndTargetTeamId(Long userId, Integer targetTeamId, CanvasType canvasType);

    FLetterOfIntentModel save(FLetterOfIntentModel model);

    boolean existsSubmittedByUserId(Long userId, CanvasType canvasType);

    boolean existsSubmittedByUserIdAndTarget(Long userId, Integer targetTeamId, CanvasType canvasType);

    void submitAllByUserId(Long userId, CanvasType canvasType);

    void rollbackAllByUserId(Long userId, CanvasType canvasType);

    List<FLetterOfIntentModel> findByTargetTeamId(Integer teamId, CanvasType canvasType);
    List<FLetterOfIntentModel> findByTargetTeamIds(List<Integer> teamIds, CanvasType canvasType);

    void updateInvestmentTargetByCanvasOwner(Long userId, String teamId);

    Set<Long> findSubmittedUserIds(List<Long> userIds, CanvasType canvasType);
}
