package qtedu.Impact_design.api.dto.response.report;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReportResponse {

    private String className;

    private String target;

    private String projectDate;

    private String imageUrl;

    private List<ImpactCheckScoreItem> impactCheckScores;

    private FrequencyAnalysis externalThreats;

    private FrequencyAnalysis internalLimitations;

    private VisionMissionValue visionMissionValue;

    private GoalAnalysis flowCanvasGoals;

    private List<TacticalItem> tacticals;

    private List<StrategicActivityItem> strategicActivities;

    private List<WinCanvasWithScore> quickWinCanvasList;

    private List<WinCanvasWithScore> buildWinCanvasList;

    @Getter
    @Builder
    public static class ImpactCheckScoreItem {
        private Long userId;
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
    }

    @Getter
    @Builder
    public static class FrequencyAnalysis {
        private List<String> allData;
        private List<FrequencyItem> top4; // top4개
        private List<String> keywords; // 4개
        private List<String> aiSummary; // AI 요약
    }

    @Getter
    @Builder
    public static class FrequencyItem {
        private String content;
        private Integer count;
    }

    @Getter
    @Builder
    public static class VisionMissionValue {
        private List<FrequencyItem> visionTop4;
        private List<FrequencyItem> missionTop4;
        private List<FrequencyItem> valueTop4;
        private String aiVision;
        private String aiMission;
        private String aiValue;
    }

    @Getter
    @Builder
    public static class GoalAnalysis {
        private List<GoalItem> goals;
        private List<String> keywords;
    }

    @Getter
    @Builder
    public static class GoalItem {
        private String title;
        private String description;
        private Integer count;
    }

    @Getter
    @Builder
    public static class TacticalItem {
        private String metric;
        private String goal;
    }

    @Getter
    @Builder
    public static class StrategicActivityItem {
        private String activityMetric;
        private String interCriteria;
    }

    @Getter
    @Builder
    public static class WinCanvasWithScore {
        private Long canvasId;
        private Long userId;
        private String taskName;
        private String taskDescription;
        private Integer totalScore;
        private List<ScoreDetail> evaluations;
    }

    @Getter
    @Builder
    public static class ScoreDetail {
        private Integer intentIndex;
        private Long stdntNo;
        private Integer score1;
        private Integer score2;
        private Integer score3;
        private Integer score4;
        private Integer score5;
        private Integer score6;
        private Integer score7;
        private Integer score8;
        private Integer score9;
        private Integer totalScore;
    }
}
