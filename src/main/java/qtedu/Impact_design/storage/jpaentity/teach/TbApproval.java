package qtedu.Impact_design.storage.jpaentity.teach;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbapproval")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TbApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Integer approvalId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "dd_year")
    private Integer ddYear;

    @Column(name = "dd_term")
    private Integer ddTerm;

    @Column(name = "level")
    private Integer level;

    @Column(name = "role")
    private Integer role;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "team_id")
    private Integer teamId;
}
