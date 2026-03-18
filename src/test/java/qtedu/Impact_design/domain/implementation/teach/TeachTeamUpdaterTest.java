package qtedu.Impact_design.domain.implementation.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TeachTeamUpdaterTest {

    @Mock private TbGameRepository tbGameRepository;
    @Mock private TbTeamRepository tbTeamRepository;
    @Mock private TeamUserRepository teamUserRepository;
    @Mock private UserinfoRepository userinfoRepository;
    @Mock private FLetterOfIntentRepository fLetterOfIntentRepository;

    @InjectMocks
    private TeachTeamUpdater teachTeamUpdater;

    @Nested
    @DisplayName("updateTeamMember - 팀 멤버 이동")
    class UpdateTeamMember {

        @Test
        @DisplayName("기존 팀 numUser 감소, 새 팀 numUser 증가, teamId 업데이트")
        void movesUserBetweenTeams() {
            Long userId = 5L;
            Integer newTeamId = 2;

            given(userinfoRepository.findByUserId(userId)).willReturn(Optional.of(
                    UserinfoModel.builder().userId(5L).writer("0").build()));
            given(teamUserRepository.findByUserId(userId)).willReturn(Optional.of(
                    TeamUserModel.builder().teamId(1).userId(5L).build()));

            teachTeamUpdater.updateTeamMember(userId, newTeamId);

            then(tbTeamRepository).should().decrementNumUser(1);
            then(tbTeamRepository).should().incrementNumUser(2);
            then(teamUserRepository).should().updateTeamId(userId, newTeamId);
            then(fLetterOfIntentRepository).should().updateInvestmentTargetByCanvasOwner(userId, "2");
        }

        @Test
        @DisplayName("writer인 유저를 이동하려면 ConflictException 발생")
        void throwsWhenMovingWriter() {
            Long userId = 6L;
            given(userinfoRepository.findByUserId(userId)).willReturn(Optional.of(
                    UserinfoModel.builder().userId(6L).writer("1").build()));

            assertThatThrownBy(() -> teachTeamUpdater.updateTeamMember(userId, 2))
                    .isInstanceOf(ConflictException.class);
        }

        @Test
        @DisplayName("기존 팀이 없는 유저(신규)면 기존 팀 감소 없이 새 팀만 증가")
        void incrementsOnlyNewTeamWhenNoExistingTeam() {
            Long userId = 100L;
            Integer newTeamId = 3;

            given(userinfoRepository.findByUserId(userId)).willReturn(Optional.of(
                    UserinfoModel.builder().userId(100L).writer("0").build()));
            given(teamUserRepository.findByUserId(userId)).willReturn(Optional.empty());

            teachTeamUpdater.updateTeamMember(userId, newTeamId);

            then(tbTeamRepository).should().incrementNumUser(3);
            then(teamUserRepository).should().updateTeamId(userId, newTeamId);
        }
    }

    @Nested
    @DisplayName("restoreTeam - 팀 복원")
    class RestoreTeam {

        @Test
        @DisplayName("삭제된 팀(status=-1)을 복원하고 numTeam을 증가시킨다")
        void restoresDeletedTeam() {
            Integer teamId = 1;
            Integer gameId = 1;
            given(tbTeamRepository.findByTeamId(teamId)).willReturn(Optional.of(
                    TbTeamModel.builder().teamId(1).name("알파팀").status(-1).build()));

            teachTeamUpdater.restoreTeam(teamId, gameId);

            then(tbTeamRepository).should().save(any(TbTeamModel.class));
            then(tbGameRepository).should().incrementNumTeam(gameId);
        }

        @Test
        @DisplayName("이미 활성 상태인 팀은 복원하지 않는다")
        void doesNotRestoreActiveTeam() {
            Integer teamId = 1;
            given(tbTeamRepository.findByTeamId(teamId)).willReturn(Optional.of(
                    TbTeamModel.builder().teamId(1).name("알파팀").status(1).build()));

            teachTeamUpdater.restoreTeam(teamId, 1);

            then(tbTeamRepository).should().findByTeamId(teamId);
            then(tbGameRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("존재하지 않는 팀이면 NotFoundException 발생")
        void throwsWhenTeamNotFound() {
            given(tbTeamRepository.findByTeamId(99)).willReturn(Optional.empty());

            assertThatThrownBy(() -> teachTeamUpdater.restoreTeam(99, 1))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("setTeamWriter - writer 변경")
    class SetTeamWriter {

        @Test
        @DisplayName("팀 내 기존 writer를 해제하고 새 writer를 설정한다")
        void clearsAndSetsWriter() {
            Integer teamId = 1;
            Long userId = 6L;

            given(teamUserRepository.findByTeamId(teamId)).willReturn(List.of(
                    TeamUserModel.builder().teamId(1).userId(5L).build(),
                    TeamUserModel.builder().teamId(1).userId(6L).build()
            ));

            teachTeamUpdater.setTeamWriter(teamId, userId);

            then(userinfoRepository).should().clearWriterByUserIds(List.of(5L, 6L));
            then(userinfoRepository).should().setWriter(6L);
        }

        @Test
        @DisplayName("팀에 속하지 않은 유저를 writer로 지정하면 NotFoundException 발생")
        void throwsWhenUserNotInTeam() {
            Integer teamId = 1;
            given(teamUserRepository.findByTeamId(teamId)).willReturn(List.of(
                    TeamUserModel.builder().teamId(1).userId(5L).build()
            ));

            assertThatThrownBy(() -> teachTeamUpdater.setTeamWriter(teamId, 99L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateTeamInfo - 팀 정보 수정")
    class UpdateTeamInfo {

        @Test
        @DisplayName("전달된 값만 업데이트하고 null인 필드는 기존 값 유지")
        void updatesOnlyProvidedFields() {
            Integer teamId = 1;
            given(tbTeamRepository.findByTeamId(teamId)).willReturn(Optional.of(
                    TbTeamModel.builder().teamId(1).name("알파팀").sequence(1).isDoing(1).aiPlay(0).build()));

            teachTeamUpdater.updateTeamInfo(teamId, "베타팀", null, null, null);

            then(tbTeamRepository).should().save(any(TbTeamModel.class));
        }

        @Test
        @DisplayName("존재하지 않는 팀이면 NotFoundException 발생")
        void throwsWhenTeamNotFound() {
            given(tbTeamRepository.findByTeamId(99)).willReturn(Optional.empty());

            assertThatThrownBy(() -> teachTeamUpdater.updateTeamInfo(99, "팀", null, null, null))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("saveStep - 게임 단계 저장")
    class SaveStep {

        @Test
        @DisplayName("게임의 step을 업데이트한다")
        void updatesGameStep() {
            Integer gameId = 1;
            given(tbGameRepository.findById(gameId)).willReturn(Optional.of(
                    TbGameModel.builder().gameId(1).name("수업1").status(10).isDoing(1).totalDd(16).build()));

            teachTeamUpdater.saveStep(gameId, "A-1,B-1");

            then(tbGameRepository).should().save(any(TbGameModel.class));
        }

        @Test
        @DisplayName("존재하지 않는 게임이면 NotFoundException 발생")
        void throwsWhenGameNotFound() {
            given(tbGameRepository.findById(99)).willReturn(Optional.empty());

            assertThatThrownBy(() -> teachTeamUpdater.saveStep(99, "A-1"))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
