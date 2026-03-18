package qtedu.Impact_design.storage.jpaentity.win_canvas;

import jakarta.persistence.*;
import lombok.*;
import qtedu.Impact_design.domain.model.en.CanvasType;

@Entity
@Table(name = "win_canvas", indexes = {
        @Index(name = "idx_wincanvas_user_canvas", columnList = "user_id, canvas_type")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WinCanvas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "canvas_id", nullable = false)
    private Long canvasId;

    @Enumerated(EnumType.STRING)
    @Column(name = "canvas_type")
    private CanvasType canvasType;

    @Column(name = "strategic_goal", length = 128)
    private String strategicGoal;

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

    @Column(name = "submitted", nullable = false)
    @Builder.Default
    private Boolean submitted = false;

    public void update(String strategicGoal, String taskName, String taskDescription,
                       String crisisSignal, String painTouchPoint) {
        this.strategicGoal = strategicGoal;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.crisisSignal = crisisSignal;
        this.painTouchPoint = painTouchPoint;
    }

    public void submit() {
        this.submitted = true;
    }
}
