package qtedu.Impact_design.storage.jparepository.web;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.FLetterOfIntent;

import java.util.List;
import java.util.Optional;

@Repository
public interface FLetterOfIntentJpaRepository extends JpaRepository<FLetterOfIntent, Integer> {
    List<FLetterOfIntent> findByCanvasIdIn(List<Long> canvasIds);

    // 사용자의 모든 투자 의향서 (삭제되지 않은 것)
    List<FLetterOfIntent> findByStdntNoAndDelYn(Long stdntNo, String delYn);

    // 특정 팀에 대한 투자 의향서
    Optional<FLetterOfIntent> findByStdntNoAndInvestmentTargetAndDelYn(Long stdntNo, String investmentTarget, String delYn);

    // 특정 팀이 받은 모든 투자
    List<FLetterOfIntent> findByInvestmentTargetAndDelYn(String investmentTarget, String delYn);

    // 제출 완료 여부 확인
    boolean existsByStdntNoAndSubmittedAndDelYn(Long stdntNo, Boolean submitted, String delYn);

    // 내 팀을 투자 대상으로 한 모든 투자 (F3용)
    List<FLetterOfIntent> findByInvestmentTargetAndDelYnAndSubmitted(String investmentTarget, String delYn, Boolean submitted);

    // 팀 이동 시 investment_target 일괄 업데이트
    @Modifying
    @Query(value = "UPDATE f_letter_of_intent SET investment_target = :teamId WHERE canvas_id IN (SELECT canvas_id FROM win_canvas WHERE user_id = :userId)", nativeQuery = true)
    void updateInvestmentTargetByCanvasOwner(@Param("userId") Long userId, @Param("teamId") String teamId);
}
