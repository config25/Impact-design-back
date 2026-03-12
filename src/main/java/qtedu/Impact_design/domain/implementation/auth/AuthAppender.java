package qtedu.Impact_design.domain.implementation.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.auth.LoggedInModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.LoggedInRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthAppender {

    private final UserinfoRepository userinfoRepository;
    private final TeamUserRepository teamUserRepository;
    private final LoggedInRepository loggedInRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(String loginId, String password, String code, Integer teamId) {
        String encodedPassword = passwordEncoder.encode(password);
        UserinfoModel userinfo = UserinfoModel.createStudent(loginId, encodedPassword, code);
        UserinfoModel savedUserinfo = userinfoRepository.save(userinfo);

        TeamUserModel teamUser = TeamUserModel.createStudent(savedUserinfo.getUserId(), teamId);
        teamUserRepository.save(teamUser);

        log.info("회원가입 완료 - userId: {}, teamId: {}", savedUserinfo.getUserId(), teamId);
    }

    @Transactional
    public void saveLoggedIn(Long userId, JwtToken jwtToken) {
        loggedInRepository.findByUserId(userId)
                .ifPresentOrElse(
                        loggedIn -> {
                            LoggedInModel updated = LoggedInModel.builder()
                                    .loggedInId(loggedIn.getLoggedInId())
                                    .userId(loggedIn.getUserId())
                                    .refreshToken(jwtToken.getRefreshToken().getToken())
                                    .expiredAt(jwtToken.getRefreshToken().getExpiredAt())
                                    .build();
                            loggedInRepository.save(updated);
                        },
                        () -> {
                            LoggedInModel newLoggedIn = LoggedInModel.create(
                                    userId,
                                    jwtToken.getRefreshToken().getToken(),
                                    jwtToken.getRefreshToken().getExpiredAt()
                            );
                            loggedInRepository.save(newLoggedIn);
                        }
                );
    }
}
