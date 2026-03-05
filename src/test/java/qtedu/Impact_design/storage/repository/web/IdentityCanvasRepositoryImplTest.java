package qtedu.Impact_design.storage.repository.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.storage.jpaentity.IdentityCanvas;
import qtedu.Impact_design.storage.jparepository.web.IdentityCanvasJpaRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class IdentityCanvasRepositoryImplTest {

    @InjectMocks
    private IdentityCanvasRepositoryImpl identityCanvasRepository;

    @Mock
    private IdentityCanvasJpaRepository identityCanvasJpaRepository;

    private IdentityCanvas createEntity(Long id, Long userId) {
        return IdentityCanvas.builder()
                .identityId(id).userId(userId)
                .mission("미션").vision("비전").value("가치")
                .macro("매크로").tech("기술").customer("고객")
                .competitor("경쟁").capability("역량")
                .culture("문화").structure("구조").etc("기타")
                .newMission("새미션").newVision("새비전").newValue("새가치")
                .build();
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("존재하면 Model로 변환하여 반환한다")
        void returnsModel() {
            IdentityCanvas entity = createEntity(1L, 10L);
            given(identityCanvasJpaRepository.findByUserId(10L)).willReturn(Optional.of(entity));

            Optional<IdentityCanvasModel> result = identityCanvasRepository.findByUserId(10L);

            assertThat(result).isPresent();
            assertThat(result.get().getIdentityId()).isEqualTo(1L);
            assertThat(result.get().getMission()).isEqualTo("미션");
            assertThat(result.get().getNewVision()).isEqualTo("새비전");
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional을 반환한다")
        void returnsEmpty() {
            given(identityCanvasJpaRepository.findByUserId(999L)).willReturn(Optional.empty());

            assertThat(identityCanvasRepository.findByUserId(999L)).isEmpty();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("ID가 없으면 새 Entity를 생성하여 저장한다")
        void createsNewEntity() {
            IdentityCanvasModel model = IdentityCanvasModel.builder()
                    .userId(10L).mission("미션").vision("비전").value("가치")
                    .build();
            IdentityCanvas savedEntity = createEntity(1L, 10L);
            given(identityCanvasJpaRepository.save(any(IdentityCanvas.class))).willReturn(savedEntity);

            IdentityCanvasModel result = identityCanvasRepository.save(model);

            assertThat(result.getIdentityId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("ID가 있으면 기존 Entity를 찾아 업데이트한다")
        void updatesExistingEntity() {
            IdentityCanvasModel model = IdentityCanvasModel.builder()
                    .identityId(1L).userId(10L).mission("수정미션").vision("수정비전").value("수정가치")
                    .build();
            IdentityCanvas existingEntity = createEntity(1L, 10L);
            given(identityCanvasJpaRepository.findById(1L)).willReturn(Optional.of(existingEntity));
            given(identityCanvasJpaRepository.save(existingEntity)).willReturn(existingEntity);

            IdentityCanvasModel result = identityCanvasRepository.save(model);

            assertThat(result.getIdentityId()).isEqualTo(1L);
            then(identityCanvasJpaRepository).should().findById(1L);
        }

        @Test
        @DisplayName("ID가 있지만 Entity가 없으면 예외가 발생한다")
        void throwsWhenEntityNotFound() {
            IdentityCanvasModel model = IdentityCanvasModel.builder()
                    .identityId(999L).userId(10L).mission("미션")
                    .build();
            given(identityCanvasJpaRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> identityCanvasRepository.save(model))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("findByUserIdIn - 여러 유저의 캔버스를 Model로 변환한다")
    void findByUserIdIn() {
        List<IdentityCanvas> entities = List.of(
                createEntity(1L, 10L),
                createEntity(2L, 20L)
        );
        given(identityCanvasJpaRepository.findByUserIdIn(List.of(10L, 20L))).willReturn(entities);

        List<IdentityCanvasModel> result = identityCanvasRepository.findByUserIdIn(List.of(10L, 20L));

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("existsSubmittedByUserId - JPA에 위임한다")
    void existsSubmittedByUserId() {
        given(identityCanvasJpaRepository.existsByUserIdAndSubmittedTrue(10L)).willReturn(true);

        assertThat(identityCanvasRepository.existsSubmittedByUserId(10L)).isTrue();
    }

    @Test
    @DisplayName("submitByUserId - 해당 유저의 캔버스를 제출 처리한다")
    void submitByUserId() {
        IdentityCanvas entity = createEntity(1L, 10L);
        given(identityCanvasJpaRepository.findByUserId(10L)).willReturn(Optional.of(entity));

        identityCanvasRepository.submitByUserId(10L);

        then(identityCanvasJpaRepository).should().save(entity);
    }
}
