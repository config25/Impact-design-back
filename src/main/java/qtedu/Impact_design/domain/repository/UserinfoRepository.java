package qtedu.Impact_design.domain.repository;

import qtedu.Impact_design.storage.jpaentity.User;

import java.util.Optional;

public interface UserinfoRepository {

    Optional<User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    User save(User user);
}
