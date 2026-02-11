package qtedu.Impact_design.storage.jpaentity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Impact_check")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ImpactCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @Column(name = "q1_score")
    private Integer q1Score;

    @Column(name = "q2_score")
    private Integer q2Score;

    @Column(name = "q3_score")
    private Integer q3Score;

    @Column(name = "q4_score")
    private Integer q4Score;

    @Column(name = "q5_score")
    private Integer q5Score;

    @Column(name = "q6_score")
    private Integer q6Score;

    @Column(name = "q7_score")
    private Integer q7Score;

    @Column(name = "q8_score")
    private Integer q8Score;

    @Column(name = "q9_score")
    private Integer q9Score;

    @Column(name = "q10_score")
    private Integer q10Score;

    @Column(name = "q11_score")
    private Integer q11Score;

    @Column(name = "q12_score")
    private Integer q12Score;

    @Column(name = "q13_text", columnDefinition = "TEXT")
    private String q13Text;

    @Column(name = "q14_text", columnDefinition = "TEXT")
    private String q14Text;

    @Column(name = "q15_text", columnDefinition = "TEXT")
    private String q15Text;

    @Column(name = "q16_text", columnDefinition = "TEXT")
    private String q16Text;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "submitted", nullable = false)
    @Builder.Default
    private Boolean submitted = false;

    public void submit() {
        this.submitted = true;
    }

    public void update(Integer q1Score, Integer q2Score, Integer q3Score, Integer q4Score,
                       Integer q5Score, Integer q6Score, Integer q7Score, Integer q8Score,
                       Integer q9Score, Integer q10Score, Integer q11Score, Integer q12Score,
                       String q13Text, String q14Text, String q15Text, String q16Text) {
        this.q1Score = q1Score;
        this.q2Score = q2Score;
        this.q3Score = q3Score;
        this.q4Score = q4Score;
        this.q5Score = q5Score;
        this.q6Score = q6Score;
        this.q7Score = q7Score;
        this.q8Score = q8Score;
        this.q9Score = q9Score;
        this.q10Score = q10Score;
        this.q11Score = q11Score;
        this.q12Score = q12Score;
        this.q13Text = q13Text;
        this.q14Text = q14Text;
        this.q15Text = q15Text;
        this.q16Text = q16Text;
    }
}
