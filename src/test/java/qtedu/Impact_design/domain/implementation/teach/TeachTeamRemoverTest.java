package qtedu.Impact_design.domain.implementation.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class TeachTeamRemoverTest {

    @Mock private TbGameRepository tbGameRepository;
    @Mock private TbTeamRepository tbTeamRepository;
    @Mock private TeamUserRepository teamUserRepository;
    @Mock private UserinfoRepository userinfoRepository;

    @InjectMocks
    private TeachTeamRemover teachTeamRemover;

    @Nested
    @DisplayName("deleteTeam - 팀 삭제")
    class DeleteTeam {

        @Test
        @DisplayName("팀 status를 -1로 변경하고 게임의 numTeam을 감소시킨다")
        void setsStatusToMinusOneAndDecrementsNumTeam() {
            Integer teamId = 1;
            Integer gameId = 1;
            TbTeamModel team = TbTeamModel.builder().teamId(1).name("알파팀").sequence(1).status(0).code("ABC").isDoing(1).numUser(4).build();
            given(tbTeamRepository.findByTeamId(teamId)).willReturn(Optional.of(team));

            teachTeamRemover.deleteTeam(teamId, gameId);

            ArgumentCaptor<TbTeamModel> captor = ArgumentCaptor.forClass(TbTeamModel.class);
            then(tbTeamRepository).should().save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(-1);
            then(tbGameRepository).should().decrementNumTeam(gameId);
        }

        @Test
        @DisplayName("존재하지 않는 팀이면 NotFoundException 발생")
        void throwsWhenTeamNotFound() {
            given(tbTeamRepository.findByTeamId(99)).willReturn(Optional.empty());

            assertThatThrownBy(() -> teachTeamRemover.deleteTeam(99, 1))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteTeamMembers - 팀 멤버 삭제")
    class DeleteTeamMembers {

        @Test
        @DisplayName("팀별 멤버 수를 감소시키고 teamUser를 삭제한다")
        void decrementsNumUserAndDeletesTeamUser() {
            List<Long> userIds = List.of(5L, 6L);

            given(userinfoRepository.findAllByUserIds(userIds)).willReturn(List.of(
                    UserinfoModel.builder().userId(5L).writer("0").build(),
                    UserinfoModel.builder().userId(6L).writer("0").build()
            ));
            given(teamUserRepository.findByUserIdIn(userIds)).willReturn(List.of(
                    TeamUserModel.builder().teamId(1).userId(5L).build(),
                    TeamUserModel.builder().teamId(1).userId(6L).build()
            ));

            teachTeamRemover.deleteTeamMembers(userIds);

            then(tbTeamRepository).should().decrementNumUserBy(1, 2);
            then(teamUserRepository).should().deleteByUserIdIn(userIds);
        }

        @Test
        @DisplayName("writer인 유저를 삭제하면 남은 멤버에게 writer를 이전한다")
        void transfersWriterToRemainingMember() {
            List<Long> userIds = List.of(6L);

            given(userinfoRepository.findAllByUserIds(userIds)).willReturn(List.of(
                    UserinfoModel.builder().userId(6L).writer("1").build()
            ));
            given(teamUserRepository.findByUserIdIn(userIds)).willReturn(List.of(
                    TeamUserModel.builder().teamId(1).userId(6L).build()
            ));
            given(teamUserRepository.findByTeamIdIn(List.of(1))).willReturn(List.of(
                    TeamUserModel.builder().teamId(1).userId(5L).build(),
                    TeamUserModel.builder().teamId(1).userId(6L).build()
            ));

            teachTeamRemover.deleteTeamMembers(userIds);

            then(userinfoRepository).should().setWriter(5L);
            then(userinfoRepository).should().clearWriterByUserIds(List.of(6L));
        }

        @Test
        @DisplayName("writer가 아닌 유저 삭제 시 writer 이전을 하지 않는다")
        void doesNotTransferWriterForNonWriter() {
            List<Long> userIds = List.of(5L);

            given(userinfoRepository.findAllByUserIds(userIds)).willReturn(List.of(
                    UserinfoModel.builder().userId(5L).writer("0").build()
            ));
            given(teamUserRepository.findByUserIdIn(userIds)).willReturn(List.of(
                    TeamUserModel.builder().teamId(1).userId(5L).build()
            ));

            teachTeamRemover.deleteTeamMembers(userIds);

            then(userinfoRepository).should(never()).setWriter(any(Long.class));
        }
    }
}
