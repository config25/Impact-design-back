package qtedu.Impact_design.domain.implementation.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.domain.service.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReportDataLoader {

    private final TeamService teamService;
    private final ImpactCheckService impactCheckService;
    private final IdentityCanvasService identityCanvasService;
    private final FlowCanvasService flowCanvasService;
    private final QuickWinCanvasService quickWinCanvasService;
    private final BuildWinCanvasService buildWinCanvasService;

    public ReportRawData load(Long userId) {
        List<Long> userIds = teamService.getTeamMemberUserIds(userId);

        List<FlowCanvasModel> flowCanvases = flowCanvasService.getFlowCanvasesByUserIds(userIds);
        List<Long> goalIds = flowCanvases.stream()
                .map(FlowCanvasModel::getGoalId)
                .collect(Collectors.toList());

        List<WinCanvasModel> quickCanvases = quickWinCanvasService.getCanvasesByUserIds(userIds);
        List<WinCanvasModel> buildCanvases = buildWinCanvasService.getCanvasesByUserIds(userIds);

        List<Long> quickCanvasIds = quickCanvases.stream().map(WinCanvasModel::getCanvasId).collect(Collectors.toList());
        List<Long> buildCanvasIds = buildCanvases.stream().map(WinCanvasModel::getCanvasId).collect(Collectors.toList());

        return ReportRawData.builder()
                .impactChecks(impactCheckService.getImpactChecksByUserIds(userIds))
                .identityCanvases(identityCanvasService.getIdentityCanvasesByUserIds(userIds))
                .flowCanvases(flowCanvases)
                .tacticals(flowCanvasService.getTacticalsByGoalIds(goalIds))
                .strategicActivities(flowCanvasService.getStrategicActivitiesByGoalIds(goalIds))
                .quickCanvases(quickCanvases)
                .buildCanvases(buildCanvases)
                .quickIntents(quickWinCanvasService.getEvaluationsByCanvasIds(quickCanvasIds))
                .buildIntents(buildWinCanvasService.getEvaluationsByCanvasIds(buildCanvasIds))
                .build();
    }
}
