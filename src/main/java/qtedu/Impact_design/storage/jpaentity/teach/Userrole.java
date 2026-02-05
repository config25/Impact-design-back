package qtedu.Impact_design.storage.jpaentity.teach;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "userrole")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Userrole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Integer idx;

    @Column(name = "isDoing", nullable = false)
    private Integer isDoing;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "powerlevel", nullable = false)
    private Integer powerlevel;
}
