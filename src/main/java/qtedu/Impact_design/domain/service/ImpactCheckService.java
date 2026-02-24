package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.request.impactcheck.ImpactCheckRequest;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.domain.implementation.impactcheck.ImpactCheckAppender;
import qtedu.Impact_design.domain.implementation.impactcheck.ImpactCheckReader;
import qtedu.Impact_design.domain.model.ImpactCheckModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpactCheckService {

    private final ImpactCheckReader impactCheckReader;
    private final ImpactCheckAppender impactCheckAppender;

    public ImpactCheckResponse getImpactCheck(Long userId) {
        return impactCheckReader.read(userId);
    }

    public ImpactCheckResponse saveImpactCheck(Long userId, ImpactCheckRequest request) {
        return impactCheckAppender.append(userId, request);
    }

    public ImpactCheckResponse submitImpactCheck(Long userId, ImpactCheckRequest request) {
        return impactCheckAppender.submit(userId, request);
    }

    public List<ImpactCheckModel> getImpactChecksByUserIds(List<Long> userIds) {
        return impactCheckReader.readByUserIds(userIds);
    }
}
