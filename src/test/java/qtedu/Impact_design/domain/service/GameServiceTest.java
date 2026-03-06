package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.response.game.StudentDashboardResponse;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.implementation.game.GameReader;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private GameReader gameReader;

    @Test
    @DisplayName("getUserStep - 유저의 현재 step을 반환한다")
    void getUserStep() {
        given(gameReader.readStepByUserId(1L)).willReturn("step3");

        String step = gameService.getUserStep(1L);

        assertThat(step).isEqualTo("step3");
    }

    @Nested
    @DisplayName("getDashboard")
    class GetDashboard {

        @Test
        @DisplayName("정상적으로 대시보드 정보를 반환한다")
        void returnsDashboard() {
            TbGameModel game = TbGameModel.builder()
                    .gameId(1).name("임팩트디자인 수업").imageUrl(null).build();
            UserinfoModel user = UserinfoModel.builder()
                    .userId(1L).userName("홍길동").build();

            given(gameReader.readGameByUserId(1L)).willReturn(game);
            given(gameReader.readTeamNameByUserId(1L)).willReturn("팀1");
            given(gameReader.readUserinfoByUserId(1L)).willReturn(user);
            given(gameReader.resolveImageUrl(null)).willReturn(null);

            StudentDashboardResponse result = gameService.getDashboard(1L);

            assertThat(result.getClassName()).isEqualTo("임팩트디자인 수업");
            assertThat(result.getTeamName()).isEqualTo("팀1");
            assertThat(result.getUserName()).isEqualTo("홍길동");
            assertThat(result.getClassImage()).isNull();
        }

        @Test
        @DisplayName("게임이 없으면 NotFoundException이 발생한다")
        void throwsWhenGameNotFound() {
            given(gameReader.readGameByUserId(1L)).willThrow(NotFoundException.class);

            assertThatThrownBy(() -> gameService.getDashboard(1L))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("팀이 없으면 NotFoundException이 발생한다")
        void throwsWhenTeamNotFound() {
            TbGameModel game = TbGameModel.builder().gameId(1).name("수업").build();
            given(gameReader.readGameByUserId(1L)).willReturn(game);
            given(gameReader.readTeamNameByUserId(1L)).willThrow(NotFoundException.class);

            assertThatThrownBy(() -> gameService.getDashboard(1L))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("유저 정보가 없으면 NotFoundException이 발생한다")
        void throwsWhenUserNotFound() {
            TbGameModel game = TbGameModel.builder().gameId(1).name("수업").build();
            given(gameReader.readGameByUserId(1L)).willReturn(game);
            given(gameReader.readTeamNameByUserId(1L)).willReturn("팀1");
            given(gameReader.readUserinfoByUserId(1L)).willThrow(NotFoundException.class);

            assertThatThrownBy(() -> gameService.getDashboard(1L))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
