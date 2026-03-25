package qtedu.Impact_design.domain.implementation.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.response.teach.ClassInfoResponse;
import qtedu.Impact_design.api.dto.response.teach.TeachListResponse;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.ClassInfoProjection;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbMissionDataModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.*;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TeachReaderTest {

    @Mock private GameRepository gameRepository;
    @Mock private MissionDataRepository missionDataRepository;
    @Mock private TbGameRepository tbGameRepository;
    @Mock private TbMissionRepository tbMissionRepository;
    @Mock private TbTeamRepository tbTeamRepository;
    @Mock private GameTeamRepository gameTeamRepository;
    @Mock private TeamUserRepository teamUserRepository;
    @Mock private ContentsRepository contentsRepository;
    @Mock private TeamSubmitStatusChecker submitStatusChecker;
    @Mock private UserinfoRepository userinfoRepository;

    @InjectMocks
    private TeachReader teachReader;

    @Nested
    @DisplayName("getTeachIndex - 수업 인덱스 조회")
    class GetTeachIndex {

        @Test
        @DisplayName("missionData의 최소 statusCeo를 계산하여 반환한다")
        void returnsMinStatusCeo() {
            ClassInfoProjection proj = mockProjection(1, 100, 10);
            given(gameRepository.findClassList(1L, 10)).willReturn(List.of(proj));
            given(missionDataRepository.findByMissionIdIn(List.of(100))).willReturn(List.of(
                    TbMissionDataModel.builder().missionId(100).statusCeo(15).build(),
                    TbMissionDataModel.builder().missionId(100).statusCeo(5).build()
            ));

            List<ClassInfoResponse> result = teachReader.getTeachIndex(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatusCeo()).isEqualTo(5);
        }

        @Test
        @DisplayName("missionId가 null이면 statusCeo는 20이다")
        void defaultStatusCeoWhenMissionIdNull() {
            ClassInfoProjection proj = mockProjection(1, null, 10);
            given(gameRepository.findClassList(1L, 10)).willReturn(List.of(proj));

            List<ClassInfoResponse> result = teachReader.getTeachIndex(1L);

            assertThat(result.get(0).getStatusCeo()).isEqualTo(20);
        }

        @Test
        @DisplayName("missionData가 없으면 statusCeo는 기본값 20이다")
        void defaultStatusCeoWhenNoMissionData() {
            ClassInfoProjection proj = mockProjection(1, 100, 10);
            given(gameRepository.findClassList(1L, 10)).willReturn(List.of(proj));
            given(missionDataRepository.findByMissionIdIn(List.of(100))).willReturn(Collections.emptyList());

            List<ClassInfoResponse> result = teachReader.getTeachIndex(1L);

            assertThat(result.get(0).getStatusCeo()).isEqualTo(20);
        }

        @Test
        @DisplayName("빈 수업 목록이면 빈 리스트를 반환한다")
        void emptyClassList() {
            given(gameRepository.findClassList(1L, 10)).willReturn(Collections.emptyList());

            List<ClassInfoResponse> result = teachReader.getTeachIndex(1L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getTeachList - 수업 목록 상태별 분류")
    class GetTeachList {

        @Test
        @DisplayName("status별로 올바르게 분류한다")
        void groupsByStatus() {
            given(gameRepository.findClassListByUserId(1L)).willReturn(List.of(
                    mockProjection(1, null, 10),
                    mockProjection(2, null, 1),
                    mockProjection(3, null, 100),
                    mockProjection(4, null, 0)
            ));

            TeachListResponse result = teachReader.getTeachList(1L);

            assertThat(result.getInProgress()).hasSize(1);
            assertThat(result.getSetting()).hasSize(1);
            assertThat(result.getCompleted()).hasSize(1);
            assertThat(result.getEtc()).hasSize(1);
        }

        @Test
        @DisplayName("status가 null인 수업은 어떤 그룹에도 포함되지 않는다")
        void excludesNullStatus() {
            given(gameRepository.findClassListByUserId(1L)).willReturn(List.of(
                    mockProjection(1, null, null)
            ));

            TeachListResponse result = teachReader.getTeachList(1L);

            assertThat(result.getInProgress()).isEmpty();
            assertThat(result.getSetting()).isEmpty();
            assertThat(result.getCompleted()).isEmpty();
            assertThat(result.getEtc()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getTeachDetail - 수업 상세 조회")
    class GetTeachDetail {

        @Test
        @DisplayName("존재하지 않는 gameId이면 NotFoundException 발생")
        void throwsWhenGameNotFound() {
            given(tbGameRepository.findById(99)).willReturn(Optional.empty());

            assertThatThrownBy(() -> teachReader.getTeachDetail(99))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getTeachDetail2 - gameId null 분기")
    class GetTeachDetail2 {

        @Test
        @DisplayName("gameId가 null이고 진행중인 수업도 없으면 NotFoundException 발생")
        void throwsWhenNoGameIdAndNoClasses() {
            given(gameRepository.findClassList(1L, 10)).willReturn(Collections.emptyList());

            assertThatThrownBy(() -> teachReader.getTeachDetail2(1L, null))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getStudentList - 학생 목록 조회")
    class GetStudentList {

        @Test
        @DisplayName("존재하지 않는 gameId이면 NotFoundException 발생")
        void throwsWhenGameNotFound() {
            given(tbGameRepository.findById(99)).willReturn(Optional.empty());

            assertThatThrownBy(() -> teachReader.getStudentList(99))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    private ClassInfoProjection mockProjection(Integer gameId, Integer missionId, Integer status) {
        return new ClassInfoProjection() {
            public Integer getGameId() { return gameId; }
            public String getName() { return "수업" + gameId; }
            public Integer getNumTeam() { return 3; }
            public Integer getTotalDd() { return 16; }
            public Integer getMissionId() { return missionId; }
            public Integer getSequence() { return 1; }
            public String getSubject() { return null; }
            public String getSummary() { return null; }
            public java.time.LocalDateTime getStartdate() { return null; }
            public java.time.LocalDateTime getEnddate() { return null; }
            public Integer getDdYear() { return null; }
            public Integer getDdTerm() { return null; }
            public String getImageUrl() { return null; }
            public Integer getStatus() { return status; }
        };
    }
}
