package qtedu.Impact_design.storage.jpaentity.teach;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teamuser")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Integer idx;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "team_id")
    private Integer teamId;

    @Column(name = "userlevel", nullable = false)
    private Integer userlevel;

    @Column(name = "isDoing", nullable = false)
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
