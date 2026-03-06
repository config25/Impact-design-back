package qtedu.Impact_design.domain.model.auth;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LoggedInModel {
    private final Long loggedInId;
    private final String refreshToken;
    private final LocalDateTime expiredAt;
    private final Long userId;

    public static LoggedInModel create(Long userId, String refreshToken, LocalDateTime expiredAt) {
        return LoggedInModel.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiredAt(expiredAt)
                .build();
    }
}
