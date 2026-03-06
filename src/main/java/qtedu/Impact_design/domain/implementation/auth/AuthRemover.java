package qtedu.Impact_design.domain.implementation.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.repository.auth.LoggedInRepository;

@Component
@RequiredArgsConstructor
public class AuthRemover {

    private final LoggedInRepository loggedInRepository;

    @Transactional
    public void logout(UserId userId) {
        loggedInRepository.deleteByUserId(userId.getId());
    }
}
