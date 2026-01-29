package qtedu.Impact_design.storage.jpaentity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tactical")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Tactical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metric_id")
    private Long metricId;

    @Column(name = "tactical_metric", length = 255)
    private String tacticalMetric;

    @Column(name = "tactical_goal", length = 255)
    private String tacticalGoal;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "goal_id", nullable = false)
    private Long goalId;
}
