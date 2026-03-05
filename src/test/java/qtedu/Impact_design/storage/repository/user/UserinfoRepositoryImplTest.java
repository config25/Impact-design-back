package qtedu.Impact_design.storage.repository.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.model.en.UserRole;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.storage.jpaentity.user.Userinfo;
import qtedu.Impact_design.storage.jparepository.user.UserinfoJpaRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserinfoRepositoryImplTest {

    @InjectMocks
    private UserinfoRepositoryImpl userinfoRepository;

    @Mock
    private UserinfoJpaRepository userinfoJpaRepository;

    private Userinfo createEntity(Long userId, String loginId) {
        return Userinfo.builder()
                .userId(userId).loginId(loginId).password("encoded")
                .userName("홍길동").code("ABC").userRole(UserRole.STUDENT).writer("0")
                .build();
    }

    @Nested
    @DisplayName("findByLoginId")
    class FindByLoginId {

        @Test
        @DisplayName("존재하는 loginId면 Model로 변환하여 반환한다")
        void returnsModelWhenFound() {
            Userinfo entity = createEntity(1L, "a11");
            given(userinfoJpaRepository.findByLoginId("a11")).willReturn(Optional.of(entity));

            Optional<UserinfoModel> result = userinfoRepository.findByLoginId("a11");

            assertThat(result).isPresent();
            assertThat(result.get().getUserId()).isEqualTo(1L);
            assertThat(result.get().getLoginId()).isEqualTo("a11");
            assertThat(result.get().getUserRole()).isEqualTo(UserRole.STUDENT);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional을 반환한다")
        void returnsEmptyWhenNotFound() {
            given(userinfoJpaRepository.findByLoginId("unknown")).willReturn(Optional.empty());

            Optional<UserinfoModel> result = userinfoRepository.findByLoginId("unknown");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("Model을 Entity로 변환하여 저장하고 다시 Model로 반환한다")
        void savesAndConverts() {
            UserinfoModel model = UserinfoModel.builder()
                    .loginId("a11").password("encoded").userName("홍길동")
                    .code("ABC").userRole(UserRole.STUDENT).writer("0")
                    .build();
            Userinfo savedEntity = createEntity(1L, "a11");
            given(userinfoJpaRepository.save(any(Userinfo.class))).willReturn(savedEntity);

            UserinfoModel result = userinfoRepository.save(model);

            assertThat(result.getUserId()).isEqualTo(1L);
            assertThat(result.getLoginId()).isEqualTo("a11");
        }
    }

    @Nested
    @DisplayName("existsByLoginId")
    class ExistsByLoginId {

        @Test
        @DisplayName("존재하면 true를 반환한다")
        void returnsTrueWhenExists() {
            given(userinfoJpaRepository.existsByLoginId("a11")).willReturn(true);

            assertThat(userinfoRepository.existsByLoginId("a11")).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false를 반환한다")
        void returnsFalseWhenNotExists() {
            given(userinfoJpaRepository.existsByLoginId("unknown")).willReturn(false);

            assertThat(userinfoRepository.existsByLoginId("unknown")).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByUserIds")
    class FindAllByUserIds {

        @Test
        @DisplayName("여러 userId로 조회하여 Model 리스트를 반환한다")
        void returnsModelList() {
            List<Userinfo> entities = List.of(
                    createEntity(1L, "a11"),
                    createEntity(2L, "a12")
            );
            given(userinfoJpaRepository.findAllById(List.of(1L, 2L))).willReturn(entities);

            List<UserinfoModel> result = userinfoRepository.findAllByUserIds(List.of(1L, 2L));

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getLoginId()).isEqualTo("a11");
            assertThat(result.get(1).getLoginId()).isEqualTo("a12");
        }
    }

    @Test
    @DisplayName("setWriter - JPA의 setWriter를 호출한다")
    void setWriter() {
        userinfoRepository.setWriter(1L);

        then(userinfoJpaRepository).should().setWriter(1L);
    }

    @Test
    @DisplayName("clearWriterByUserIds - JPA의 clearWriterByUserIds를 호출한다")
    void clearWriterByUserIds() {
        List<Long> userIds = List.of(1L, 2L);

        userinfoRepository.clearWriterByUserIds(userIds);

        then(userinfoJpaRepository).should().clearWriterByUserIds(userIds);
    }

    @Test
    @DisplayName("findLastLoginIdByPattern - JPA에 위임한다")
    void findLastLoginIdByPattern() {
        given(userinfoJpaRepository.findLastLoginIdByPattern("a1%")).willReturn(Optional.of("a13"));

        Optional<String> result = userinfoRepository.findLastLoginIdByPattern("a1%");

        assertThat(result).contains("a13");
    }
}
