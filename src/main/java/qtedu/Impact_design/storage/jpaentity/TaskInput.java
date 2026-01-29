package qtedu.Impact_design.storage.jpaentity;

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

    @Column(name = "resource_name", nullable = false, length = 255)
    private String resourceName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "order_no")
    private Integer orderNo;

    @Column(name = "canvas_id", nullable = false)
    private Long canvasId;
}
