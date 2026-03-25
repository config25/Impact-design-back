package qtedu.Impact_design.storage.jparepository.web;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.storage.jpaentity.FLetterOfIntent;

import java.util.List;
import java.util.Optional;

@Repository
public interface FLetterOfIntentJpaRepository extends JpaRepository<FLetterOfIntent, Integer> {
    List<FLetterOfIntent> findByCanvasIdIn(List<Long> canvasIds);

    List<FLetterOfIntent> findByUserIdAndDelYnAndCanvasType(Long userId, String delYn, CanvasType canvasType);

    Optional<FLetterOfIntent> findByUserIdAndInvestmentTargetAndDelYnAndCanvasType(Long userId, String investmentTarget, String delYn, CanvasType canvasType);

    List<FLetterOfIntent> findByInvestmentTargetAndDelYnAndCanvasType(String investmentTarget, String delYn, CanvasType canvasType);
    List<FLetterOfIntent> findByInvestmentTargetInAndDelYnAndCanvasType(List<String> investmentTargets, String delYn, CanvasType canvasType);

    boolean existsByUserIdAndSubmittedAndDelYnAndCanvasType(Long userId, Boolean submitted, String delYn, CanvasType canvasType);
    boolean existsByUserIdAndInvestmentTargetAndSubmittedAndDelYnAndCanvasType(Long userId, String investmentTarget, Boolean submitted, String delYn, CanvasType canvasType);
    List<FLetterOfIntent> findByUserIdInAndSubmittedAndDelYnAndCanvasType(List<Long> userIds, Boolean submitted, String delYn, CanvasType canvasType);

    @Modifying
    @Query(value = "UPDATE f_letter_of_intent SET investment_target = :teamId WHERE canvas_id IN (SELECT canvas_id FROM win_canvas WHERE user_id = :userId)", nativeQuery = true)
    void updateInvestmentTargetByCanvasOwner(@Param("userId") Long userId, @Param("teamId") String teamId);
}
