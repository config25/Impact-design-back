package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.domain.implementation.auth.AccountFacade;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.UserId;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountFacade accountFacade;

    public JwtToken login(String loginId, String password) {
        return accountFacade.login(loginId, password);
    }

    public JwtToken teacherLogin(String loginId, String password) {
        return accountFacade.teacherLogin(loginId, password);
    }

    public void signup(String loginId, String password, String code) {
        accountFacade.signup(loginId, password, code);
    }

    public void logout(UserId userId) {
        accountFacade.logout(userId);
    }
}
