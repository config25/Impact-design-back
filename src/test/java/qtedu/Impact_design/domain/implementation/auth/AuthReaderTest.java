package qtedu.Impact_design.domain.implementation.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.response.auth.TeamInfoResponse;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthReaderTest {

    @Mock private UserinfoRepository userinfoRepository;
    @Mock private TbTeamRepository tbTeamRepository;

    @InjectMocks
    private AuthReader authReader;

    @Nested
    @DisplayName("findUserByLoginId - 로그인ID로 유저 조회")
    class FindUserByLoginId {

        @Test
        @DisplayName("유저를 정상 반환한다")
        void returnsUser() {
            given(userinfoRepository.findByLoginId("a11")).willReturn(Optional.of(
                    UserinfoModel.builder().userId(1L).loginId("a11").build()));

            UserinfoModel result = authReader.findUserByLoginId("a11");

            assertThat(result.getLoginId()).isEqualTo("a11");
        }

        @Test
        @DisplayName("존재하지 않는 loginId이면 NotFoundException 발생")
        void throwsWhenNotFound() {
            given(userinfoRepository.findByLoginId("xxx")).willReturn(Optional.empty());

            assertThatThrownBy(() -> authReader.findUserByLoginId("xxx"))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("checkCode - 수업 코드 확인")
    class CheckCode {

        @Test
        @DisplayName("코드로 팀 목록을 반환한다")
        void returnsTeams() {
            given(tbTeamRepository.findByCode("ABC")).willReturn(List.of(
                    TbTeamModel.builder().teamId(1).name("알파팀").build(),
                    TbTeamModel.builder().teamId(2).name("베타팀").build()));

            List<TeamInfoResponse> result = authReader.checkCode("ABC");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("알파팀");
        }

        @Test
        @DisplayName("코드에 해당하는 팀이 없으면 NotFoundException 발생")
        void throwsWhenCodeNotFound() {
            given(tbTeamRepository.findByCode("INVALID")).willReturn(Collections.emptyList());

            assertThatThrownBy(() -> authReader.checkCode("INVALID"))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
