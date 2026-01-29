package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.domain.implementation.AuthImplementation;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.UserId;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthImplementation authImplementation;

    public JwtToken login(String loginId, String password) {
        return authImplementation.login(loginId, password);
    }

    public JwtToken teacherLogin(String loginId, String password) {
        return authImplementation.teacherLogin(loginId, password);
    }

    public void signup(String loginId, String password, String code) {
        authImplementation.signup(loginId, password, code);
    }

    public void logout(UserId userId) {
        authImplementation.logout(userId);
    }
}
