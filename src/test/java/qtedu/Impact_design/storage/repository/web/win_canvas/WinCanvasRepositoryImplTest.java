package qtedu.Impact_design.storage.repository.web.win_canvas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.storage.jpaentity.win_canvas.WinCanvas;
import qtedu.Impact_design.storage.jparepository.web.win_canvas.WinCanvasJpaRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class WinCanvasRepositoryImplTest {

    @InjectMocks
    private WinCanvasRepositoryImpl winCanvasRepository;

    @Mock
    private WinCanvasJpaRepository winCanvasJpaRepository;

    private WinCanvas createEntity(Long canvasId, Long userId, CanvasType type) {
        return WinCanvas.builder()
                .canvasId(canvasId).userId(userId).canvasType(type)
                .strategicGoal("전략목표").taskName("태스크").taskDescription("설명")
                .crisisSignal("위기신호").painTouchPoint("터치포인트")
                .build();
    }

    @Nested
    @DisplayName("findByUserIdAndCanvasType")
    class FindByUserIdAndCanvasType {

        @Test
        @DisplayName("존재하면 Model로 변환하여 반환한다")
        void returnsModel() {
            WinCanvas entity = createEntity(1L, 10L, CanvasType.BUILD);
            given(winCanvasJpaRepository.findByUserIdAndCanvasType(10L, CanvasType.BUILD))
                    .willReturn(Optional.of(entity));

            Optional<WinCanvasModel> result = winCanvasRepository.findByUserIdAndCanvasType(10L, CanvasType.BUILD);

            assertThat(result).isPresent();
            assertThat(result.get().getCanvasId()).isEqualTo(1L);
            assertThat(result.get().getTaskName()).isEqualTo("태스크");
            assertThat(result.get().getCanvasType()).isEqualTo(CanvasType.BUILD);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional을 반환한다")
        void returnsEmpty() {
            given(winCanvasJpaRepository.findByUserIdAndCanvasType(999L, CanvasType.QUICK))
                    .willReturn(Optional.empty());

            assertThat(winCanvasRepository.findByUserIdAndCanvasType(999L, CanvasType.QUICK)).isEmpty();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("ID가 없으면 새 Entity를 생성한다")
        void createsNew() {
            WinCanvasModel model = WinCanvasModel.builder()
                    .userId(10L).canvasType(CanvasType.BUILD)
                    .strategicGoal("목표").taskName("태스크").taskDescription("설명")
                    .build();
            WinCanvas savedEntity = createEntity(1L, 10L, CanvasType.BUILD);
            given(winCanvasJpaRepository.findById(0L)).willReturn(Optional.empty());
            given(winCanvasJpaRepository.save(any(WinCanvas.class))).willReturn(savedEntity);

            WinCanvasModel result = winCanvasRepository.save(model);

            assertThat(result.getCanvasId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("ID가 있으면 기존 Entity를 업데이트한다")
        void updatesExisting() {
            WinCanvasModel model = WinCanvasModel.builder()
                    .canvasId(1L).userId(10L).canvasType(CanvasType.BUILD)
                    .strategicGoal("수정목표").taskName("수정태스크").taskDescription("수정설명")
                    .build();
            WinCanvas existingEntity = createEntity(1L, 10L, CanvasType.BUILD);
            given(winCanvasJpaRepository.findById(1L)).willReturn(Optional.of(existingEntity));
            given(winCanvasJpaRepository.save(existingEntity)).willReturn(existingEntity);

            WinCanvasModel result = winCanvasRepository.save(model);

            assertThat(result.getCanvasId()).isEqualTo(1L);
        }
    }

    @Test
    @DisplayName("findByUserIdInAndCanvasType - 여러 유저의 캔버스를 조회한다")
    void findByUserIdInAndCanvasType() {
        List<WinCanvas> entities = List.of(
                createEntity(1L, 10L, CanvasType.QUICK),
                createEntity(2L, 20L, CanvasType.QUICK)
        );
        given(winCanvasJpaRepository.findByUserIdInAndCanvasType(List.of(10L, 20L), CanvasType.QUICK))
                .willReturn(entities);

        List<WinCanvasModel> result = winCanvasRepository.findByUserIdInAndCanvasType(List.of(10L, 20L), CanvasType.QUICK);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("existsSubmittedByUserIdAndCanvasType - JPA에 위임한다")
    void existsSubmitted() {
        given(winCanvasJpaRepository.existsByUserIdAndCanvasTypeAndSubmittedTrue(10L, CanvasType.BUILD))
                .willReturn(true);

        assertThat(winCanvasRepository.existsSubmittedByUserIdAndCanvasType(10L, CanvasType.BUILD)).isTrue();
    }

    @Test
    @DisplayName("submitByUserIdAndCanvasType - 캔버스를 제출 처리한다")
    void submit() {
        WinCanvas entity = createEntity(1L, 10L, CanvasType.BUILD);
        given(winCanvasJpaRepository.findByUserIdAndCanvasType(10L, CanvasType.BUILD))
                .willReturn(Optional.of(entity));

        winCanvasRepository.submitByUserIdAndCanvasType(10L, CanvasType.BUILD);

        then(winCanvasJpaRepository).should().save(entity);
    }
}
