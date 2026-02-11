package qtedu.Impact_design.api.dto.response.funding;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FundingScoresResponse {
    private List<TeamScoreItem> teamScores;

    public static FundingScoresResponse from(List<TeamScoreItem> teamScores) {
        return FundingScoresResponse.builder()
                .teamScores(teamScores)
                .build();
    }

    @Getter
    @Builder
    public static class TeamScoreItem {
        private Integer teamId;
        private String teamName;
        private Integer problemScore;
        private Integer solutionScore;
        private Integer scaleUpScore;
        private Integer effectScore;
        private Integer totalScore;

        public static TeamScoreItem from(Integer teamId, String teamName,
                                         int score1, int score2, int score3, int score4, int score5,
                                         int score6, int score7, int score8, int score9) {
            int problem = score1 + score2;
            int solution = score3 + score4 + score5;
            int scaleUp = score6 + score7;
            int effect = score8 + score9;

            return TeamScoreItem.builder()
                    .teamId(teamId)
                    .teamName(teamName)
                    .problemScore(problem)
                    .solutionScore(solution)
                    .scaleUpScore(scaleUp)
                    .effectScore(effect)
                    .totalScore(problem + solution + scaleUp + effect)
                    .build();
        }
    }
}
