package qtedu.Impact_design.domain.implementation.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.ImpactCheckScoreItem;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.WinCanvasWithScore;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.model.ImpactCheckModel;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WinCanvasScoreCalculatorTest {

    private final WinCanvasScoreCalculator calculator = new WinCanvasScoreCalculator();

    @Nested
    @DisplayName("ImpactCheck 점수 변환")
    class ImpactCheckScores {

        @Test
        @DisplayName("ImpactCheckModel을 ImpactCheckScoreItem으로 정상 변환한다")
        void convertsImpactCheckToScoreItem() {
            ImpactCheckModel ic = ImpactCheckModel.builder()
                    .userId(1L)
                    .q1Score(5).q2Score(4).q3Score(3).q4Score(2)
                    .q5Score(1).q6Score(5).q7Score(4).q8Score(3)
                    .q9Score(2).q10Score(1).q11Score(5).q12Score(4)
                    .build();

            ReportRawData raw = ReportRawData.builder()
                    .impactChecks(List.of(ic))
                    .quickCanvases(Collections.emptyList())
                    .buildCanvases(Collections.emptyList())
                    .quickIntents(Collections.emptyList())
                    .buildIntents(Collections.emptyList())
                    .build();

            ReportScoreResult result = calculator.calculate(raw);

            assertThat(result.getImpactCheckScores()).hasSize(1);
            ImpactCheckScoreItem item = result.getImpactCheckScores().get(0);
            assertThat(item.getUserId()).isEqualTo(1L);
            assertThat(item.getQ1Score()).isEqualTo(5);
            assertThat(item.getQ12Score()).isEqualTo(4);
        }

        @Test
        @DisplayName("빈 ImpactCheck 리스트이면 빈 결과를 반환한다")
        void returnsEmptyForNoImpactChecks() {
            ReportRawData raw = ReportRawData.builder()
                    .impactChecks(Collections.emptyList())
                    .quickCanvases(Collections.emptyList())
                    .buildCanvases(Collections.emptyList())
                    .quickIntents(Collections.emptyList())
                    .buildIntents(Collections.emptyList())
                    .build();

            ReportScoreResult result = calculator.calculate(raw);

            assertThat(result.getImpactCheckScores()).isEmpty();
        }
    }

    @Nested
    @DisplayName("BuildWin 캔버스 점수 계산")
    class BuildWinScores {

        @Test
        @DisplayName("intent의 score1~9 합산이 totalScore로 계산된다")
        void calculatesTotalScoreFromIntents() {
            WinCanvasModel canvas = WinCanvasModel.builder()
                    .canvasId(10L).userId(1L)
                    .taskName("태스크A").taskDescription("설명A")
                    .build();

            FLetterOfIntentModel intent = FLetterOfIntentModel.builder()
                    .intentIndex(1).userId(100L).canvasId(10L)
                    .score1(1).score2(2).score3(3).score4(4).score5(5)
                    .score6(1).score7(2).score8(3).score9(4)
                    .build();

            ReportRawData raw = ReportRawData.builder()
                    .impactChecks(Collections.emptyList())
                    .quickCanvases(Collections.emptyList())
                    .buildCanvases(List.of(canvas))
                    .quickIntents(Collections.emptyList())
                    .buildIntents(List.of(intent))
                    .build();

            ReportScoreResult result = calculator.calculate(raw);

            assertThat(result.getBuildWinCanvasList()).hasSize(1);
            WinCanvasWithScore winCanvas = result.getBuildWinCanvasList().get(0);
            assertThat(winCanvas.getTotalScore()).isEqualTo(25); // 1+2+3+4+5+1+2+3+4
            assertThat(winCanvas.getEvaluations()).hasSize(1);
            assertThat(winCanvas.getEvaluations().get(0).getTotalScore()).isEqualTo(25);
        }

        @Test
        @DisplayName("여러 intent의 합산이 teamTotalScore가 된다")
        void sumsMultipleIntentsForTeamTotal() {
            WinCanvasModel canvas = WinCanvasModel.builder()
                    .canvasId(10L).userId(1L)
                    .taskName("태스크A").taskDescription("설명A")
                    .build();

            FLetterOfIntentModel intent1 = FLetterOfIntentModel.builder()
                    .intentIndex(1).userId(100L).canvasId(10L)
                    .score1(1).score2(1).score3(1).score4(1).score5(1)
                    .score6(1).score7(1).score8(1).score9(1)
                    .build();

            FLetterOfIntentModel intent2 = FLetterOfIntentModel.builder()
                    .intentIndex(2).userId(200L).canvasId(10L)
                    .score1(2).score2(2).score3(2).score4(2).score5(2)
                    .score6(2).score7(2).score8(2).score9(2)
                    .build();

            ReportRawData raw = ReportRawData.builder()
                    .impactChecks(Collections.emptyList())
                    .quickCanvases(Collections.emptyList())
                    .buildCanvases(List.of(canvas))
                    .quickIntents(Collections.emptyList())
                    .buildIntents(List.of(intent1, intent2))
                    .build();

            ReportScoreResult result = calculator.calculate(raw);

            // intent1 total = 9, intent2 total = 18, teamTotal = 27
            assertThat(result.getBuildWinCanvasList().get(0).getTotalScore()).isEqualTo(27);
        }

        @Test
        @DisplayName("null 점수는 0으로 처리한다")
        void handlesNullScores() {
            WinCanvasModel canvas = WinCanvasModel.builder()
                    .canvasId(10L).userId(1L)
                    .taskName("태스크A").taskDescription("설명A")
                    .build();

            FLetterOfIntentModel intent = FLetterOfIntentModel.builder()
                    .intentIndex(1).userId(100L).canvasId(10L)
                    .score1(5).score2(null).score3(null).score4(null).score5(null)
                    .score6(null).score7(null).score8(null).score9(null)
                    .build();

            ReportRawData raw = ReportRawData.builder()
                    .impactChecks(Collections.emptyList())
                    .quickCanvases(Collections.emptyList())
                    .buildCanvases(List.of(canvas))
                    .quickIntents(Collections.emptyList())
                    .buildIntents(List.of(intent))
                    .build();

            ReportScoreResult result = calculator.calculate(raw);

            assertThat(result.getBuildWinCanvasList().get(0).getTotalScore()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("QuickWin 캔버스 점수 계산")
    class QuickWinScores {

        @Test
        @DisplayName("FLetterOfIntentModel(QUICK) 기반으로 점수를 계산한다")
        void calculatesFromQuickIntentModel() {
            WinCanvasModel canvas = WinCanvasModel.builder()
                    .canvasId(20L).userId(2L)
                    .taskName("퀵태스크").taskDescription("퀵설명")
                    .build();

            FLetterOfIntentModel intent = FLetterOfIntentModel.builder()
                    .intentIndex(1).userId(100L).canvasId(20L)
                    .score1(3).score2(3).score3(3).score4(3).score5(3)
                    .score6(3).score7(3).score8(3).score9(3)
                    .build();

            ReportRawData raw = ReportRawData.builder()
                    .impactChecks(Collections.emptyList())
                    .quickCanvases(List.of(canvas))
                    .buildCanvases(Collections.emptyList())
                    .quickIntents(List.of(intent))
                    .buildIntents(Collections.emptyList())
                    .build();

            ReportScoreResult result = calculator.calculate(raw);

            assertThat(result.getQuickWinCanvasList()).hasSize(1);
            assertThat(result.getQuickWinCanvasList().get(0).getTotalScore()).isEqualTo(27); // 3*9
        }
    }
}
