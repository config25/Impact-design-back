package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.response.auth.TeamInfoResponse;
import qtedu.Impact_design.domain.implementation.auth.AccountFacade;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.model.en.UserRole;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountFacade accountFacade;

    public JwtToken login(String loginId, String password) {
        return accountFacade.login(loginId, password);
    }

    public Pair<JwtToken, UserRole> teacherLogin(String loginId, String password) {
        return accountFacade.teacherLogin(loginId, password);
    }

    public List<TeamInfoResponse> checkCode(String code) {
        return accountFacade.checkCode(code);
    }

    public void checkLoginIdDuplicate(String loginId) {
        accountFacade.checkLoginIdDuplicate(loginId);
    }

    public void signup(String loginId, String password, String code, Integer teamId) {
        accountFacade.signup(loginId, password, code, teamId);
    }

    public void logout(UserId userId) {
        accountFacade.logout(userId);
    }
}
