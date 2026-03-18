package qtedu.Impact_design.storage.jpaentity.teach;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teamuser", indexes = {
        @Index(name = "idx_teamuser_user_id", columnList = "user_id"),
        @Index(name = "idx_teamuser_team_id", columnList = "team_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_user_id")
    private Integer teamUserId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "team_id")
    private Integer teamId;

    @Column(name = "userlevel", nullable = false)
    private Integer userlevel;

    @Column(name = "is_doing", nullable = false)
    private Integer isDoing;

    public static TeamUser createStudent(Long userId, Integer teamId) {
        return TeamUser.builder()
                .userId(userId)
                .teamId(teamId)
                .userlevel(0)
                .isDoing(1)
                .build();
    }
}
