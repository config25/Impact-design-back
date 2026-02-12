package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntent2Repository;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.ImpactCheckRepository;
import qtedu.Impact_design.domain.repository.IdentityCanvasRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.flow_canvas.FlowCanvasRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;

import java.util.List;

/**
 * 팀별 제출 상태 체크
 * 각 팀의 대표작성자(writer=1)의 제출 여부를 확인
 */
@Component
@RequiredArgsConstructor
public class TeamSubmitStatusChecker {

    private final TeamUserRepository teamUserRepository;
    private final ImpactCheckRepository impactCheckRepository;
    private final IdentityCanvasRepository identityCanvasRepository;
    private final FlowCanvasRepository flowCanvasRepository;
    private final WinCanvasRepository winCanvasRepository;
    private final FLetterOfIntentRepository fLetterOfIntentRepository;
    private final FLetterOfIntent2Repository fLetterOfIntent2Repository;
    private final UserinfoRepository userinfoRepository;

    /**
     * 팀의 대표작성자 userId 조회
     */
    public Long findWriterUserId(Integer teamId) {
        List<TeamUserModel> teamUsers = teamUserRepository.findByTeamId(teamId);
        if (teamUsers.isEmpty()) {
            return null;
        }

        List<Long> userIds = teamUsers.stream()
                .map(TeamUserModel::getUserId)
                .toList();

        // writer = 1인 사용자 찾기, 없으면 첫 번째 사용자
        return userinfoRepository.findWriterByUserIds(userIds)
                .map(user -> user.getUserId())
                .orElse(userIds.get(0));
    }

    /**
     * submitA: 성과관리 현황진단 (ImpactCheck)
     */
    public String checkSubmitA(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return impactCheckRepository.existsSubmittedByUserId(writerUserId)
                ? "제출" : "미제출";
    }

    /**
     * submitB: 정체성 설계 (IdentityCanvas)
     */
    public String checkSubmitB(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return identityCanvasRepository.existsSubmittedByUserId(writerUserId)
                ? "제출" : "미제출";
    }

    /**
     * submitC: 성과경로 설계 (FlowCanvas)
     */
    public String checkSubmitC(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return flowCanvasRepository.existsSubmittedByUserId(writerUserId)
                ? "제출" : "미제출";
    }

    /**
     * submitD: 전술적 실행과제 (WinCanvas - QUICK)
     */
    public String checkSubmitD(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return winCanvasRepository.existsSubmittedByUserIdAndCanvasType(writerUserId, CanvasType.QUICK)
                ? "제출" : "미제출";
    }

    /**
     * submitE: 전략적 실행과제 (WinCanvas - BUILD)
     */
    public String checkSubmitE(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        return winCanvasRepository.existsSubmittedByUserIdAndCanvasType(writerUserId, CanvasType.BUILD)
                ? "제출" : "미제출";
    }

    /**
     * submitF: 실행과제 검증 (FLetterOfIntent + FLetterOfIntent2)
     */
    public String checkSubmitF(Long writerUserId) {
        if (writerUserId == null) return "미제출";
        boolean intent1 = fLetterOfIntentRepository.existsSubmittedByUserId(writerUserId);
        boolean intent2 = fLetterOfIntent2Repository.existsSubmittedByUserId(writerUserId);
        return (intent1 || intent2) ? "제출" : "미제출";
    }
}
