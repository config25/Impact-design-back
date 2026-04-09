package qtedu.Impact_design.api.dto.response.report;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.dto.response.wincanvas.WinCanvasResponse;

import java.util.List;

@Getter
@Builder
public class GameCanvasesResponse {

    private String imageUrl;

    private List<IdentityCanvasItem> identityCanvases;
    private List<FlowCanvasItem> flowCanvases;
    private List<WinCanvasItem> quickWinCanvases;
    private List<WinCanvasItem> buildWinCanvases;

    @Getter
    @Builder
    public static class IdentityCanvasItem {
        private Integer teamId;
        private String teamName;
        private Long writerUserId;
        private IdentityCanvasResponse canvas;
    }

    @Getter
    @Builder
    public static class FlowCanvasItem {
        private Integer teamId;
        private String teamName;
        private Long writerUserId;
        private FlowCanvasResponse canvas;
    }

    @Getter
    @Builder
    public static class WinCanvasItem {
        private Integer teamId;
        private String teamName;
        private Long writerUserId;
        private WinCanvasResponse canvas;
    }
}
