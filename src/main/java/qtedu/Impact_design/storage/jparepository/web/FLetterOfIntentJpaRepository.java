package qtedu.Impact_design.storage.jparepository.web;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.FLetterOfIntent;

import java.util.List;
import java.util.Optional;

@Repository
public interface FLetterOfIntentJpaRepository extends JpaRepository<FLetterOfIntent, Integer> {
    List<FLetterOfIntent> findByCanvasIdIn(List<Long> canvasIds);

    // 사용자의 모든 투자 의향서 (삭제되지 않은 것)
    List<FLetterOfIntent> findByStdntNoAndDelYn(Integer stdntNo, String delYn);

    // 특정 팀에 대한 투자 의향서
    Optional<FLetterOfIntent> findByStdntNoAndInvestmentTargetAndDelYn(Integer stdntNo, String investmentTarget, String delYn);

    // 특정 팀이 받은 모든 투자
    List<FLetterOfIntent> findByInvestmentTargetAndDelYn(String investmentTarget, String delYn);

    // 제출 완료 여부 확인
    boolean existsByStdntNoAndSubmittedAndDelYn(Integer stdntNo, Boolean submitted, String delYn);

    // 내 팀을 투자 대상으로 한 모든 투자 (F3용)
    List<FLetterOfIntent> findByInvestmentTargetAndDelYnAndSubmitted(String investmentTarget, String delYn, Boolean submitted);
}
