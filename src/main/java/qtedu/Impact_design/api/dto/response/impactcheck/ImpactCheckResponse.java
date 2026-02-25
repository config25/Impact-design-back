package qtedu.Impact_design.api.dto.response.impactcheck;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.domain.model.ImpactCheckModel;

@Getter
@Builder
public class ImpactCheckResponse {
    private Long answerId;
    private Integer q1Score;
    private Integer q2Score;
    private Integer q3Score;
    private Integer q4Score;
    private Integer q5Score;
    private Integer q6Score;
    private Integer q7Score;
    private Integer q8Score;
    private Integer q9Score;
    private Integer q10Score;
    private Integer q11Score;
    private Integer q12Score;
    private String q13Text;
    private String q14Text;
    private String q15Text;
    private String q16Text;
    private Long userId;
    private Boolean submitted;
    private String gameName;

    public static ImpactCheckResponse from(ImpactCheckModel model) {
        return ImpactCheckResponse.builder()
                .answerId(model.getAnswerId())
                .q1Score(model.getQ1Score())
                .q2Score(model.getQ2Score())
                .q3Score(model.getQ3Score())
                .q4Score(model.getQ4Score())
                .q5Score(model.getQ5Score())
                .q6Score(model.getQ6Score())
                .q7Score(model.getQ7Score())
                .q8Score(model.getQ8Score())
                .q9Score(model.getQ9Score())
                .q10Score(model.getQ10Score())
                .q11Score(model.getQ11Score())
                .q12Score(model.getQ12Score())
                .q13Text(model.getQ13Text())
                .q14Text(model.getQ14Text())
                .q15Text(model.getQ15Text())
                .q16Text(model.getQ16Text())
                .userId(model.getUserId())
                .submitted(model.getSubmitted())
                .build();
    }

    public static ImpactCheckResponse from(ImpactCheckModel model, String gameName) {
        return ImpactCheckResponse.builder()
                .answerId(model.getAnswerId())
                .q1Score(model.getQ1Score())
                .q2Score(model.getQ2Score())
                .q3Score(model.getQ3Score())
                .q4Score(model.getQ4Score())
                .q5Score(model.getQ5Score())
                .q6Score(model.getQ6Score())
                .q7Score(model.getQ7Score())
                .q8Score(model.getQ8Score())
                .q9Score(model.getQ9Score())
                .q10Score(model.getQ10Score())
                .q11Score(model.getQ11Score())
                .q12Score(model.getQ12Score())
                .q13Text(model.getQ13Text())
                .q14Text(model.getQ14Text())
                .q15Text(model.getQ15Text())
                .q16Text(model.getQ16Text())
                .userId(model.getUserId())
                .submitted(model.getSubmitted())
                .gameName(gameName)
                .build();
    }

    public static ImpactCheckResponse empty(String gameName) {
        return ImpactCheckResponse.builder()
                .submitted(false)
                .gameName(gameName)
                .build();
    }
}
