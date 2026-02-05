package qtedu.Impact_design.storage.jpaentity.teach;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gameteam")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@IdClass(GameTeamId.class)
public class GameTeam {

    @Id
    @Column(name = "game_id")
    private Integer gameId;

    @Id
    @Column(name = "team_id")
    private Integer teamId;
}
