package qtedu.Impact_design.domain.repository;

import qtedu.Impact_design.storage.jpaentity.LoggedIn;

import java.util.Optional;

public interface LoggedInRepository {

    Optional<LoggedIn> findByUserNo(Long userNo);

    LoggedIn save(LoggedIn loggedIn);

    void deleteByUserNo(Long userNo);
}
