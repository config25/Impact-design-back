package qtedu.Impact_design.storage.repository.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.storage.jpaentity.teach.GameTeam;
import qtedu.Impact_design.storage.jparepository.teach.GameTeamJpaRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class GameTeamRepositoryImplTest {

    @InjectMocks
    private GameTeamRepositoryImpl gameTeamRepository;

    @Mock
    private GameTeamJpaRepository gameTeamJpaRepository;

    @Test
    @DisplayName("save - gameId와 teamId로 GameTeam Entity를 생성하여 저장한다")
    void save() {
        gameTeamRepository.save(1, 10);

        then(gameTeamJpaRepository).should().save(any(GameTeam.class));
    }

    @Test
    @DisplayName("findTeamIdsByGameId - 게임에 속한 팀 ID 목록을 반환한다")
    void findTeamIdsByGameId() {
        given(gameTeamJpaRepository.findTeamIdsByGameId(1)).willReturn(List.of(10, 20, 30));

        List<Integer> result = gameTeamRepository.findTeamIdsByGameId(1);

        assertThat(result).containsExactly(10, 20, 30);
    }

    @Test
    @DisplayName("countByGameId - 게임의 팀 수를 반환한다")
    void countByGameId() {
        given(gameTeamJpaRepository.countByGameId(1)).willReturn(3);

        assertThat(gameTeamRepository.countByGameId(1)).isEqualTo(3);
    }
}
