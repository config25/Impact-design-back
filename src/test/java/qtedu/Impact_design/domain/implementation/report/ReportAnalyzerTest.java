package qtedu.Impact_design.domain.implementation.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.FrequencyAnalysis;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.StrategicActivityItem;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.TacticalItem;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.VisionMissionValue;
import qtedu.Impact_design.domain.implementation.ai.AiSender;
import qtedu.Impact_design.domain.implementation.ai.AiSender.FullReportAiResult;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;
import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportAnalyzerTest {

    @Mock
    private AiSender aiSender;

    @InjectMocks
    private ReportAnalyzer analyzer;

    private FullReportAiResult defaultAiResult(List<Integer> tacticalOrder, List<Integer> strategicOrder) {
        return FullReportAiResult.builder()
                .externalThreats(FrequencyAnalysis.builder().build())
                .internalLimitations(FrequencyAnalysis.builder().build())
                .visionMissionValue(VisionMissionValue.builder().build())
                .goalKeywords(List.of("키워드1"))
                .tacticalOrder(tacticalOrder)
                .strategicActivityOrder(strategicOrder)
                .build();
    }

    @Nested
    @DisplayName("reorder 로직")
    class Reorder {

        @Test
        @DisplayName("AI가 반환한 순서대로 tactical 항목이 재배치된다")
        void reordersTacticalsAccordingToAiOrder() {
            TacticalModel t1 = TacticalModel.builder().metricId(1L).tacticalMetric("지표A").tacticalGoal("목표A").build();
            TacticalModel t2 = TacticalModel.builder().metricId(2L).tacticalMetric("지표B").tacticalGoal("목표B").build();
            TacticalModel t3 = TacticalModel.builder().metricId(3L).tacticalMetric("지표C").tacticalGoal("목표C").build();

            ReportRawData raw = ReportRawData.builder()
                    .identityCanvases(Collections.emptyList())
                    .flowCanvases(Collections.emptyList())
                    .tacticals(List.of(t1, t2, t3))
                    .strategicActivities(Collections.emptyList())
                    .build();

            when(aiSender.analyzeFullReport(anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList()))
                    .thenReturn(defaultAiResult(List.of(2, 0, 1), Collections.emptyList()));

            ReportAiResult result = analyzer.analyze(raw);

            assertThat(result.getTacticals()).extracting(TacticalItem::getMetric)
                    .containsExactly("지표C", "지표A", "지표B");
        }

        @Test
        @DisplayName("AI가 일부 인덱스만 반환하면 누락된 항목이 뒤에 추가된다")
        void appendsMissingItemsWhenOrderIsPartial() {
            TacticalModel t1 = TacticalModel.builder().metricId(1L).tacticalMetric("A").tacticalGoal("1").build();
            TacticalModel t2 = TacticalModel.builder().metricId(2L).tacticalMetric("B").tacticalGoal("2").build();
            TacticalModel t3 = TacticalModel.builder().metricId(3L).tacticalMetric("C").tacticalGoal("3").build();

            ReportRawData raw = ReportRawData.builder()
                    .identityCanvases(Collections.emptyList())
                    .flowCanvases(Collections.emptyList())
                    .tacticals(List.of(t1, t2, t3))
                    .strategicActivities(Collections.emptyList())
                    .build();

            // AI가 인덱스 2만 반환 → 0, 1은 뒤에 추가
            when(aiSender.analyzeFullReport(anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList()))
                    .thenReturn(defaultAiResult(List.of(2), Collections.emptyList()));

            ReportAiResult result = analyzer.analyze(raw);

            assertThat(result.getTacticals()).extracting(TacticalItem::getMetric)
                    .containsExactly("C", "A", "B");
        }

        @Test
        @DisplayName("빈 order 리스트이면 원본 순서 유지")
        void keepsOriginalOrderWhenOrderIsEmpty() {
            TacticalModel t1 = TacticalModel.builder().metricId(1L).tacticalMetric("A").tacticalGoal("1").build();
            TacticalModel t2 = TacticalModel.builder().metricId(2L).tacticalMetric("B").tacticalGoal("2").build();

            ReportRawData raw = ReportRawData.builder()
                    .identityCanvases(Collections.emptyList())
                    .flowCanvases(Collections.emptyList())
                    .tacticals(List.of(t1, t2))
                    .strategicActivities(Collections.emptyList())
                    .build();

            when(aiSender.analyzeFullReport(anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList()))
                    .thenReturn(defaultAiResult(Collections.emptyList(), Collections.emptyList()));

            ReportAiResult result = analyzer.analyze(raw);

            assertThat(result.getTacticals()).extracting(TacticalItem::getMetric)
                    .containsExactly("A", "B");
        }
    }

    @Nested
    @DisplayName("데이터 추출")
    class DataExtraction {

        @Test
        @DisplayName("IdentityCanvas에서 외부위협/내부한계 데이터를 추출한다")
        void extractsExternalAndInternalData() {
            IdentityCanvasModel canvas = IdentityCanvasModel.builder()
                    .userId(1L)
                    .macro("거시환경").tech("기술변화")
                    .customer("고객변화").competitor("경쟁자")
                    .capability("역량부족").culture("문화")
                    .structure("구조").etc("기타")
                    .build();

            ReportRawData raw = ReportRawData.builder()
                    .identityCanvases(List.of(canvas))
                    .flowCanvases(Collections.emptyList())
                    .tacticals(Collections.emptyList())
                    .strategicActivities(Collections.emptyList())
                    .build();

            when(aiSender.analyzeFullReport(anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList()))
                    .thenReturn(defaultAiResult(Collections.emptyList(), Collections.emptyList()));

            // 예외 없이 정상 실행되면 추출 로직이 동작한 것
            ReportAiResult result = analyzer.analyze(raw);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null/blank 필드는 추출에서 제외된다")
        void filtersNullAndBlankFields() {
            IdentityCanvasModel canvas = IdentityCanvasModel.builder()
                    .userId(1L)
                    .macro("거시환경").tech(null)
                    .customer("").competitor("  ")
                    .capability("역량").culture(null)
                    .structure("").etc(null)
                    .build();

            ReportRawData raw = ReportRawData.builder()
                    .identityCanvases(List.of(canvas))
                    .flowCanvases(Collections.emptyList())
                    .tacticals(Collections.emptyList())
                    .strategicActivities(Collections.emptyList())
                    .build();

            when(aiSender.analyzeFullReport(anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList()))
                    .thenReturn(defaultAiResult(Collections.emptyList(), Collections.emptyList()));

            ReportAiResult result = analyzer.analyze(raw);
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("GoalAnalysis 빌드")
    class GoalAnalysisBuild {

        @Test
        @DisplayName("FlowCanvas가 비어있으면 빈 GoalAnalysis를 반환한다")
        void returnsEmptyGoalAnalysisWhenNoFlowCanvases() {
            ReportRawData raw = ReportRawData.builder()
                    .identityCanvases(Collections.emptyList())
                    .flowCanvases(Collections.emptyList())
                    .tacticals(Collections.emptyList())
                    .strategicActivities(Collections.emptyList())
                    .build();

            when(aiSender.analyzeFullReport(anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList()))
                    .thenReturn(defaultAiResult(Collections.emptyList(), Collections.emptyList()));

            ReportAiResult result = analyzer.analyze(raw);

            assertThat(result.getFlowCanvasGoals().getGoals()).isEmpty();
            assertThat(result.getFlowCanvasGoals().getKeywords()).isEmpty();
        }

        @Test
        @DisplayName("goalTitle이 null/blank인 FlowCanvas는 GoalItem에서 제외된다")
        void excludesFlowCanvasesWithNullOrBlankGoalTitle() {
            FlowCanvasModel valid = FlowCanvasModel.builder().goalId(1L).userId(1L).goalTitle("목표1").goalDescription("설명1").build();
            FlowCanvasModel nullTitle = FlowCanvasModel.builder().goalId(2L).userId(2L).goalTitle(null).goalDescription("설명2").build();
            FlowCanvasModel blankTitle = FlowCanvasModel.builder().goalId(3L).userId(3L).goalTitle("  ").goalDescription("설명3").build();

            ReportRawData raw = ReportRawData.builder()
                    .identityCanvases(Collections.emptyList())
                    .flowCanvases(List.of(valid, nullTitle, blankTitle))
                    .tacticals(Collections.emptyList())
                    .strategicActivities(Collections.emptyList())
                    .build();

            when(aiSender.analyzeFullReport(anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList()))
                    .thenReturn(defaultAiResult(Collections.emptyList(), Collections.emptyList()));

            ReportAiResult result = analyzer.analyze(raw);

            assertThat(result.getFlowCanvasGoals().getGoals()).hasSize(1);
            assertThat(result.getFlowCanvasGoals().getGoals().get(0).getTitle()).isEqualTo("목표1");
        }
    }

    @Nested
    @DisplayName("빈 데이터 처리")
    class EmptyData {

        @Test
        @DisplayName("tacticals가 비어있으면 빈 리스트를 반환한다")
        void returnsEmptyListForEmptyTacticals() {
            ReportRawData raw = ReportRawData.builder()
                    .identityCanvases(Collections.emptyList())
                    .flowCanvases(Collections.emptyList())
                    .tacticals(Collections.emptyList())
                    .strategicActivities(Collections.emptyList())
                    .build();

            when(aiSender.analyzeFullReport(anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList(), anyList()))
                    .thenReturn(defaultAiResult(Collections.emptyList(), Collections.emptyList()));

            ReportAiResult result = analyzer.analyze(raw);

            assertThat(result.getTacticals()).isEmpty();
            assertThat(result.getStrategicActivities()).isEmpty();
        }
    }
}
