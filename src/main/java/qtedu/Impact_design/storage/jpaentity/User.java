package qtedu.Impact_design.storage.jpaentity;

import jakarta.persistence.*;
import lombok.*;
import qtedu.Impact_design.domain.model.en.UserRole;

@Entity
@Table(name = "userinfo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "id", nullable = false, length = 128)
    private String loginId;

    @Column(name = "pwd", nullable = false, length = 128)
    private String password;

    @Column(name = "userName", nullable = false, length = 128)
    private String userName;

    @Column(name = "code", nullable = false, length = 128)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "userRole")
    private UserRole userRole;

    public static User createStudent(String loginId, String encodedPassword, String code) {
        return User.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .userName(loginId)
                .code(code)
                .userRole(UserRole.STUDENT)
                .build();
    }
}
