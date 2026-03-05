package qtedu.Impact_design.storage.repository.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.storage.jpaentity.teach.TbTeam;
import qtedu.Impact_design.storage.jparepository.teach.TbTeamJpaRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TbTeamRepositoryImplTest {

    @InjectMocks
    private TbTeamRepositoryImpl tbTeamRepository;

    @Mock
    private TbTeamJpaRepository tbTeamJpaRepository;

    private TbTeam createEntity(Integer teamId, String name, Integer sequence) {
        return TbTeam.builder()
                .teamId(teamId).name(name).sequence(sequence)
                .status(0).code("ABC").isDoing(1).numUser(3)
                .build();
    }

    @Nested
    @DisplayName("findByCode")
    class FindByCode {

        @Test
        @DisplayName("코드로 팀 목록을 Model로 변환하여 반환한다")
        void returnsModelList() {
            List<TbTeam> entities = List.of(
                    createEntity(1, "팀1", 1),
                    createEntity(2, "팀2", 2)
            );
            given(tbTeamJpaRepository.findByCode("ABC")).willReturn(entities);

            List<TbTeamModel> result = tbTeamRepository.findByCode("ABC");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("팀1");
            assertThat(result.get(1).getSequence()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("findByTeamId")
    class FindByTeamId {

        @Test
        @DisplayName("존재하면 Model로 변환하여 반환한다")
        void returnsModel() {
            TbTeam entity = createEntity(1, "팀1", 1);
            given(tbTeamJpaRepository.findByTeamId(1)).willReturn(Optional.of(entity));

            Optional<TbTeamModel> result = tbTeamRepository.findByTeamId(1);

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("팀1");
            assertThat(result.get().getNumUser()).isEqualTo(3);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional을 반환한다")
        void returnsEmpty() {
            given(tbTeamJpaRepository.findByTeamId(999)).willReturn(Optional.empty());

            assertThat(tbTeamRepository.findByTeamId(999)).isEmpty();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("Model을 Entity로 변환하여 저장하고 Model로 반환한다")
        void savesAndConverts() {
            TbTeamModel model = TbTeamModel.builder()
                    .name("팀1").sequence(1).status(0).code("ABC").isDoing(1).build();
            TbTeam savedEntity = createEntity(10, "팀1", 1);
            given(tbTeamJpaRepository.save(any(TbTeam.class))).willReturn(savedEntity);

            TbTeamModel result = tbTeamRepository.save(model);

            assertThat(result.getTeamId()).isEqualTo(10);
            assertThat(result.getName()).isEqualTo("팀1");
        }
    }

    @Test
    @DisplayName("findMaxSequenceByGameId - JPA에 위임한다")
    void findMaxSequenceByGameId() {
        given(tbTeamJpaRepository.findMaxSequenceByGameId(1)).willReturn(3);

        assertThat(tbTeamRepository.findMaxSequenceByGameId(1)).isEqualTo(3);
    }

    @Test
    @DisplayName("incrementNumUser - JPA에 위임한다")
    void incrementNumUser() {
        tbTeamRepository.incrementNumUser(1);

        then(tbTeamJpaRepository).should().incrementNumUser(1);
    }

    @Test
    @DisplayName("decrementNumUser - JPA에 위임한다")
    void decrementNumUser() {
        tbTeamRepository.decrementNumUser(1);

        then(tbTeamJpaRepository).should().decrementNumUser(1);
    }

    @Test
    @DisplayName("findDeletedByGameId - 삭제된 팀을 Model로 반환한다")
    void findDeletedByGameId() {
        List<TbTeam> entities = List.of(createEntity(5, "삭제팀", 3));
        given(tbTeamJpaRepository.findDeletedTeamsByGameId(1)).willReturn(entities);

        List<TbTeamModel> result = tbTeamRepository.findDeletedByGameId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("삭제팀");
    }

    @Test
    @DisplayName("findByTeamIdIn - 여러 teamId로 조회한다")
    void findByTeamIdIn() {
        List<TbTeam> entities = List.of(
                createEntity(1, "팀1", 1),
                createEntity(2, "팀2", 2)
        );
        given(tbTeamJpaRepository.findByTeamIdIn(List.of(1, 2))).willReturn(entities);

        List<TbTeamModel> result = tbTeamRepository.findByTeamIdIn(List.of(1, 2));

        assertThat(result).hasSize(2);
    }
}
