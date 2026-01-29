package qtedu.Impact_design.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class RefreshToken {

    private final String token;
    private final LocalDateTime expiredAt;

    public static RefreshToken of(String token, LocalDateTime expiredAt) {
        return new RefreshToken(token, expiredAt);
    }
}