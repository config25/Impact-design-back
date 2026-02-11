package qtedu.Impact_design.domain.implementation.auth;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.util.security.JwtTokenUtil;
import qtedu.Impact_design.common.error.AuthorizationException;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.model.auth.LoggedInModel;
import qtedu.Impact_design.domain.model.en.UserRole;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.api.dto.response.auth.TeamInfoResponse;
import qtedu.Impact_design.domain.repository.auth.LoggedInRepository;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountFacade {

    private final UserinfoRepository userinfoRepository;
    private final LoggedInRepository loggedInRepository;
    private final TbTeamRepository tbTeamRepository;
    private final TeamUserRepository teamUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    public JwtToken login(String loginId, String password) {
        UserinfoModel userinfo = userinfoRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, userinfo.getPassword())) {
            throw new AuthorizationException(ErrorCode.WRONG_PASSWORD);
        }

        // STUDENT만 허용
        if (userinfo.getUserRole() != UserRole.STUDENT) {
            throw new AuthorizationException(ErrorCode.ACCESS_DENIED);
        }

        return generateAndSaveToken(userinfo);
    }

    @Transactional
    public Pair<JwtToken, UserRole> teacherLogin(String loginId, String password) {
        UserinfoModel userinfo = userinfoRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, userinfo.getPassword())) {
            throw new AuthorizationException(ErrorCode.WRONG_PASSWORD);
        }

        // ADMIN, TEACHER만 허용
        if (userinfo.getUserRole() != UserRole.ADMIN && userinfo.getUserRole() != UserRole.TEACHER) {
            throw new AuthorizationException(ErrorCode.ACCESS_DENIED);
        }

        return Pair.of(generateAndSaveToken(userinfo), userinfo.getUserRole());
    }

    public List<TeamInfoResponse> checkCode(String code) {
        List<TbTeamModel> teams = tbTeamRepository.findByCode(code);
        if (teams.isEmpty()) {
            throw new NotFoundException(ErrorCode.CODE_NOT_FOUND);
        }
        return teams.stream()
                .map(TeamInfoResponse::from)
                .collect(Collectors.toList());
    }

    public void checkLoginIdDuplicate(String loginId) {
        if (userinfoRepository.existsByLoginId(loginId)) {
            throw new ConflictException(ErrorCode.USER_ALREADY_CREATED);
        }
    }

    @Transactional
    public void signup(String loginId, String password, String code, Integer teamId) {
        // 1. userinfo 저장
        String encodedPassword = passwordEncoder.encode(password);
        UserinfoModel userinfo = UserinfoModel.createStudent(loginId, encodedPassword, code);
        UserinfoModel savedUserinfo = userinfoRepository.save(userinfo);

        // 2. teamuser 저장
        TeamUserModel teamUser = TeamUserModel.createStudent(savedUserinfo.getUserId(), teamId);
        teamUserRepository.save(teamUser);
    }

    @Transactional
    public void logout(UserId userId) {
        loggedInRepository.deleteByUserNo(userId.getId());
    }

    private JwtToken generateAndSaveToken(UserinfoModel userinfo) {
        UserId userId = UserId.of(userinfo.getUserId());
        JwtToken jwtToken = jwtTokenUtil.createJwtToken(userId);

        loggedInRepository.findByUserNo(userId.getId())
                .ifPresentOrElse(
                        loggedIn -> {
                            LoggedInModel updated = LoggedInModel.builder()
                                    .loggedInId(loggedIn.getLoggedInId())
                                    .userNo(loggedIn.getUserNo())
                                    .refreshToken(jwtToken.getRefreshToken().getToken())
                                    .expiredAt(jwtToken.getRefreshToken().getExpiredAt())
                                    .build();
                            loggedInRepository.save(updated);
                        },
                        () -> {
                            LoggedInModel newLoggedIn = LoggedInModel.create(
                                    userId.getId(),
                                    jwtToken.getRefreshToken().getToken(),
                                    jwtToken.getRefreshToken().getExpiredAt()
                            );
                            loggedInRepository.save(newLoggedIn);
                        }
                );

        return jwtToken;
    }
}
