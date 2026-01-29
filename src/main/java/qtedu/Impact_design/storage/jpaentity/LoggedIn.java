package qtedu.Impact_design.storage.jpaentity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "logged_in")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LoggedIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logged_in_id")
    private Long loggedInId;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "userNo", nullable = false)
    private Long userNo;

    public static LoggedIn create(Long userNo, String refreshToken, LocalDateTime expiredAt) {
        return LoggedIn.builder()
                .userNo(userNo)
                .refreshToken(refreshToken)
                .expiredAt(expiredAt)
                .build();
    }

    public void updateRefreshToken(String refreshToken, LocalDateTime expiredAt) {
        this.refreshToken = refreshToken;
        this.expiredAt = expiredAt;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
        this.expiredAt = null;
    }
}
