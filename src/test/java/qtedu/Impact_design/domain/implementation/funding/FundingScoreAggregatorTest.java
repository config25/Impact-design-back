package qtedu.Impact_design.domain.implementation.funding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.response.funding.FundingMyResultResponse;
import qtedu.Impact_design.api.dto.response.funding.FundingScoresResponse;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FundingScoreAggregatorTest {

    @InjectMocks
    private FundingScoreAggregator aggregator;

    @Mock
    private TbTeamRepository tbTeamRepository;

    @Nested
    @DisplayName("aggregateResult")
    class AggregateResult {

        @Test
        @DisplayName("빈 목록이면 빈 ResultSection을 반환한다")
        void emptyList() {
            FundingMyResultResponse.ResultSection result = aggregator.aggregateResult(Collections.emptyList());

            assertThat(result.getScores().getProblemScore()).isZero();
            assertThat(result.getScores().getEvaluatorCount()).isZero();
            assertThat(result.getOpinions()).isEmpty();
        }

        @Test
        @DisplayName("단일 투자의 점수를 올바르게 집계한다")
        void singleInvestment() {
            FLetterOfIntentModel inv = FLetterOfIntentModel.builder()
                    .score1(3).score2(4)       // problem = 7
                    .score3(5).score4(2).score5(3) // solution = 10
                    .score6(1).score7(2)       // scaleUp = 3
                    .score8(4).score9(5)       // effect = 9
                    .opinion("좋은 아이디어")
                    .build();

            FundingMyResultResponse.ResultSection result = aggregator.aggregateResult(List.of(inv));

            assertThat(result.getScores().getProblemScore()).isEqualTo(7);
            assertThat(result.getScores().getSolutionScore()).isEqualTo(10);
            assertThat(result.getScores().getScaleUpScore()).isEqualTo(3);
            assertThat(result.getScores().getEffectScore()).isEqualTo(9);
            assertThat(result.getScores().getEvaluatorCount()).isEqualTo(1);
            assertThat(result.getOpinions()).containsExactly("좋은 아이디어");
        }

        @Test
        @DisplayName("복수 투자의 점수를 합산 후 평균한다")
        void multipleInvestments() {
            FLetterOfIntentModel inv1 = FLetterOfIntentModel.builder()
                    .score1(2).score2(2)
                    .score3(3).score4(3).score5(3)
                    .score6(4).score7(4)
                    .score8(5).score9(5)
                    .opinion("의견1")
                    .build();
            FLetterOfIntentModel inv2 = FLetterOfIntentModel.builder()
                    .score1(4).score2(4)
                    .score3(5).score4(5).score5(5)
                    .score6(6).score7(6)
                    .score8(3).score9(3)
                    .opinion("")
                    .build();

            FundingMyResultResponse.ResultSection result = aggregator.aggregateResult(List.of(inv1, inv2));

            // 평균: problem=(4+8)/2=6, solution=(9+15)/2=12, scaleUp=(8+12)/2=10, effect=(10+6)/2=8
            assertThat(result.getScores().getProblemScore()).isEqualTo(6);
            assertThat(result.getScores().getSolutionScore()).isEqualTo(12);
            assertThat(result.getScores().getScaleUpScore()).isEqualTo(10);
            assertThat(result.getScores().getEffectScore()).isEqualTo(8);
            assertThat(result.getScores().getEvaluatorCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("null 점수는 0으로 처리한다")
        void nullScores() {
            FLetterOfIntentModel inv = FLetterOfIntentModel.builder()
                    .score1(null).score2(3)
                    .score3(null).score4(null).score5(null)
                    .score6(2).score7(null)
                    .score8(null).score9(1)
                    .build();

            FundingMyResultResponse.ResultSection result = aggregator.aggregateResult(List.of(inv));

            assertThat(result.getScores().getProblemScore()).isEqualTo(3);
            assertThat(result.getScores().getSolutionScore()).isZero();
            assertThat(result.getScores().getScaleUpScore()).isEqualTo(2);
            assertThat(result.getScores().getEffectScore()).isEqualTo(1);
        }

        @Test
        @DisplayName("null/blank 의견은 필터링된다")
        void filtersBlankOpinions() {
            FLetterOfIntentModel inv1 = FLetterOfIntentModel.builder()
                    .score1(1).score2(1).score3(1).score4(1).score5(1)
                    .score6(1).score7(1).score8(1).score9(1)
                    .opinion(null)
                    .build();
            FLetterOfIntentModel inv2 = FLetterOfIntentModel.builder()
                    .score1(1).score2(1).score3(1).score4(1).score5(1)
                    .score6(1).score7(1).score8(1).score9(1)
                    .opinion("   ")
                    .build();
            FLetterOfIntentModel inv3 = FLetterOfIntentModel.builder()
                    .score1(1).score2(1).score3(1).score4(1).score5(1)
                    .score6(1).score7(1).score8(1).score9(1)
                    .opinion("유효한 의견")
                    .build();

            FundingMyResultResponse.ResultSection result = aggregator.aggregateResult(List.of(inv1, inv2, inv3));

            assertThat(result.getOpinions()).containsExactly("유효한 의견");
        }
    }

    @Nested
    @DisplayName("createScoreItem")
    class CreateScoreItem {

        @Test
        @DisplayName("teamNameMap이 있으면 팀 이름을 매핑한다")
        void withTeamNameMap() {
            FLetterOfIntentModel inv = FLetterOfIntentModel.builder()
                    .investmentTarget("5")
                    .score1(1).score2(2).score3(3).score4(4).score5(5)
                    .score6(1).score7(2).score8(3).score9(4)
                    .build();
            Map<Integer, String> teamNameMap = Map.of(5, "팀A");

            FundingScoresResponse.TeamScoreItem item = aggregator.createScoreItem(inv, teamNameMap);

            assertThat(item.getTeamId()).isEqualTo(5);
            assertThat(item.getTeamName()).isEqualTo("팀A");
            assertThat(item.getProblemScore()).isEqualTo(3);   // 1+2
            assertThat(item.getSolutionScore()).isEqualTo(12); // 3+4+5
            assertThat(item.getScaleUpScore()).isEqualTo(3);   // 1+2
            assertThat(item.getEffectScore()).isEqualTo(7);    // 3+4
        }

        @Test
        @DisplayName("teamNameMap에 없는 팀은 Unknown으로 표시한다")
        void unknownTeam() {
            FLetterOfIntentModel inv = FLetterOfIntentModel.builder()
                    .investmentTarget("99")
                    .score1(0).score2(0).score3(0).score4(0).score5(0)
                    .score6(0).score7(0).score8(0).score9(0)
                    .build();

            FundingScoresResponse.TeamScoreItem item = aggregator.createScoreItem(inv, Map.of());

            assertThat(item.getTeamName()).isEqualTo("Unknown");
        }

        @Test
        @DisplayName("repository 조회 방식으로 팀 이름을 가져온다")
        void withRepository() {
            FLetterOfIntentModel inv = FLetterOfIntentModel.builder()
                    .investmentTarget("10")
                    .score1(5).score2(5).score3(5).score4(5).score5(5)
                    .score6(5).score7(5).score8(5).score9(5)
                    .build();
            given(tbTeamRepository.findByTeamId(10))
                    .willReturn(Optional.of(TbTeamModel.builder().name("팀B").build()));

            FundingScoresResponse.TeamScoreItem item = aggregator.createScoreItem(inv);

            assertThat(item.getTeamName()).isEqualTo("팀B");
            assertThat(item.getTotalScore()).isEqualTo(45);
        }
    }
}
