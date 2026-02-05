package qtedu.Impact_design.storage.jpaentity.teach;

import java.io.Serializable;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameTeamId implements Serializable {
    private Integer gameId;
    private Integer teamId;
}
