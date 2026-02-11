package qtedu.Impact_design.domain.repository.user;

import qtedu.Impact_design.domain.model.user.UserinfoModel;

import java.util.List;
import java.util.Optional;

public interface UserinfoRepository {

    Optional<UserinfoModel> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    UserinfoModel save(UserinfoModel userinfoModel);

    Optional<UserinfoModel> findWriterByUserIds(List<Long> userIds);

    Optional<UserinfoModel> findByUserId(Long userId);
}
