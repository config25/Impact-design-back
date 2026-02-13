package qtedu.Impact_design.storage.jpaentity.teach;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbgame")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TbGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "code", length = 32)
    private String code;

    @Column(name = "num")
    private Integer num;

    @Column(name = "num_team")
    private Integer numTeam;

    @Column(name = "num_member", length = 32)
    private String numMember;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "e_status")
    private Integer eStatus;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "total_dd", nullable = false)
    private Integer totalDd;

    @Column(name = "lang")
    private Integer lang;

    @Column(name = "world_type")
    private Integer worldType;

    @Column(name = "step", length = 100)
    private String step;

    @Column(name = "class_type", length = 32)
    private String classType;

    @Column(name = "is_doing", nullable = false)
    private Integer isDoing;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @Column(name = "popup_id")
    private Integer popupId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "target", length = 100)
    private String target;

    @Column(name = "project_date", length = 20)
    private String projectDate;
}
