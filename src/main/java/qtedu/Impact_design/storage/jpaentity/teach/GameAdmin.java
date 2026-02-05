package qtedu.Impact_design.storage.jpaentity.teach;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gameadmin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@IdClass(GameAdminId.class)
public class GameAdmin {

    @Id
    @Column(name = "game_id")
    private Integer gameId;

    @Id
    @Column(name = "user_id")
    private Long userId;
}
