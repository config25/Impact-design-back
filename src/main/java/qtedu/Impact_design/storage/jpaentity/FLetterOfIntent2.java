package qtedu.Impact_design.storage.jpaentity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "f_letter_of_intent2")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FLetterOfIntent2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "intent_index")
    private Integer intentIndex;

    @Column(name = "stdntNo", nullable = false)
    private Integer stdntNo;

    @Column(name = "courseCd", nullable = false, length = 32)
    private String courseCd;

    @Column(name = "investment_target", length = 32)
    private String investmentTarget;

    @Column(name = "investment_price", length = 100)
    private String investmentPrice;

    @Column(name = "score1")
    private Integer score1;

    @Column(name = "score2")
    private Integer score2;

    @Column(name = "score3")
    private Integer score3;

    @Column(name = "score4")
    private Integer score4;

    @Column(name = "score5")
    private Integer score5;

    @Column(name = "score6")
    private Integer score6;

    @Column(name = "score7")
    private Integer score7;

    @Column(name = "score8")
    private Integer score8;

    @Column(name = "score9")
    private Integer score9;

    @Column(name = "score10")
    private Integer score10;

    @Column(name = "opinion", columnDefinition = "TEXT")
    private String opinion;

    @Column(name = "del_Yn", length = 8)
    private String delYn;

    @Column(name = "reg_id", length = 32)
    private String regId;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    @Column(name = "mdfcn_id", length = 32)
    private String mdfcnId;

    @Column(name = "mdfcn_dt")
    private LocalDateTime mdfcnDt;

    @Column(name = "commit_yn", length = 255)
    private String commitYn;

    @Column(name = "team_id")
    private Integer teamId;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "categoryCd", nullable = false, length = 32)
    private String categoryCd;

    @Column(name = "canvas_id")
    private Long canvasId;
}
