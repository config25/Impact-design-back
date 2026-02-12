package qtedu.Impact_design.storage.jparepository.web;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.FLetterOfIntent2;

import java.util.List;
import java.util.Optional;

@Repository
public interface FLetterOfIntent2JpaRepository extends JpaRepository<FLetterOfIntent2, Integer> {
    List<FLetterOfIntent2> findByCanvasIdIn(List<Long> canvasIds);

    List<FLetterOfIntent2> findByStdntNoAndDelYn(Integer stdntNo, String delYn);

    Optional<FLetterOfIntent2> findByStdntNoAndInvestmentTargetAndDelYn(Integer stdntNo, String investmentTarget, String delYn);

    boolean existsByStdntNoAndSubmittedAndDelYn(Integer stdntNo, Boolean submitted, String delYn);

    // 특정 팀이 받은 모든 투자
    List<FLetterOfIntent2> findByInvestmentTargetAndDelYn(String investmentTarget, String delYn);

    // 내 팀을 투자 대상으로 한 모든 투자 (F3용)
    List<FLetterOfIntent2> findByInvestmentTargetAndDelYnAndSubmitted(String investmentTarget, String delYn, Boolean submitted);

    // 팀 이동 시 investment_target 일괄 업데이트
    @Modifying
    @Query(value = "UPDATE f_letter_of_intent2 SET investment_target = :teamId WHERE canvas_id IN (SELECT canvas_id FROM win_canvas WHERE user_id = :userId)", nativeQuery = true)
    void updateInvestmentTargetByCanvasOwner(@Param("userId") Long userId, @Param("teamId") String teamId);
}
