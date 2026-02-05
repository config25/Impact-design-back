package qtedu.Impact_design.storage.jpaentity.teach;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbmissiondata")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TbMissionData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Integer idx;

    @Column(name = "status_ceo")
    private Integer statusCeo;

    @Column(name = "status_mar")
    private Integer statusMar;

    @Column(name = "status_pro")
    private Integer statusPro;

    @Column(name = "status_fin")
    private Integer statusFin;

    @Column(name = "status_cho")
    private Integer statusCho;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "mission_id", nullable = false)
    private Long missionId;
}
