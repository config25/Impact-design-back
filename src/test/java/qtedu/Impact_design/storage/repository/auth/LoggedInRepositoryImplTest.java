package qtedu.Impact_design.storage.repository.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.model.auth.LoggedInModel;
import qtedu.Impact_design.storage.jpaentity.auth.LoggedIn;
import qtedu.Impact_design.storage.jparepository.auth.LoggedInJpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LoggedInRepositoryImplTest {

    @InjectMocks
    private LoggedInRepositoryImpl loggedInRepository;

    @Mock
    private LoggedInJpaRepository loggedInJpaRepository;

    private final LocalDateTime expiredAt = LocalDateTime.of(2026, 3, 12, 0, 0);

    @Nested
    @DisplayName("findByUserId")
    class FindByUserNo {

        @Test
        @DisplayName("존재하면 Model로 변환하여 반환한다")
        void returnsModel() {
            LoggedIn entity = LoggedIn.builder()
                    .loggedInId(1L).userId(10L).refreshToken("token").expiredAt(expiredAt).build();
            given(loggedInJpaRepository.findByUserId(10L)).willReturn(Optional.of(entity));

            Optional<LoggedInModel> result = loggedInRepository.findByUserId(10L);

            assertThat(result).isPresent();
            assertThat(result.get().getLoggedInId()).isEqualTo(1L);
            assertThat(result.get().getRefreshToken()).isEqualTo("token");
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional을 반환한다")
        void returnsEmpty() {
            given(loggedInJpaRepository.findByUserId(999L)).willReturn(Optional.empty());

            assertThat(loggedInRepository.findByUserId(999L)).isEmpty();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("ID가 없으면 새 Entity를 생성하여 저장한다")
        void createsNew() {
            LoggedInModel model = LoggedInModel.builder()
                    .userId(10L).refreshToken("newToken").expiredAt(expiredAt).build();
            LoggedIn savedEntity = LoggedIn.builder()
                    .loggedInId(1L).userId(10L).refreshToken("newToken").expiredAt(expiredAt).build();
            given(loggedInJpaRepository.save(any(LoggedIn.class))).willReturn(savedEntity);

            LoggedInModel result = loggedInRepository.save(model);

            assertThat(result.getLoggedInId()).isEqualTo(1L);
            assertThat(result.getRefreshToken()).isEqualTo("newToken");
        }

        @Test
        @DisplayName("ID가 있으면 기존 Entity를 찾아 업데이트한다")
        void updatesExisting() {
            LoggedInModel model = LoggedInModel.builder()
                    .loggedInId(1L).userId(10L).refreshToken("updatedToken").expiredAt(expiredAt).build();
            LoggedIn existingEntity = LoggedIn.builder()
                    .loggedInId(1L).userId(10L).refreshToken("oldToken").expiredAt(expiredAt).build();
            given(loggedInJpaRepository.findById(1L)).willReturn(Optional.of(existingEntity));
            given(loggedInJpaRepository.save(existingEntity)).willReturn(existingEntity);

            LoggedInModel result = loggedInRepository.save(model);

            assertThat(result.getLoggedInId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("ID가 있지만 Entity가 없으면 예외가 발생한다")
        void throwsWhenNotFound() {
            LoggedInModel model = LoggedInModel.builder()
                    .loggedInId(999L).userId(10L).refreshToken("token").expiredAt(expiredAt).build();
            given(loggedInJpaRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> loggedInRepository.save(model))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    @DisplayName("deleteByUserId - JPA에 위임한다")
    void deleteByUserId() {
        loggedInRepository.deleteByUserId(10L);

        then(loggedInJpaRepository).should().deleteByUserId(10L);
    }
}
