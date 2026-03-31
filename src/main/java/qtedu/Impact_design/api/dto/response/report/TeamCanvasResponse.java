package qtedu.Impact_design.api.dto.response.report;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.dto.response.wincanvas.WinCanvasResponse;

@Getter
@Builder
public class TeamCanvasResponse {

    private Integer teamId;
    private String teamName;
    private Long writerUserId;
    private String imageUrl;

    private IdentityCanvasResponse identityCanvas;
    private FlowCanvasResponse flowCanvas;
    private WinCanvasResponse quickWinCanvas;
    private WinCanvasResponse buildWinCanvas;
}
