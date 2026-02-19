package qtedu.Impact_design.storage.jpaentity.teach;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bc_missiongame")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BcMissionGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bc_mission_game_id")
    private Integer bcMissionGameId;

    @Column(name = "ceo_mission", length = 50)
    private String ceoMission;

    @Column(name = "cmo_mission", length = 50)
    private String cmoMission;

    @Column(name = "coo_mission", length = 50)
    private String cooMission;

    @Column(name = "cho_mission", length = 50)
    private String choMission;

    @Column(name = "cfo_mission", length = 50)
    private String cfoMission;

    @Column(name = "d_year")
    private Integer dYear;

    @Column(name = "d_term")
    private Integer dTerm;

    @Column(name = "game_id")
    private Integer gameId;
}
