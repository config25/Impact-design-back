package qtedu.Impact_design.api.dto.response.funding;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FundingMyResultResponse {
    private ResultSection build;
    private ResultSection quick;

    public static FundingMyResultResponse from(ResultSection build, ResultSection quick) {
        return FundingMyResultResponse.builder()
                .build(build)
                .quick(quick)
                .build();
    }

    @Getter
    @Builder
    public static class ResultSection {
        private ScoreResult scores;
        private List<String> opinions;

        public static ResultSection from(ScoreResult scores, List<String> opinions) {
            return ResultSection.builder()
                    .scores(scores)
                    .opinions(opinions)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ScoreResult {
        private Integer problemScore;      // score1 + score2
        private Integer solutionScore;     // score3 + score4 + score5
        private Integer scaleUpScore;      // score6 + score7
        private Integer effectScore;       // score8 + score9
        private Integer totalScore;
        private Integer evaluatorCount;    // 평가 참가자 수

        public static ScoreResult from(int problem, int solution, int scaleUp, int effect, int evaluatorCount) {
            return ScoreResult.builder()
                    .problemScore(problem)
                    .solutionScore(solution)
                    .scaleUpScore(scaleUp)
                    .effectScore(effect)
                    .totalScore(problem + solution + scaleUp + effect)
                    .evaluatorCount(evaluatorCount)
                    .build();
        }

        public static ScoreResult empty() {
            return ScoreResult.builder()
                    .problemScore(0)
                    .solutionScore(0)
                    .scaleUpScore(0)
                    .effectScore(0)
                    .totalScore(0)
                    .evaluatorCount(0)
                    .build();
        }
    }
}
