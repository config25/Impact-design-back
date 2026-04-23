package qtedu.Impact_design.domain.repository;

import qtedu.Impact_design.domain.model.ImpactCheckModel;

import java.util.List;
import java.util.Optional;

public interface ImpactCheckRepository {
    Optional<ImpactCheckModel> findByUserId(Long userId);
    ImpactCheckModel save(ImpactCheckModel model);
    List<ImpactCheckModel> findByUserIdIn(List<Long> userIds);
    boolean existsSubmittedByUserId(Long userId);
    void submitByUserId(Long userId);
    void rollbackByUserId(Long userId);
}
