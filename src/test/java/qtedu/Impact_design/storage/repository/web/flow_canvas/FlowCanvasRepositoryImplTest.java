package qtedu.Impact_design.storage.repository.web.flow_canvas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.storage.jpaentity.flow_canvas.FlowCanvas;
import qtedu.Impact_design.storage.jparepository.web.flow_canvas.FlowCanvasJpaRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FlowCanvasRepositoryImplTest {

    @InjectMocks
    private FlowCanvasRepositoryImpl flowCanvasRepository;

    @Mock
    private FlowCanvasJpaRepository flowCanvasJpaRepository;

    private FlowCanvas createEntity(Long goalId, Long userId) {
        return FlowCanvas.builder()
                .goalId(goalId).userId(userId)
                .goalTitle("목표1").goalDescription("설명1").orderNo(1)
                .build();
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("유저의 플로우캔버스를 Model 리스트로 반환한다")
        void returnsModelList() {
            List<FlowCanvas> entities = List.of(createEntity(1L, 10L), createEntity(2L, 10L));
            given(flowCanvasJpaRepository.findByUserIdOrderByOrderNoAsc(10L)).willReturn(entities);

            List<FlowCanvasModel> result = flowCanvasRepository.findByUserId(10L);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getGoalTitle()).isEqualTo("목표1");
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("ID가 없으면 새 Entity를 생성한다")
        void createsNew() {
            FlowCanvasModel model = FlowCanvasModel.builder()
                    .userId(10L).goalTitle("새목표").goalDescription("새설명").orderNo(1).build();
            FlowCanvas savedEntity = createEntity(1L, 10L);
            given(flowCanvasJpaRepository.save(any(FlowCanvas.class))).willReturn(savedEntity);

            FlowCanvasModel result = flowCanvasRepository.save(model);

            assertThat(result.getGoalId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("ID가 있으면 기존 Entity를 업데이트한다")
        void updatesExisting() {
            FlowCanvasModel model = FlowCanvasModel.builder()
                    .goalId(1L).userId(10L).goalTitle("수정목표").goalDescription("수정설명").orderNo(1).build();
            FlowCanvas existingEntity = createEntity(1L, 10L);
            given(flowCanvasJpaRepository.findById(1L)).willReturn(Optional.of(existingEntity));
            given(flowCanvasJpaRepository.save(existingEntity)).willReturn(existingEntity);

            FlowCanvasModel result = flowCanvasRepository.save(model);

            assertThat(result.getGoalId()).isEqualTo(1L);
        }
    }

    @Test
    @DisplayName("findByUserIdIn - 여러 유저의 캔버스를 조회한다")
    void findByUserIdIn() {
        List<FlowCanvas> entities = List.of(createEntity(1L, 10L), createEntity(2L, 20L));
        given(flowCanvasJpaRepository.findByUserIdIn(List.of(10L, 20L))).willReturn(entities);

        List<FlowCanvasModel> result = flowCanvasRepository.findByUserIdIn(List.of(10L, 20L));

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("existsSubmittedByUserId - JPA에 위임한다")
    void existsSubmittedByUserId() {
        given(flowCanvasJpaRepository.existsByUserIdAndSubmittedTrue(10L)).willReturn(true);

        assertThat(flowCanvasRepository.existsSubmittedByUserId(10L)).isTrue();
    }

    @Test
    @DisplayName("deleteAllByIdIn - JPA에 위임한다")
    void deleteAllByIdIn() {
        List<Long> goalIds = List.of(1L, 2L);

        flowCanvasRepository.deleteAllByIdIn(goalIds);

        then(flowCanvasJpaRepository).should().deleteAllByIdInBatch(goalIds);
    }

    @Test
    @DisplayName("submitAllByUserId - 유저의 모든 캔버스를 제출 처리한다")
    void submitAllByUserId() {
        FlowCanvas entity = createEntity(1L, 10L);
        given(flowCanvasJpaRepository.findByUserId(10L)).willReturn(List.of(entity));

        flowCanvasRepository.submitAllByUserId(10L);

        then(flowCanvasJpaRepository).should().saveAll(List.of(entity));
    }
}
