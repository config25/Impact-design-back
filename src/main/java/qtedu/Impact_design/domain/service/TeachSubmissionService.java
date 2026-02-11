package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.response.buildwin.BuildWinCanvasResponse;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.api.dto.response.funding.FundingInvestmentResponse;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.api.dto.response.quickwin.QuickWinCanvasResponse;
import qtedu.Impact_design.api.dto.response.teach.TeamSubmissionListResponse;
import qtedu.Impact_design.domain.implementation.teach.TeachSubmissionReader;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeachSubmissionService {

    private final TeachSubmissionReader teachSubmissionReader;

    public List<TeamSubmissionListResponse> getSubmissionList(Integer gameId) {
        return teachSubmissionReader.getSubmissionList(gameId);
    }

    public ImpactCheckResponse getImpactCheck(Integer teamId) {
        return teachSubmissionReader.getImpactCheck(teamId);
    }

    public IdentityCanvasResponse getIdentityCanvas(Integer teamId) {
        return teachSubmissionReader.getIdentityCanvas(teamId);
    }

    public FlowCanvasResponse getFlowCanvas(Integer teamId) {
        return teachSubmissionReader.getFlowCanvas(teamId);
    }

    public QuickWinCanvasResponse getQuickWinCanvas(Integer teamId) {
        return teachSubmissionReader.getQuickWinCanvas(teamId);
    }

    public BuildWinCanvasResponse getBuildWinCanvas(Integer teamId) {
        return teachSubmissionReader.getBuildWinCanvas(teamId);
    }

    public List<FundingInvestmentResponse> getFunding(Integer teamId) {
        return teachSubmissionReader.getFunding(teamId);
    }
}
