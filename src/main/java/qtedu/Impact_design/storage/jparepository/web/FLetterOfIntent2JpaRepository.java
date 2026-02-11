package qtedu.Impact_design.storage.jparepository.web;

import org.springframework.data.jpa.repository.JpaRepository;
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

    // 내 팀을 투자 대상으로 한 모든 투자 (F3용)
    List<FLetterOfIntent2> findByInvestmentTargetAndDelYnAndSubmitted(String investmentTarget, String delYn, Boolean submitted);
}
