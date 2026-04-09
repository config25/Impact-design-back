package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qtedu.Impact_design.api.dto.response.report.GameCanvasesResponse;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.implementation.report.ReportFacade;
import qtedu.Impact_design.domain.model.UserId;

@RestController
@RequestMapping("/api/teach")
@RequiredArgsConstructor
public class ReportController {

    private final ReportFacade reportFacade;

    @GetMapping("/report/{teamId}")
    public ResponseEntity<HttpResponse<ReportResponse>> getReport(
            @CurrentUser UserId userId,
            @PathVariable Integer teamId
    ) {
        ReportResponse response = reportFacade.getReport(teamId);
        return ResponseHelper.success(response);
    }

    @GetMapping("/report/canvas/{gameId}")
    public ResponseEntity<HttpResponse<GameCanvasesResponse>> getTeamCanvases(
            @CurrentUser UserId userId,
            @PathVariable Integer gameId
    ) {
        GameCanvasesResponse response = reportFacade.getTeamCanvases(gameId);
        return ResponseHelper.success(response);
    }
}
