package qtedu.Impact_design.storage.repository.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.storage.jpaentity.teach.TbGame;
import qtedu.Impact_design.storage.jparepository.teach.TbGameJpaRepository;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TbGameRepositoryImplTest {

    @InjectMocks
    private TbGameRepositoryImpl tbGameRepository;

    @Mock
    private TbGameJpaRepository tbGameJpaRepository;

    private TbGame createEntity(Integer gameId, String name) {
        return TbGame.builder()
                .gameId(gameId).name(name).code("ABC123")
                .numTeam(3).status(1).totalDd(30).isDoing(1)
                .step("step1").imageUrl("images/class1.png")
                .build();
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("존재하면 Model로 변환하여 반환한다")
        void returnsModel() {
            TbGame entity = createEntity(1, "수업1");
            given(tbGameJpaRepository.findById(1)).willReturn(Optional.of(entity));

            Optional<TbGameModel> result = tbGameRepository.findById(1);

            assertThat(result).isPresent();
            assertThat(result.get().getGameId()).isEqualTo(1);
            assertThat(result.get().getName()).isEqualTo("수업1");
            assertThat(result.get().getCode()).isEqualTo("ABC123");
            assertThat(result.get().getNumTeam()).isEqualTo(3);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional을 반환한다")
        void returnsEmpty() {
            given(tbGameJpaRepository.findById(999)).willReturn(Optional.empty());

            assertThat(tbGameRepository.findById(999)).isEmpty();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("Model을 Entity로 변환하여 저장하고 Model로 반환한다")
        void savesAndConverts() {
            TbGameModel model = TbGameModel.builder()
                    .name("새수업").code("NEW").numTeam(0).status(0).totalDd(30).isDoing(1).build();
            TbGame savedEntity = createEntity(10, "새수업");
            given(tbGameJpaRepository.save(any(TbGame.class))).willReturn(savedEntity);

            TbGameModel result = tbGameRepository.save(model);

            assertThat(result.getGameId()).isEqualTo(10);
            assertThat(result.getName()).isEqualTo("새수업");
        }
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("유저가 속한 게임을 Model로 반환한다")
        void returnsModel() {
            TbGame entity = createEntity(1, "수업1");
            given(tbGameJpaRepository.findByUserId(10L)).willReturn(Optional.of(entity));

            Optional<TbGameModel> result = tbGameRepository.findByUserId(10L);

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("수업1");
        }
    }

    @Nested
    @DisplayName("findStepByUserId")
    class FindStepByUserId {

        @Test
        @DisplayName("유저가 속한 게임의 step을 반환한다")
        void returnsStep() {
            TbGame entity = createEntity(1, "수업1");
            given(tbGameJpaRepository.findByUserId(10L)).willReturn(Optional.of(entity));

            String step = tbGameRepository.findStepByUserId(10L);

            assertThat(step).isEqualTo("step1");
        }

        @Test
        @DisplayName("게임이 없으면 빈 문자열을 반환한다")
        void returnsEmptyStringWhenNotFound() {
            given(tbGameJpaRepository.findByUserId(999L)).willReturn(Optional.empty());

            String step = tbGameRepository.findStepByUserId(999L);

            assertThat(step).isEmpty();
        }
    }

    @Nested
    @DisplayName("findGameNameByUserId")
    class FindGameNameByUserId {

        @Test
        @DisplayName("유저가 속한 게임의 이름을 반환한다")
        void returnsName() {
            TbGame entity = createEntity(1, "임팩트디자인");
            given(tbGameJpaRepository.findByUserId(10L)).willReturn(Optional.of(entity));

            String name = tbGameRepository.findGameNameByUserId(10L);

            assertThat(name).isEqualTo("임팩트디자인");
        }

        @Test
        @DisplayName("게임이 없으면 null을 반환한다")
        void returnsNullWhenNotFound() {
            given(tbGameJpaRepository.findByUserId(999L)).willReturn(Optional.empty());

            assertThat(tbGameRepository.findGameNameByUserId(999L)).isNull();
        }
    }

    @Nested
    @DisplayName("findGameImageUrlByUserId")
    class FindGameImageUrlByUserId {

        @Test
        @DisplayName("이미지가 있으면 baseUrl + path를 반환한다")
        void returnsResolvedUrl() throws Exception {
            setFileBaseUrl("https://cdn.example.com");
            TbGame entity = createEntity(1, "수업1");
            given(tbGameJpaRepository.findByUserId(10L)).willReturn(Optional.of(entity));

            String imageUrl = tbGameRepository.findGameImageUrlByUserId(10L);

            assertThat(imageUrl).isEqualTo("https://cdn.example.com/images/class1.png");
        }

        @Test
        @DisplayName("이미지가 null이면 null을 반환한다")
        void returnsNullWhenNoImage() throws Exception {
            setFileBaseUrl("https://cdn.example.com");
            TbGame entity = TbGame.builder()
                    .gameId(1).name("수업1").status(1).totalDd(30).isDoing(1).build();
            given(tbGameJpaRepository.findByUserId(10L)).willReturn(Optional.of(entity));

            String imageUrl = tbGameRepository.findGameImageUrlByUserId(10L);

            assertThat(imageUrl).isNull();
        }
    }

    @Test
    @DisplayName("incrementNumTeam - JPA에 위임한다")
    void incrementNumTeam() {
        tbGameRepository.incrementNumTeam(1);

        then(tbGameJpaRepository).should().incrementNumTeam(1);
    }

    @Test
    @DisplayName("decrementNumTeam - JPA에 위임한다")
    void decrementNumTeam() {
        tbGameRepository.decrementNumTeam(1);

        then(tbGameJpaRepository).should().decrementNumTeam(1);
    }

    @Test
    @DisplayName("updateImageUrl - JPA에 위임한다")
    void updateImageUrl() {
        tbGameRepository.updateImageUrl(1, "new/path.png");

        then(tbGameJpaRepository).should().updateImageUrl(1, "new/path.png");
    }

    @Test
    @DisplayName("findByTeamId - teamId로 게임을 조회한다")
    void findByTeamId() {
        TbGame entity = createEntity(1, "수업1");
        given(tbGameJpaRepository.findByTeamId(10)).willReturn(Optional.of(entity));

        Optional<TbGameModel> result = tbGameRepository.findByTeamId(10);

        assertThat(result).isPresent();
    }

    private void setFileBaseUrl(String url) throws Exception {
        Field field = TbGameRepositoryImpl.class.getDeclaredField("fileBaseUrl");
        field.setAccessible(true);
        field.set(tbGameRepository, url);
    }
}
