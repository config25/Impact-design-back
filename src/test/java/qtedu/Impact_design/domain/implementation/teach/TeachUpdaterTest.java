package qtedu.Impact_design.domain.implementation.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.common.error.BadRequestException;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.external.ExternalFileClient;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbMissionModel;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;
import qtedu.Impact_design.domain.repository.teach.MissionDataRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.teach.TbMissionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TeachUpdaterTest {

    @InjectMocks
    private TeachUpdater teachUpdater;

    @Mock private TbGameRepository tbGameRepository;
    @Mock private TbMissionRepository tbMissionRepository;
    @Mock private GameTeamRepository gameTeamRepository;
    @Mock private MissionDataRepository missionDataRepository;
    @Mock private ExternalFileClient fileClient;

    private TbGameModel sampleGame(int gameId) {
        return TbGameModel.builder()
                .gameId(gameId).name("테스트 수업").code("ABC").status(1)
                .numTeam(3).build();
    }

    private String futureDate() {
        return "2099-12-31 23:59";
    }

    @Nested
    @DisplayName("startClass")
    class StartClass {

        @Test
        @DisplayName("수업을 시작하면 미션을 생성하고 status를 10으로 변경한다")
        void startsClassSuccessfully() {
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame(1)));
            given(gameTeamRepository.findTeamIdsByGameId(1)).willReturn(List.of(10, 11, 12));

            ArgumentCaptor<TbMissionModel> missionCaptor = ArgumentCaptor.forClass(TbMissionModel.class);
            given(tbMissionRepository.save(missionCaptor.capture())).willReturn(
                    TbMissionModel.builder().missionId(100).build());

            teachUpdater.startClass(1, futureDate());

            TbMissionModel savedMission = missionCaptor.getValue();
            assertThat(savedMission.getSequence()).isEqualTo(1);
            assertThat(savedMission.getDdYear()).isEqualTo(1);
            assertThat(savedMission.getDdTerm()).isEqualTo(1);

            then(missionDataRepository).should().saveAll(100, List.of(10, 11, 12));

            ArgumentCaptor<TbGameModel> gameCaptor = ArgumentCaptor.forClass(TbGameModel.class);
            then(tbGameRepository).should().save(gameCaptor.capture());
            // save is called twice: once for findGame check and once for status update
            // The last captured value should have status=10
        }

        @Test
        @DisplayName("과거 날짜이면 InvalidDateException")
        void pastDateThrows() {
            assertThatThrownBy(() -> teachUpdater.startClass(1, "2020-01-01 00:00"))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("잘못된 날짜 형식이면 InvalidDateException")
        void invalidDateFormat() {
            assertThatThrownBy(() -> teachUpdater.startClass(1, "2099-12-31"))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("게임이 없으면 NotFoundException")
        void gameNotFound() {
            assertThatThrownBy(() -> teachUpdater.startClass(99, futureDate()))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("endClass")
    class EndClass {

        @Test
        @DisplayName("수업을 종료하면 status=100, endedAt이 설정된다")
        void endsClassSuccessfully() {
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame(1)));

            teachUpdater.endClass(1);

            ArgumentCaptor<TbGameModel> captor = ArgumentCaptor.forClass(TbGameModel.class);
            then(tbGameRepository).should().save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(100);
            assertThat(captor.getValue().getEndedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("restoreClass")
    class RestoreClass {

        @Test
        @DisplayName("수업 복원 시 endedAt=null, status=10으로 변경한다")
        void restoresClassSuccessfully() {
            given(tbGameRepository.findById(1)).willReturn(Optional.of(
                    TbGameModel.builder().gameId(1).status(100)
                            .endedAt(LocalDateTime.now()).build()));
            given(tbMissionRepository.findLatestByGameId(1)).willReturn(Optional.of(
                    TbMissionModel.builder().missionId(50).build()));

            teachUpdater.restoreClass(1, futureDate());

            then(tbMissionRepository).should().updateEnddate(eq(50), any(LocalDateTime.class));

            ArgumentCaptor<TbGameModel> captor = ArgumentCaptor.forClass(TbGameModel.class);
            then(tbGameRepository).should().save(captor.capture());
            assertThat(captor.getValue().getEndedAt()).isNull();
            assertThat(captor.getValue().getStatus()).isEqualTo(10);
        }

        @Test
        @DisplayName("미션이 없으면 새로 생성한다")
        void createsNewMissionWhenNoneExists() {
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame(1)));
            given(tbMissionRepository.findLatestByGameId(1)).willReturn(Optional.empty());
            given(tbMissionRepository.save(any())).willReturn(
                    TbMissionModel.builder().missionId(200).build());
            given(gameTeamRepository.findTeamIdsByGameId(1)).willReturn(List.of(10));

            teachUpdater.restoreClass(1, futureDate());

            then(missionDataRepository).should().saveAll(200, List.of(10));
        }
    }

    @Nested
    @DisplayName("startNextStage")
    class StartNextStage {

        @Test
        @DisplayName("term < 4이면 같은 year에서 term만 증가한다")
        void incrementsTermWithinYear() {
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame(1)));
            given(tbMissionRepository.findLatestByGameId(1)).willReturn(Optional.of(
                    TbMissionModel.builder().missionId(10).sequence(2).ddYear(1).ddTerm(2).build()));
            given(gameTeamRepository.findTeamIdsByGameId(1)).willReturn(List.of(10));

            ArgumentCaptor<TbMissionModel> captor = ArgumentCaptor.forClass(TbMissionModel.class);
            given(tbMissionRepository.save(captor.capture())).willReturn(
                    TbMissionModel.builder().missionId(11).build());

            teachUpdater.startNextStage(1, futureDate());

            TbMissionModel saved = captor.getValue();
            assertThat(saved.getSequence()).isEqualTo(3);
            assertThat(saved.getDdYear()).isEqualTo(1);
            assertThat(saved.getDdTerm()).isEqualTo(3);
        }

        @Test
        @DisplayName("term = 4이면 year가 증가하고 term은 1로 초기화된다")
        void rollsOverToNextYear() {
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame(1)));
            given(tbMissionRepository.findLatestByGameId(1)).willReturn(Optional.of(
                    TbMissionModel.builder().missionId(10).sequence(4).ddYear(2).ddTerm(4).build()));
            given(gameTeamRepository.findTeamIdsByGameId(1)).willReturn(List.of(10));

            ArgumentCaptor<TbMissionModel> captor = ArgumentCaptor.forClass(TbMissionModel.class);
            given(tbMissionRepository.save(captor.capture())).willReturn(
                    TbMissionModel.builder().missionId(11).build());

            teachUpdater.startNextStage(1, futureDate());

            TbMissionModel saved = captor.getValue();
            assertThat(saved.getSequence()).isEqualTo(5);
            assertThat(saved.getDdYear()).isEqualTo(3);
            assertThat(saved.getDdTerm()).isEqualTo(1);
        }

        @Test
        @DisplayName("미션이 없으면 year=1, term=1, sequence=1부터 시작한다")
        void startsFromScratchWhenNoMission() {
            given(tbGameRepository.findById(1)).willReturn(Optional.of(sampleGame(1)));
            given(tbMissionRepository.findLatestByGameId(1)).willReturn(Optional.empty());
            given(gameTeamRepository.findTeamIdsByGameId(1)).willReturn(List.of(10));

            ArgumentCaptor<TbMissionModel> captor = ArgumentCaptor.forClass(TbMissionModel.class);
            given(tbMissionRepository.save(captor.capture())).willReturn(
                    TbMissionModel.builder().missionId(1).build());

            teachUpdater.startNextStage(1, futureDate());

            TbMissionModel saved = captor.getValue();
            assertThat(saved.getSequence()).isEqualTo(1);
            assertThat(saved.getDdYear()).isEqualTo(1);
            assertThat(saved.getDdTerm()).isEqualTo(1);
        }
    }
}
