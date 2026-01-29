package qtedu.Impact_design.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtToken {

    private final String accessToken;
    private final RefreshToken refreshToken;


    public static JwtToken of(String accessToken, RefreshToken refreshToken) {
        return new JwtToken(accessToken, refreshToken);
    }


}