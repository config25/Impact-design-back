package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.auth.TeamInfoResponse;
import qtedu.Impact_design.domain.implementation.auth.*;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.model.en.UserRole;
import qtedu.Impact_design.domain.model.user.UserinfoModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthReader authReader;
    private final AuthValidator authValidator;
    private final AuthGenerator authGenerator;
    private final AuthAppender authAppender;
    private final AuthRemover authRemover;

    @Transactional
    public JwtToken login(String loginId, String password) {
        UserinfoModel userinfo = authReader.findUserByLoginId(loginId);
        authValidator.validatePassword(password, userinfo.getPassword());
        authValidator.validateStudentRole(userinfo);
        JwtToken jwtToken = authGenerator.generateToken(userinfo);
        authAppender.saveLoggedIn(userinfo.getUserId(), jwtToken);
        return jwtToken;
    }

    @Transactional
    public Pair<JwtToken, UserRole> teacherLogin(String loginId, String password) {
        UserinfoModel userinfo = authReader.findUserByLoginId(loginId);
        authValidator.validatePassword(password, userinfo.getPassword());
        authValidator.validateTeacherRole(userinfo);
        JwtToken jwtToken = authGenerator.generateToken(userinfo);
        authAppender.saveLoggedIn(userinfo.getUserId(), jwtToken);
        return Pair.of(jwtToken, userinfo.getUserRole());
    }

    public List<TeamInfoResponse> checkCode(String code) {
        return authReader.checkCode(code);
    }

    public void checkLoginIdDuplicate(String loginId) {
        authValidator.checkLoginIdDuplicate(loginId);
    }

    public void signup(String loginId, String password, String code, Integer teamId) {
        authAppender.signup(loginId, password, code, teamId);
    }

    public void logout(UserId userId) {
        authRemover.logout(userId);
    }
}
