package qtedu.Impact_design.storage.repository.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.storage.jpaentity.teach.TeamUser;
import qtedu.Impact_design.storage.jparepository.teach.TeamUserJpaRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TeamUserRepositoryImplTest {

    @InjectMocks
    private TeamUserRepositoryImpl teamUserRepository;

    @Mock
    private TeamUserJpaRepository teamUserJpaRepository;

    private TeamUser createEntity(Integer teamUserId, Long userId, Integer teamId) {
        return TeamUser.builder()
                .teamUserId(teamUserId).userId(userId).teamId(teamId)
                .userlevel(0).isDoing(1)
                .build();
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("Model을 Entity로 변환하여 저장하고 Model로 반환한다")
        void savesAndConverts() {
            TeamUserModel model = TeamUserModel.createStudent(1L, 10);
            TeamUser savedEntity = createEntity(100, 1L, 10);
            given(teamUserJpaRepository.save(any(TeamUser.class))).willReturn(savedEntity);

            TeamUserModel result = teamUserRepository.save(model);

            assertThat(result.getTeamUserId()).isEqualTo(100);
            assertThat(result.getUserId()).isEqualTo(1L);
            assertThat(result.getTeamId()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("findByTeamId")
    class FindByTeamId {

        @Test
        @DisplayName("팀 멤버를 Model 리스트로 변환하여 반환한다")
        void returnsModelList() {
            List<TeamUser> entities = List.of(
                    createEntity(1, 10L, 5),
                    createEntity(2, 20L, 5)
            );
            given(teamUserJpaRepository.findByTeamId(5)).willReturn(entities);

            List<TeamUserModel> result = teamUserRepository.findByTeamId(5);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getUserId()).isEqualTo(10L);
            assertThat(result.get(1).getUserId()).isEqualTo(20L);
        }
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("존재하면 Model로 변환하여 반환한다")
        void returnsModelWhenFound() {
            TeamUser entity = createEntity(1, 10L, 5);
            given(teamUserJpaRepository.findByUserId(10L)).willReturn(Optional.of(entity));

            Optional<TeamUserModel> result = teamUserRepository.findByUserId(10L);

            assertThat(result).isPresent();
            assertThat(result.get().getTeamId()).isEqualTo(5);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional을 반환한다")
        void returnsEmptyWhenNotFound() {
            given(teamUserJpaRepository.findByUserId(999L)).willReturn(Optional.empty());

            assertThat(teamUserRepository.findByUserId(999L)).isEmpty();
        }
    }

    @Test
    @DisplayName("countByTeamId - JPA에 위임한다")
    void countByTeamId() {
        given(teamUserJpaRepository.countByTeamId(5)).willReturn(3);

        assertThat(teamUserRepository.countByTeamId(5)).isEqualTo(3);
    }

    @Test
    @DisplayName("deleteByUserId - JPA에 위임한다")
    void deleteByUserId() {
        teamUserRepository.deleteByUserId(1L);

        then(teamUserJpaRepository).should().deleteByUserId(1L);
    }

    @Test
    @DisplayName("updateTeamId - JPA에 위임한다")
    void updateTeamId() {
        teamUserRepository.updateTeamId(1L, 10);

        then(teamUserJpaRepository).should().updateTeamIdByUserId(1L, 10);
    }

    @Test
    @DisplayName("findTeamNameByUserId - JPA에 위임한다")
    void findTeamNameByUserId() {
        given(teamUserJpaRepository.findTeamNameByUserId(1L)).willReturn(Optional.of("팀1"));

        assertThat(teamUserRepository.findTeamNameByUserId(1L)).contains("팀1");
    }

    @Test
    @DisplayName("findByTeamIdIn - 여러 팀의 멤버를 조회한다")
    void findByTeamIdIn() {
        List<TeamUser> entities = List.of(
                createEntity(1, 10L, 5),
                createEntity(2, 20L, 6)
        );
        given(teamUserJpaRepository.findByTeamIdIn(List.of(5, 6))).willReturn(entities);

        List<TeamUserModel> result = teamUserRepository.findByTeamIdIn(List.of(5, 6));

        assertThat(result).hasSize(2);
    }
}
