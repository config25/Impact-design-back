package qtedu.Impact_design.storage.jpaentity.win_canvas;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_activity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TaskActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "process_step", length = 255)
    private String processStep;

    @Column(name = "activity_content", length = 255)
    private String activityContent;

    @Column(name = "duration", length = 255)
    private String duration;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "canvas_id", nullable = false)
    private Long canvasId;

    public void update(String processStep, String activityContent, String duration, Integer orderNo) {
        this.processStep = processStep;
        this.activityContent = activityContent;
        this.duration = duration;
        this.orderNo = orderNo;
    }
}
