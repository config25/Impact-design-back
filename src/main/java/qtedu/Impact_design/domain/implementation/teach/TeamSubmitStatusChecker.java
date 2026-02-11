package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.storage.jpaentity.teach.TeamUser;
import qtedu.Impact_design.storage.jparepository.teach.TeamUserJpaRepository;
import qtedu.Impact_design.storage.jparepository.user.UserinfoJpaRepository;
import qtedu.Impact_design.storage.jparepository.web.FLetterOfIntent2JpaRepository;
import qtedu.Impact_design.storage.jparepository.web.FLetterOfIntentJpaRepository;
import qtedu.Impact_design.storage.jparepository.web.IdentityCanvasJpaRepository;
import qtedu.Impact_design.storage.jparepository.web.ImpactCheckJpaRepository;
import qtedu.Impact_design.storage.jparepository.web.flow_canvas.FlowCanvasJpaRepository;
import qtedu.Impact_design.storage.jparepository.web.win_canvas.WinCanvasJpaRepository;

import java.util.List;

/**
 * 팀별 제출 상태 체크
 * 각 팀의 대표작성자(writer=1)의 제출 여부를 확인
 */
@Component
@RequiredArgsConstructor
public class TeamSubmitStatusChecker {

    private final TeamUserJpaRepository teamUserJpaRepository;
    private final ImpactCheckJpaRepository impactCheckJpaRepository;
    private final IdentityCanvasJpaRepository identityCanvasJpaRepository;
    private final FlowCanvasJpaRepository flowCanvasJpaRepository;
    private final WinCanvasJpaRepository winCanvasJpaRepository;
    private final FLetterOfIntentJpaRepository fLetterOfIntentJpaRepository;
    private final FLetterOfIntent2JpaRepository fLetterOfIntent2JpaRepository;
    private final UserinfoJpaRepository userinfoJpaRepository;

    /**
     * 팀의 대표작성자 userId 조회
     */
    public Long findWriterUserId(Integer teamId) {
        List<TeamUser> teamUsers = teamUserJpaRepository.findByTeamId(teamId);
        if (teamUsers.isEmpty()) {
            return null;
        }

        List<Long> userIds = teamUsers.stream()
                .map(TeamUser::getUserId)
                .toList();

        // writer = 1인 사용자 찾기, 없으면 첫 번째 사용자
        return userinfoJpaRepository.findFirstByUserIdInAndWriter(userIds, "1")
                .map(user -> user.getUserId())
                .orElse(userIds.get(0));
    }

    /**
     * submitA: 성과관리 현황진단 (ImpactCheck)
     */
    public String checkSubmitA(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return impactCheckJpaRepository.existsByUserIdAndSubmitted(writerUserId, true)
                ? "제출" : "미제출";
    }

    /**
     * submitB: 정체성 설계 (IdentityCanvas)
     */
    public String checkSubmitB(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return identityCanvasJpaRepository.existsByUserIdAndSubmitted(writerUserId, true)
                ? "제출" : "미제출";
    }

    /**
     * submitC: 성과경로 설계 (FlowCanvas)
     */
    public String checkSubmitC(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return flowCanvasJpaRepository.existsByUserIdAndSubmitted(writerUserId, true)
                ? "제출" : "미제출";
    }

    /**
     * submitD: 전술적 실행과제 (WinCanvas - QUICK)
     */
    public String checkSubmitD(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return winCanvasJpaRepository.existsByUserIdAndCanvasTypeAndSubmitted(writerUserId, CanvasType.QUICK, true)
                ? "제출" : "미제출";
    }

    /**
     * submitE: 전략적 실행과제 (WinCanvas - BUILD)
     */
    public String checkSubmitE(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return winCanvasJpaRepository.existsByUserIdAndCanvasTypeAndSubmitted(writerUserId, CanvasType.BUILD, true)
                ? "제출" : "미제출";
    }

    /**
     * submitF: 실행과제 검증 (FLetterOfIntent + FLetterOfIntent2)
     */
    public String checkSubmitF(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        boolean intent1 = fLetterOfIntentJpaRepository.existsByStdntNoAndSubmittedAndDelYn(
                writerUserId.intValue(), true, "N");
        boolean intent2 = fLetterOfIntent2JpaRepository.existsByStdntNoAndSubmittedAndDelYn(
                writerUserId.intValue(), true, "N");
        return (intent1 || intent2) ? "제출" : "미제출";
    }
}
