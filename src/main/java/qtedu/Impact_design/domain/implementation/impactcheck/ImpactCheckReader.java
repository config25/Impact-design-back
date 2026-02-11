package qtedu.Impact_design.domain.implementation.impactcheck;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.domain.model.ImpactCheckModel;
import qtedu.Impact_design.domain.repository.ImpactCheckRepository;
import qtedu.Impact_design.storage.jparepository.teach.TbGameJpaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImpactCheckReader {

    private final ImpactCheckRepository impactCheckRepository;
    private final TbGameJpaRepository tbGameJpaRepository;

    public ImpactCheckResponse read(Long userId) {
        String gameName = tbGameJpaRepository.findGameNameByUserId(userId);
        return impactCheckRepository.findByUserId(userId)
                .map(model -> ImpactCheckResponse.from(model, gameName))
                .orElse(null);
    }

    public List<ImpactCheckModel> readByUserIds(List<Long> userIds) {
        return impactCheckRepository.findByUserIdIn(userIds);
    }
}
