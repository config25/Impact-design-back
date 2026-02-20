package qtedu.Impact_design.domain.implementation.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.FLetterOfIntent2Repository;
import qtedu.Impact_design.domain.service.*;

import java.util.Collections;
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
    private final FLetterOfIntentRepository fLetterOfIntentRepository;
    private final FLetterOfIntent2Repository fLetterOfIntent2Repository;

    public ReportRawData load(Integer teamId) {
        List<Long> userIds = teamService.getTeamMemberUserIdsByTeamId(teamId);

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
                // QUICK 캔버스 평가는 f_letter_of_intent2에 저장됨
                .quickIntents(quickCanvasIds.isEmpty() ? Collections.emptyList()
                        : fLetterOfIntent2Repository.findByCanvasIdIn(quickCanvasIds))
                // BUILD 캔버스 평가는 f_letter_of_intent에 저장됨
                .buildIntents(buildCanvasIds.isEmpty() ? Collections.emptyList()
                        : fLetterOfIntentRepository.findByCanvasIdIn(buildCanvasIds))
                .build();
    }
}
