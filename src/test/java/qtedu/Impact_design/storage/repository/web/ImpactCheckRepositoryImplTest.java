package qtedu.Impact_design.storage.repository.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.model.ImpactCheckModel;
import qtedu.Impact_design.storage.jpaentity.ImpactCheck;
import qtedu.Impact_design.storage.jparepository.web.ImpactCheckJpaRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ImpactCheckRepositoryImplTest {

    @InjectMocks
    private ImpactCheckRepositoryImpl impactCheckRepository;

    @Mock
    private ImpactCheckJpaRepository impactCheckJpaRepository;

    private ImpactCheck createEntity(Long answerId, Long userId) {
        return ImpactCheck.builder()
                .answerId(answerId).userId(userId)
                .q1Score(5).q2Score(4).q3Score(3).q4Score(2)
                .q5Score(1).q6Score(5).q7Score(4).q8Score(3)
                .q9Score(2).q10Score(1).q11Score(5).q12Score(4)
                .q13Text("텍스트13").q14Text("텍스트14")
                .q15Text("텍스트15").q16Text("텍스트16")
                .build();
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("존재하면 Model로 변환하여 반환한다")
        void returnsModel() {
            ImpactCheck entity = createEntity(1L, 10L);
            given(impactCheckJpaRepository.findByUserId(10L)).willReturn(Optional.of(entity));

            Optional<ImpactCheckModel> result = impactCheckRepository.findByUserId(10L);

            assertThat(result).isPresent();
            assertThat(result.get().getAnswerId()).isEqualTo(1L);
            assertThat(result.get().getQ1Score()).isEqualTo(5);
            assertThat(result.get().getQ13Text()).isEqualTo("텍스트13");
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional을 반환한다")
        void returnsEmpty() {
            given(impactCheckJpaRepository.findByUserId(999L)).willReturn(Optional.empty());

            assertThat(impactCheckRepository.findByUserId(999L)).isEmpty();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("ID가 없으면 새 Entity를 생성하여 저장한다")
        void createsNew() {
            ImpactCheckModel model = ImpactCheckModel.builder()
                    .userId(10L).q1Score(5).q2Score(4).build();
            ImpactCheck savedEntity = createEntity(1L, 10L);
            given(impactCheckJpaRepository.save(any(ImpactCheck.class))).willReturn(savedEntity);

            ImpactCheckModel result = impactCheckRepository.save(model);

            assertThat(result.getAnswerId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("ID가 있으면 기존 Entity를 찾아 업데이트한다")
        void updatesExisting() {
            ImpactCheckModel model = ImpactCheckModel.builder()
                    .answerId(1L).userId(10L).q1Score(3).build();
            ImpactCheck existingEntity = createEntity(1L, 10L);
            given(impactCheckJpaRepository.findById(1L)).willReturn(Optional.of(existingEntity));
            given(impactCheckJpaRepository.save(existingEntity)).willReturn(existingEntity);

            ImpactCheckModel result = impactCheckRepository.save(model);

            assertThat(result.getAnswerId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("ID가 있지만 Entity가 없으면 예외가 발생한다")
        void throwsWhenNotFound() {
            ImpactCheckModel model = ImpactCheckModel.builder()
                    .answerId(999L).userId(10L).build();
            given(impactCheckJpaRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> impactCheckRepository.save(model))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("findByUserIdIn - 여러 유저의 임팩트체크를 Model로 변환한다")
    void findByUserIdIn() {
        List<ImpactCheck> entities = List.of(
                createEntity(1L, 10L),
                createEntity(2L, 20L)
        );
        given(impactCheckJpaRepository.findByUserIdIn(List.of(10L, 20L))).willReturn(entities);

        List<ImpactCheckModel> result = impactCheckRepository.findByUserIdIn(List.of(10L, 20L));

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("existsSubmittedByUserId - JPA에 위임한다")
    void existsSubmittedByUserId() {
        given(impactCheckJpaRepository.existsByUserIdAndSubmitted(10L, true)).willReturn(true);

        assertThat(impactCheckRepository.existsSubmittedByUserId(10L)).isTrue();
    }

    @Test
    @DisplayName("submitByUserId - 해당 유저의 임팩트체크를 제출 처리한다")
    void submitByUserId() {
        ImpactCheck entity = createEntity(1L, 10L);
        given(impactCheckJpaRepository.findByUserId(10L)).willReturn(Optional.of(entity));

        impactCheckRepository.submitByUserId(10L);

        then(impactCheckJpaRepository).should().save(entity);
    }
}
