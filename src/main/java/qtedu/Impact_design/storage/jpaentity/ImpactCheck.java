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

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
