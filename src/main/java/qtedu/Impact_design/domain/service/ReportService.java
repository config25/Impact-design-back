package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.domain.implementation.report.ReportFacade;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportFacade reportFacade;

    public ReportResponse getReport(Long userId) {
        return reportFacade.getReport(userId);
    }
}
