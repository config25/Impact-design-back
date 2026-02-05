package qtedu.Impact_design.storage.jpaentity.win_canvas;

import jakarta.persistence.*;
import lombok.*;
import qtedu.Impact_design.domain.model.en.OutcomeType;

@Entity
@Table(name = "task_outcome")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TaskOutcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outcomeNo")
    private Long outcomeNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome_type")
    private OutcomeType outcomeType;

    @Column(name = "outcome_content", length = 255)
    private String outcomeContent;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "canvas_id", nullable = false)
    private Long canvasId;

    public void update(OutcomeType outcomeType, String outcomeContent, Integer orderNo) {
        this.outcomeType = outcomeType;
        this.outcomeContent = outcomeContent;
        this.orderNo = orderNo;
    }
}
