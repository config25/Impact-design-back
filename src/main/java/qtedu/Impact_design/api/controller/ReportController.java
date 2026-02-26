package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/api/teach")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/report/{teamId}")
    public ResponseEntity<HttpResponse<ReportResponse>> getReport(
            @CurrentUser UserId userId,
            @PathVariable Integer teamId
    ) {
        ReportResponse response = reportService.getReport(teamId);
        return ResponseHelper.success(response);
    }

    @GetMapping("/report/bulk/{gameId}")
    public ResponseEntity<HttpResponse<List<ReportResponse>>> getReportsByGameId(
            @CurrentUser UserId userId,
            @PathVariable Integer gameId
    ) {
        List<ReportResponse> responses = reportService.getReportsByGameId(gameId);
        return ResponseHelper.success(responses);
    }
}
