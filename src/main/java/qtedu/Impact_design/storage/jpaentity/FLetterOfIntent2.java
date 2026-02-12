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

    @Column(name = "submitted", nullable = false)
    @Builder.Default
    private Boolean submitted = false;

    @Column(name = "team_id")
    private Integer teamId;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "categoryCd", nullable = false, length = 32)
    private String categoryCd;

    @Column(name = "canvas_id")
    private Long canvasId;

    public void update(String investmentPrice, Integer score1, Integer score2, Integer score3,
                       Integer score4, Integer score5, Integer score6, Integer score7,
                       Integer score8, Integer score9, String opinion, Boolean submitted) {
        this.investmentPrice = investmentPrice;
        this.score1 = score1;
        this.score2 = score2;
        this.score3 = score3;
        this.score4 = score4;
        this.score5 = score5;
        this.score6 = score6;
        this.score7 = score7;
        this.score8 = score8;
        this.score9 = score9;
        this.opinion = opinion;
        this.submitted = Boolean.TRUE.equals(submitted);
        this.mdfcnDt = java.time.LocalDateTime.now();
    }

    public void submit() {
        this.submitted = true;
    }
}
