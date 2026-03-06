package qtedu.Impact_design.storage.jpaentity.user;

import jakarta.persistence.*;
import lombok.*;
import qtedu.Impact_design.domain.model.en.UserRole;

@Entity
@Table(name = "userinfo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Userinfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_id", nullable = false, length = 128)
    private String loginId;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "user_name", nullable = false, length = 128)
    private String userName;

    @Column(name = "code", nullable = false, length = 128)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;

    @Column(name = "writer", length = 1)
    private String writer;

    public static Userinfo createStudent(String loginId, String encodedPassword, String code) {
        return Userinfo.builder()
                .loginId(loginId)
                .password(encodedPassword)
                .userName(loginId)
                .code(code)
                .userRole(UserRole.STUDENT)
                .build();
    }
}
