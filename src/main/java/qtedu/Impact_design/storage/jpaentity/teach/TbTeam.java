package qtedu.Impact_design.storage.jpaentity.teach;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbteam")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TbTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Integer teamId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "status")
    private Integer status;

    @Column(name = "ai_play")
    private Integer aiPlay;

    @Column(name = "code", length = 32)
    private String code;

    @Column(name = "is_doing")
    private Integer isDoing;

    @Column(name = "team_category")
    private Integer teamCategory;

    @Column(name = "num_user")
    private Integer numUser;
}
