package qtedu.Impact_design.domain.implementation.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.util.security.JwtTokenUtil;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.model.user.UserinfoModel;

@Component
@RequiredArgsConstructor
public class AuthGenerator {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtToken generateToken(UserinfoModel userinfo) {
        UserId userId = UserId.of(userinfo.getUserId());
        return jwtTokenUtil.createJwtToken(userId);
    }
}
