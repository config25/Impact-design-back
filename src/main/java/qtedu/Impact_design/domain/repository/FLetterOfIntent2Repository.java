package qtedu.Impact_design.domain.repository;

import qtedu.Impact_design.domain.model.FLetterOfIntent2Model;

import java.util.List;
import java.util.Optional;

public interface FLetterOfIntent2Repository {
    List<FLetterOfIntent2Model> findByCanvasIdIn(List<Long> canvasIds);

    List<FLetterOfIntent2Model> findByUserId(Long userId);

    Optional<FLetterOfIntent2Model> findByUserIdAndTargetTeamId(Long userId, Integer targetTeamId);

    FLetterOfIntent2Model save(FLetterOfIntent2Model model);

    boolean existsSubmittedByUserId(Long userId);

    void submitAllByUserId(Long userId);

    // 내 팀을 투자 대상으로 한 모든 투자 (F3용)
    List<FLetterOfIntent2Model> findByTargetTeamId(Integer teamId);

    void updateInvestmentTargetByCanvasOwner(Long userId, String teamId);
}
