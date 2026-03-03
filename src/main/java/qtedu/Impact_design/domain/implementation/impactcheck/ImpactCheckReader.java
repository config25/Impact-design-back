package qtedu.Impact_design.domain.implementation.impactcheck;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.domain.model.ImpactCheckModel;
import qtedu.Impact_design.domain.repository.ImpactCheckRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImpactCheckReader {

    private final ImpactCheckRepository impactCheckRepository;

    public ImpactCheckResponse read(Long userId) {
        return impactCheckRepository.findByUserId(userId)
                .map(ImpactCheckResponse::from)
                .orElse(ImpactCheckResponse.empty());
    }

    public List<ImpactCheckModel> readByUserIds(List<Long> userIds) {
        return impactCheckRepository.findByUserIdIn(userIds);
    }
}
