package qtedu.Impact_design.domain.repository.auth;

import qtedu.Impact_design.domain.model.auth.LoggedInModel;

import java.util.Optional;

public interface LoggedInRepository {

    Optional<LoggedInModel> findByUserId(Long userId);

    LoggedInModel save(LoggedInModel loggedInModel);

    void deleteByUserId(Long userId);
}
