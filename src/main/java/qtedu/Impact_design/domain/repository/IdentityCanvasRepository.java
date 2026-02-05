package qtedu.Impact_design.domain.repository;

import qtedu.Impact_design.domain.model.IdentityCanvasModel;

import java.util.List;
import java.util.Optional;

public interface IdentityCanvasRepository {
    Optional<IdentityCanvasModel> findByUserId(Long userId);
    IdentityCanvasModel save(IdentityCanvasModel identityCanvasModel);
    List<IdentityCanvasModel> findByUserIdIn(List<Long> userIds);
}
