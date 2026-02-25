package qtedu.Impact_design.domain.implementation.report;

import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.ImpactCheckScoreItem;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.ScoreDetail;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.WinCanvasWithScore;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.model.FLetterOfIntent2Model;
import qtedu.Impact_design.domain.model.ImpactCheckModel;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class WinCanvasScoreCalculator {

    public ReportScoreResult calculate(ReportRawData raw) {
        return ReportScoreResult.builder()
                .impactCheckScores(raw.getImpactChecks().stream()
                        .map(this::toScoreItem)
                        .collect(Collectors.toList()))
                .quickWinCanvasList(buildScoresFromIntent2(raw.getQuickCanvases(), raw.getQuickIntents()))
                .buildWinCanvasList(buildScores(raw.getBuildCanvases(), raw.getBuildIntents()))
                .build();
    }

    private ImpactCheckScoreItem toScoreItem(ImpactCheckModel ic) {
        return ImpactCheckScoreItem.builder()
                .userId(ic.getUserId())
                .q1Score(ic.getQ1Score()).q2Score(ic.getQ2Score())
                .q3Score(ic.getQ3Score()).q4Score(ic.getQ4Score())
                .q5Score(ic.getQ5Score()).q6Score(ic.getQ6Score())
                .q7Score(ic.getQ7Score()).q8Score(ic.getQ8Score())
                .q9Score(ic.getQ9Score()).q10Score(ic.getQ10Score())
                .q11Score(ic.getQ11Score()).q12Score(ic.getQ12Score())
                .build();
    }

    private List<WinCanvasWithScore> buildScores(List<WinCanvasModel> canvases, List<FLetterOfIntentModel> intents) {
        List<ScoreDetail> evaluations = intents.stream()
                .map(intent -> {
                    int total = sumScores(intent.getScore1(), intent.getScore2(), intent.getScore3(),
                            intent.getScore4(), intent.getScore5(), intent.getScore6(),
                            intent.getScore7(), intent.getScore8(), intent.getScore9());
                    return ScoreDetail.builder()
                            .intentIndex(intent.getIntentIndex()).stdntNo(intent.getStdntNo())
                            .score1(intent.getScore1()).score2(intent.getScore2())
                            .score3(intent.getScore3()).score4(intent.getScore4())
                            .score5(intent.getScore5()).score6(intent.getScore6())
                            .score7(intent.getScore7()).score8(intent.getScore8())
                            .score9(intent.getScore9()).totalScore(total)
                            .build();
                })
                .collect(Collectors.toList());

        int teamTotalScore = evaluations.stream().mapToInt(ScoreDetail::getTotalScore).sum();

        return canvases.stream()
                .map(canvas -> WinCanvasWithScore.builder()
                        .canvasId(canvas.getCanvasId()).userId(canvas.getUserId())
                        .taskName(canvas.getTaskName()).taskDescription(canvas.getTaskDescription())
                        .totalScore(teamTotalScore).evaluations(evaluations)
                        .build())
                .collect(Collectors.toList());
    }

    private List<WinCanvasWithScore> buildScoresFromIntent2(List<WinCanvasModel> canvases, List<FLetterOfIntent2Model> intents) {
        List<ScoreDetail> evaluations = intents.stream()
                .map(intent -> {
                    int total = sumScores(intent.getScore1(), intent.getScore2(), intent.getScore3(),
                            intent.getScore4(), intent.getScore5(), intent.getScore6(),
                            intent.getScore7(), intent.getScore8(), intent.getScore9());
                    return ScoreDetail.builder()
                            .intentIndex(intent.getIntentIndex()).stdntNo(intent.getStdntNo())
                            .score1(intent.getScore1()).score2(intent.getScore2())
                            .score3(intent.getScore3()).score4(intent.getScore4())
                            .score5(intent.getScore5()).score6(intent.getScore6())
                            .score7(intent.getScore7()).score8(intent.getScore8())
                            .score9(intent.getScore9()).totalScore(total)
                            .build();
                })
                .collect(Collectors.toList());

        int teamTotalScore = evaluations.stream().mapToInt(ScoreDetail::getTotalScore).sum();

        return canvases.stream()
                .map(canvas -> WinCanvasWithScore.builder()
                        .canvasId(canvas.getCanvasId()).userId(canvas.getUserId())
                        .taskName(canvas.getTaskName()).taskDescription(canvas.getTaskDescription())
                        .totalScore(teamTotalScore).evaluations(evaluations)
                        .build())
                .collect(Collectors.toList());
    }

    private int sumScores(Integer... scores) {
        int total = 0;
        for (Integer score : scores) {
            if (score != null) total += score;
        }
        return total;
    }
}
