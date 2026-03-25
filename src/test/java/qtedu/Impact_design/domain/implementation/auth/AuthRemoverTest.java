package qtedu.Impact_design.domain.implementation.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.repository.auth.LoggedInRepository;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthRemoverTest {

    @InjectMocks
    private AuthRemover authRemover;

    @Mock
    private LoggedInRepository loggedInRepository;

    @Test
    @DisplayName("로그아웃 시 userId로 로그인 기록을 삭제한다")
    void logout() {
        UserId userId = UserId.of(1L);

        authRemover.logout(userId);

        then(loggedInRepository).should().deleteByUserId(1L);
    }
}
