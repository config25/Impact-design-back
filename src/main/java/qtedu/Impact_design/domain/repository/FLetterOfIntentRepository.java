package qtedu.Impact_design.domain.repository;

import qtedu.Impact_design.domain.model.FLetterOfIntentModel;

import java.util.List;
import java.util.Optional;

public interface FLetterOfIntentRepository {
    List<FLetterOfIntentModel> findByCanvasIdIn(List<Long> canvasIds);

    // 사용자의 모든 투자 목록
    List<FLetterOfIntentModel> findByUserId(Long userId);

    // 특정 팀에 대한 투자 조회
    Optional<FLetterOfIntentModel> findByUserIdAndTargetTeamId(Long userId, Integer targetTeamId);

    // 투자 저장/수정
    FLetterOfIntentModel save(FLetterOfIntentModel model);

    // 제출 완료 여부
    boolean existsSubmittedByUserId(Long userId);

    // 제출 처리
    void submitAllByUserId(Long userId);

    // 내 팀을 투자 대상으로 한 모든 투자 (F3용)
    List<FLetterOfIntentModel> findByTargetTeamId(Integer teamId);
}
