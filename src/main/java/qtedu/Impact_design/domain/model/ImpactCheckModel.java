package qtedu.Impact_design.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImpactCheckModel {
    private final Long answerId;
    private final Integer q1Score;
    private final Integer q2Score;
    private final Integer q3Score;
    private final Integer q4Score;
    private final Integer q5Score;
    private final Integer q6Score;
    private final Integer q7Score;
    private final Integer q8Score;
    private final Integer q9Score;
    private final Integer q10Score;
    private final Integer q11Score;
    private final Integer q12Score;
    private final String q13Text;
    private final String q14Text;
    private final String q15Text;
    private final String q16Text;
    private final Long userId;
    private final Boolean submitted;
}
