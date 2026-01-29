package qtedu.Impact_design.storage.jpaentity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teamwork")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Teamwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teamwork_id")
    private Long teamworkId;

    @Column(name = "activity_teamwork", length = 255)
    private String activityTeamwork;

    @Column(name = "work_type", length = 255)
    private String workType;

    @Column(name = "canvas_id", nullable = false)
    private Long canvasId;
}
