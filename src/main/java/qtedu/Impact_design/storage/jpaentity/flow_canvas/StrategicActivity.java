package qtedu.Impact_design.storage.jpaentity.flow_canvas;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "strategic_activity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StrategicActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "activity_metric", length = 255)
    private String activityMetric;

    @Column(name = "inter_criteria", length = 255)
    private String interCriteria;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "goal_id", nullable = false)
    private Long goalId;

    public void update(String activityMetric, String interCriteria, Integer orderNo) {
        this.activityMetric = activityMetric;
        this.interCriteria = interCriteria;
        this.orderNo = orderNo;
    }
}
