package qtedu.Impact_design.storage.jpaentity.win_canvas;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_input")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TaskInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "input_id")
    private Long inputId;

    @Column(name = "resource_name", length = 255)
    private String resourceName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "canvas_id", nullable = false)
    private Long canvasId;

    public void update(String resourceName, Integer quantity, Integer orderNo) {
        this.resourceName = resourceName;
        this.quantity = quantity;
        this.orderNo = orderNo;
    }
}
