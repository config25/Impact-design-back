package qtedu.Impact_design.storage.jpaentity;

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

    @Column(name = "process_step", nullable = false, length = 255)
    private String processStep;

    @Column(name = "activity_content", length = 255)
    private String activityContent;

    @Column(name = "duration", length = 255)
    private String duration;

    @Column(name = "order_no")
    private Integer orderNo;

    @Column(name = "canvas_id", nullable = false)
    private Long canvasId;
}
