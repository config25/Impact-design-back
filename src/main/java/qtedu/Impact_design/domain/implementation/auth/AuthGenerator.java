package qtedu.Impact_design.domain.implementation.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.util.security.JwtTokenUtil;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.model.auth.LoggedInModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.LoggedInRepository;

@Component
@RequiredArgsConstructor
public class AuthGenerator {

    private final JwtTokenUtil jwtTokenUtil;
    private final LoggedInRepository loggedInRepository;

    @Transactional
    public JwtToken generateAndSaveToken(UserinfoModel userinfo) {
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
