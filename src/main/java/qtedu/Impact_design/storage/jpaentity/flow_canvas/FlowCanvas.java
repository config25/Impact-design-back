package qtedu.Impact_design.storage.jpaentity.flow_canvas;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flow_canvas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FlowCanvas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;

    @Column(name = "goal_title", length = 255)
    private String goalTitle;

    @Column(name = "goal_description", length = 255)
    private String goalDescription;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "submitted", nullable = false)
    @Builder.Default
    private Boolean submitted = false;

    public void update(String goalTitle, String goalDescription, Integer orderNo) {
        this.goalTitle = goalTitle;
        this.goalDescription = goalDescription;
        this.orderNo = orderNo;
    }

    public void submit() {
        this.submitted = true;
    }
}
