package qtedu.Impact_design.domain.implementation.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.buildwin.BuildWinCanvasResponse;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.api.dto.response.funding.FundingInvestmentResponse;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.api.dto.response.quickwin.QuickWinCanvasResponse;
import qtedu.Impact_design.api.dto.response.teach.TeamSubmissionListResponse;
import qtedu.Impact_design.domain.implementation.buildwin.BuildWinCanvasReader;
import qtedu.Impact_design.domain.implementation.flowcanvas.FlowCanvasReader;
import qtedu.Impact_design.domain.implementation.funding.FundingReader;
import qtedu.Impact_design.domain.implementation.identitycanvas.IdentityCanvasReader;
import qtedu.Impact_design.domain.implementation.impactcheck.ImpactCheckReader;
import qtedu.Impact_design.domain.implementation.quickwin.QuickWinCanvasReader;
import qtedu.Impact_design.storage.jparepository.teach.GameTeamJpaRepository;
import qtedu.Impact_design.storage.jparepository.teach.TbTeamJpaRepository;
import qtedu.Impact_design.storage.jparepository.teach.TeamUserJpaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeachSubmissionReader {

    private final GameTeamJpaRepository gameTeamJpaRepository;
    private final TbTeamJpaRepository tbTeamJpaRepository;
    private final TeamUserJpaRepository teamUserJpaRepository;
    private final TeamSubmitStatusChecker submitStatusChecker;

    // 기존 Reader들 재사용
    private final ImpactCheckReader impactCheckReader;
    private final IdentityCanvasReader identityCanvasReader;
    private final FlowCanvasReader flowCanvasReader;
    private final QuickWinCanvasReader quickWinCanvasReader;
    private final BuildWinCanvasReader buildWinCanvasReader;
    private final FundingReader fundingReader;

    public List<TeamSubmissionListResponse> getSubmissionList(Integer gameId) {
        List<Integer> teamIds = gameTeamJpaRepository.findTeamIdsByGameId(gameId);

        return teamIds.stream()
                .map(teamId -> tbTeamJpaRepository.findByTeamId(teamId).orElse(null))
                .filter(team -> team != null && (team.getStatus() == null || team.getStatus() != -1))
                .map(team -> {
                    int numUser = teamUserJpaRepository.countByTeamId(team.getTeamId());
                    Long writerUserId = submitStatusChecker.findWriterUserId(team.getTeamId());

                    return TeamSubmissionListResponse.builder()
                            .teamId(team.getTeamId())
                            .teamName(team.getName())
                            .sequence(team.getSequence())
                            .numUser(numUser)
                            .submitA(submitStatusChecker.checkSubmitA(writerUserId))
                            .submitB(submitStatusChecker.checkSubmitB(writerUserId))
                            .submitC(submitStatusChecker.checkSubmitC(writerUserId))
                            .submitD(submitStatusChecker.checkSubmitD(writerUserId))
                            .submitE(submitStatusChecker.checkSubmitE(writerUserId))
                            .submitF(submitStatusChecker.checkSubmitF(writerUserId))
                            .build();
                })
                .collect(Collectors.toList());
    }

    public ImpactCheckResponse getImpactCheck(Integer teamId) {
        Long writerUserId = submitStatusChecker.findWriterUserId(teamId);
        if (writerUserId == null) return null;
        return impactCheckReader.read(writerUserId);
    }

    public IdentityCanvasResponse getIdentityCanvas(Integer teamId) {
        Long writerUserId = submitStatusChecker.findWriterUserId(teamId);
        if (writerUserId == null) return null;
        return identityCanvasReader.read(writerUserId);
    }

    public FlowCanvasResponse getFlowCanvas(Integer teamId) {
        Long writerUserId = submitStatusChecker.findWriterUserId(teamId);
        if (writerUserId == null) return null;
        return flowCanvasReader.read(writerUserId);
    }

    public QuickWinCanvasResponse getQuickWinCanvas(Integer teamId) {
        Long writerUserId = submitStatusChecker.findWriterUserId(teamId);
        if (writerUserId == null) return null;
        return quickWinCanvasReader.read(writerUserId);
    }

    public BuildWinCanvasResponse getBuildWinCanvas(Integer teamId) {
        Long writerUserId = submitStatusChecker.findWriterUserId(teamId);
        if (writerUserId == null) return null;
        return buildWinCanvasReader.read(writerUserId);
    }

    public List<FundingInvestmentResponse> getFunding(Integer teamId) {
        Long writerUserId = submitStatusChecker.findWriterUserId(teamId);
        if (writerUserId == null) return List.of();
        return fundingReader.readByUserId(writerUserId);
    }
}
