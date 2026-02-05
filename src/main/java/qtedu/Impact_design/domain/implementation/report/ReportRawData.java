package qtedu.Impact_design.domain.implementation.report;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.model.FLetterOfIntent2Model;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.model.ImpactCheckModel;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;
import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;

import java.util.List;

@Getter
@Builder
public class ReportRawData {
    private final List<ImpactCheckModel> impactChecks;
    private final List<IdentityCanvasModel> identityCanvases;
    private final List<FlowCanvasModel> flowCanvases;
    private final List<TacticalModel> tacticals;
    private final List<StrategicActivityModel> strategicActivities;
    private final List<WinCanvasModel> quickCanvases;
    private final List<WinCanvasModel> buildCanvases;
    private final List<FLetterOfIntentModel> quickIntents;
    private final List<FLetterOfIntent2Model> buildIntents;
}
