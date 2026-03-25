package qtedu.Impact_design.domain.implementation.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GameReaderTest {

    @Mock private TbGameRepository tbGameRepository;
    @Mock private TeamUserRepository teamUserRepository;
    @Mock private UserinfoRepository userinfoRepository;
    @Mock private GameTeamRepository gameTeamRepository;

    @InjectMocks
    private GameReader gameReader;

    @Nested
    @DisplayName("readGameByUserId - 유저의 게임 조회")
    class ReadGameByUserId {

        @Test
        @DisplayName("게임을 정상 반환한다")
        void returnsGame() {
            given(tbGameRepository.findByUserId(1L)).willReturn(Optional.of(
                    TbGameModel.builder().gameId(1).name("수업1").build()));

            TbGameModel result = gameReader.readGameByUserId(1L);

            assertThat(result.getGameId()).isEqualTo(1);
        }

        @Test
        @DisplayName("게임이 없으면 NotFoundException 발생")
        void throwsWhenNotFound() {
            given(tbGameRepository.findByUserId(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> gameReader.readGameByUserId(99L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("readTeamNameByUserId - 유저의 팀 이름 조회")
    class ReadTeamNameByUserId {

        @Test
        @DisplayName("팀 이름을 반환한다")
        void returnsTeamName() {
            given(teamUserRepository.findTeamNameByUserId(1L)).willReturn(Optional.of("알파팀"));

            String result = gameReader.readTeamNameByUserId(1L);

            assertThat(result).isEqualTo("알파팀");
        }

        @Test
        @DisplayName("팀이 없으면 NotFoundException 발생")
        void throwsWhenNotFound() {
            given(teamUserRepository.findTeamNameByUserId(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> gameReader.readTeamNameByUserId(99L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("readUserinfoByUserId - 유저 정보 조회")
    class ReadUserinfoByUserId {

        @Test
        @DisplayName("유저 정보를 반환한다")
        void returnsUserinfo() {
            given(userinfoRepository.findByUserId(1L)).willReturn(Optional.of(
                    UserinfoModel.builder().userId(1L).loginId("a11").build()));

            UserinfoModel result = gameReader.readUserinfoByUserId(1L);

            assertThat(result.getLoginId()).isEqualTo("a11");
        }

        @Test
        @DisplayName("유저가 없으면 NotFoundException 발생")
        void throwsWhenNotFound() {
            given(userinfoRepository.findByUserId(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> gameReader.readUserinfoByUserId(99L))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
