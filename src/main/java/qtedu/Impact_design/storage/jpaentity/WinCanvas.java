package qtedu.Impact_design.storage.jpaentity;

import jakarta.persistence.*;
import lombok.*;
import qtedu.Impact_design.domain.model.en.CanvasType;

@Entity
@Table(name = "win_canvas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WinCanvas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "canvas_id")
    private Long canvasId;

    @Enumerated(EnumType.STRING)
    @Column(name = "canvas_type")
    private CanvasType canvasType;

    @Column(name = "strategic_goal")
    private Integer strategicGoal;

    @Column(name = "task_name", length = 255)
    private String taskName;

    @Column(name = "task_description", length = 255)
    private String taskDescription;

    @Column(name = "crisis_signal", length = 255)
    private String crisisSignal;

    @Column(name = "pain_touch_point", length = 255)
    private String painTouchPoint;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
