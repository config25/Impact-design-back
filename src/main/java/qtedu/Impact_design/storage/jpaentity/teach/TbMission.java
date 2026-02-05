package qtedu.Impact_design.storage.jpaentity.teach;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbmission")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TbMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Integer missionId;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    @Column(name = "subject", columnDefinition = "TEXT")
    private String subject;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "startdate")
    private LocalDateTime startdate;

    @Column(name = "enddate")
    private LocalDateTime enddate;

    @Column(name = "dd_year")
    private Integer ddYear;

    @Column(name = "dd_term")
    private Integer ddTerm;

    @Column(name = "mlevel")
    private Integer mlevel;

    @Column(name = "game_id")
    private Long gameId;

    @Column(name = "toinform", length = 50)
    private String toinform;
}
