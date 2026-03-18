package qtedu.Impact_design.domain.implementation.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.implementation.flowcanvas.FlowCanvasReader;
import qtedu.Impact_design.domain.implementation.identitycanvas.IdentityCanvasReader;
import qtedu.Impact_design.domain.implementation.impactcheck.ImpactCheckReader;
import qtedu.Impact_design.domain.implementation.team.TeamReader;
import qtedu.Impact_design.domain.implementation.wincanvas.WinCanvasReader;
import qtedu.Impact_design.domain.model.FLetterOfIntentModel;
import qtedu.Impact_design.domain.model.ImpactCheckModel;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;
import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReportDataAggregatorTest {

    @Mock private TeamReader teamReader;
    @Mock private ImpactCheckReader impactCheckReader;
    @Mock private IdentityCanvasReader identityCanvasReader;
    @Mock private FlowCanvasReader flowCanvasReader;
    @Mock private WinCanvasReader winCanvasReader;
    @Mock private FLetterOfIntentRepository fLetterOfIntentRepository;
    @Mock private TeamUserRepository teamUserRepository;

    @InjectMocks
    private ReportDataAggregator reportDataAggregator;

    @Nested
    @DisplayName("load - 단건 팀 데이터 로드")
    class Load {

        @Test
        @DisplayName("팀원들의 모든 캔버스 데이터를 조합하여 반환한다")
        void loadsAllDataForTeam() {
            Integer teamId = 1;
            List<Long> userIds = List.of(5L, 6L);
            given(teamReader.readTeamMemberUserIdsByTeamId(teamId)).willReturn(userIds);

            ImpactCheckModel ic = ImpactCheckModel.builder().userId(5L).q1Score(4).build();
            given(impactCheckReader.readByUserIds(userIds)).willReturn(List.of(ic));

            IdentityCanvasModel identity = IdentityCanvasModel.builder().userId(5L).mission("미션").build();
            given(identityCanvasReader.readByUserIds(userIds)).willReturn(List.of(identity));

            FlowCanvasModel flow = FlowCanvasModel.builder().goalId(1L).userId(5L).goalTitle("목표1").build();
            given(flowCanvasReader.readByUserIds(userIds)).willReturn(List.of(flow));

            TacticalModel tactical = TacticalModel.builder().metricId(1L).goalId(1L).tacticalMetric("지표").build();
            given(flowCanvasReader.readTacticalsByGoalIds(List.of(1L))).willReturn(List.of(tactical));

            StrategicActivityModel strategic = StrategicActivityModel.builder().activityId(1L).goalId(1L).activityMetric("행동지표").build();
            given(flowCanvasReader.readStrategicActivitiesByGoalIds(List.of(1L))).willReturn(List.of(strategic));

            WinCanvasModel quick = WinCanvasModel.builder().canvasId(1L).userId(5L).taskName("퀵윈").build();
            WinCanvasModel build = WinCanvasModel.builder().canvasId(2L).userId(5L).taskName("빌드윈").build();
            given(winCanvasReader.readCanvasesByUserIds(userIds, CanvasType.QUICK)).willReturn(List.of(quick));
            given(winCanvasReader.readCanvasesByUserIds(userIds, CanvasType.BUILD)).willReturn(List.of(build));

            given(fLetterOfIntentRepository.findByTargetTeamId(teamId, CanvasType.QUICK)).willReturn(Collections.emptyList());
            given(fLetterOfIntentRepository.findByTargetTeamId(teamId, CanvasType.BUILD)).willReturn(Collections.emptyList());

            ReportRawData result = reportDataAggregator.load(teamId);

            assertThat(result.getImpactChecks()).hasSize(1);
            assertThat(result.getIdentityCanvases()).hasSize(1);
            assertThat(result.getFlowCanvases()).hasSize(1);
            assertThat(result.getTacticals()).hasSize(1);
            assertThat(result.getStrategicActivities()).hasSize(1);
            assertThat(result.getQuickCanvases()).hasSize(1);
            assertThat(result.getBuildCanvases()).hasSize(1);
        }

        @Test
        @DisplayName("팀원이 없으면 빈 데이터를 반환한다")
        void returnsEmptyDataWhenNoMembers() {
            Integer teamId = 99;
            given(teamReader.readTeamMemberUserIdsByTeamId(teamId)).willReturn(Collections.emptyList());
            given(impactCheckReader.readByUserIds(Collections.emptyList())).willReturn(Collections.emptyList());
            given(identityCanvasReader.readByUserIds(Collections.emptyList())).willReturn(Collections.emptyList());
            given(flowCanvasReader.readByUserIds(Collections.emptyList())).willReturn(Collections.emptyList());
            given(flowCanvasReader.readTacticalsByGoalIds(Collections.emptyList())).willReturn(Collections.emptyList());
            given(flowCanvasReader.readStrategicActivitiesByGoalIds(Collections.emptyList())).willReturn(Collections.emptyList());
            given(winCanvasReader.readCanvasesByUserIds(Collections.emptyList(), CanvasType.QUICK)).willReturn(Collections.emptyList());
            given(winCanvasReader.readCanvasesByUserIds(Collections.emptyList(), CanvasType.BUILD)).willReturn(Collections.emptyList());
            given(fLetterOfIntentRepository.findByTargetTeamId(teamId, CanvasType.QUICK)).willReturn(Collections.emptyList());
            given(fLetterOfIntentRepository.findByTargetTeamId(teamId, CanvasType.BUILD)).willReturn(Collections.emptyList());

            ReportRawData result = reportDataAggregator.load(teamId);

            assertThat(result.getImpactChecks()).isEmpty();
            assertThat(result.getFlowCanvases()).isEmpty();
            assertThat(result.getQuickCanvases()).isEmpty();
        }
    }

    @Nested
    @DisplayName("loadBulk - 벌크 데이터 로드")
    class LoadBulk {

        @Test
        @DisplayName("여러 팀의 데이터를 팀별로 분리하여 반환한다")
        void loadsAndSplitsDataByTeam() {
            List<Integer> teamIds = List.of(1, 2);

            given(teamUserRepository.findByTeamIdIn(teamIds)).willReturn(List.of(
                    TeamUserModel.builder().teamId(1).userId(5L).build(),
                    TeamUserModel.builder().teamId(1).userId(6L).build(),
                    TeamUserModel.builder().teamId(2).userId(8L).build()
            ));

            List<Long> allUserIds = List.of(5L, 6L, 8L);

            given(impactCheckReader.readByUserIds(allUserIds)).willReturn(List.of(
                    ImpactCheckModel.builder().userId(5L).q1Score(4).build(),
                    ImpactCheckModel.builder().userId(8L).q1Score(3).build()
            ));
            given(identityCanvasReader.readByUserIds(allUserIds)).willReturn(Collections.emptyList());

            FlowCanvasModel flow5 = FlowCanvasModel.builder().goalId(1L).userId(5L).goalTitle("목표A").build();
            FlowCanvasModel flow8 = FlowCanvasModel.builder().goalId(2L).userId(8L).goalTitle("목표B").build();
            given(flowCanvasReader.readByUserIds(allUserIds)).willReturn(List.of(flow5, flow8));

            given(flowCanvasReader.readTacticalsByGoalIds(List.of(1L, 2L))).willReturn(List.of(
                    TacticalModel.builder().metricId(1L).goalId(1L).tacticalMetric("지표1").build(),
                    TacticalModel.builder().metricId(2L).goalId(2L).tacticalMetric("지표2").build()
            ));
            given(flowCanvasReader.readStrategicActivitiesByGoalIds(List.of(1L, 2L))).willReturn(Collections.emptyList());
            given(winCanvasReader.readCanvasesByUserIds(allUserIds, CanvasType.QUICK)).willReturn(Collections.emptyList());
            given(winCanvasReader.readCanvasesByUserIds(allUserIds, CanvasType.BUILD)).willReturn(Collections.emptyList());
            given(fLetterOfIntentRepository.findByTargetTeamIds(teamIds, CanvasType.BUILD)).willReturn(Collections.emptyList());
            given(fLetterOfIntentRepository.findByTargetTeamIds(teamIds, CanvasType.QUICK)).willReturn(Collections.emptyList());

            Map<Integer, ReportRawData> result = reportDataAggregator.loadBulk(teamIds);

            assertThat(result).hasSize(2);

            // 팀1: userId 5, 6
            ReportRawData team1 = result.get(1);
            assertThat(team1.getImpactChecks()).hasSize(1);
            assertThat(team1.getImpactChecks().get(0).getUserId()).isEqualTo(5L);
            assertThat(team1.getFlowCanvases()).hasSize(1);
            assertThat(team1.getTacticals()).hasSize(1);
            assertThat(team1.getTacticals().get(0).getTacticalMetric()).isEqualTo("지표1");

            // 팀2: userId 8
            ReportRawData team2 = result.get(2);
            assertThat(team2.getImpactChecks()).hasSize(1);
            assertThat(team2.getImpactChecks().get(0).getUserId()).isEqualTo(8L);
            assertThat(team2.getTacticals()).hasSize(1);
            assertThat(team2.getTacticals().get(0).getTacticalMetric()).isEqualTo("지표2");
        }

        @Test
        @DisplayName("팀원이 없는 팀은 빈 데이터로 반환한다")
        void returnsEmptyDataForTeamWithNoMembers() {
            List<Integer> teamIds = List.of(1, 2);

            // 팀1에만 유저 존재
            given(teamUserRepository.findByTeamIdIn(teamIds)).willReturn(List.of(
                    TeamUserModel.builder().teamId(1).userId(5L).build()
            ));

            given(impactCheckReader.readByUserIds(List.of(5L))).willReturn(Collections.emptyList());
            given(identityCanvasReader.readByUserIds(List.of(5L))).willReturn(Collections.emptyList());
            given(flowCanvasReader.readByUserIds(List.of(5L))).willReturn(Collections.emptyList());
            given(flowCanvasReader.readTacticalsByGoalIds(Collections.emptyList())).willReturn(Collections.emptyList());
            given(flowCanvasReader.readStrategicActivitiesByGoalIds(Collections.emptyList())).willReturn(Collections.emptyList());
            given(winCanvasReader.readCanvasesByUserIds(List.of(5L), CanvasType.QUICK)).willReturn(Collections.emptyList());
            given(winCanvasReader.readCanvasesByUserIds(List.of(5L), CanvasType.BUILD)).willReturn(Collections.emptyList());
            given(fLetterOfIntentRepository.findByTargetTeamIds(teamIds, CanvasType.BUILD)).willReturn(Collections.emptyList());
            given(fLetterOfIntentRepository.findByTargetTeamIds(teamIds, CanvasType.QUICK)).willReturn(Collections.emptyList());

            Map<Integer, ReportRawData> result = reportDataAggregator.loadBulk(teamIds);

            ReportRawData team2 = result.get(2);
            assertThat(team2.getImpactChecks()).isEmpty();
            assertThat(team2.getFlowCanvases()).isEmpty();
            assertThat(team2.getTacticals()).isEmpty();
        }
    }
}
