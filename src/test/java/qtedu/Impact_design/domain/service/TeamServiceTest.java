package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.domain.implementation.team.TeamReader;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @InjectMocks
    private TeamService teamService;

    @Mock
    private TeamReader teamReader;

    @Test
    @DisplayName("getTeamMemberUserIds - 유저의 같은 팀 멤버 ID 목록을 반환한다")
    void getTeamMemberUserIds() {
        given(teamReader.readTeamMemberUserIds(1L)).willReturn(List.of(1L, 2L, 3L));

        List<Long> result = teamService.getTeamMemberUserIds(1L);

        assertThat(result).containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("getTeamMemberUserIdsByTeamId - teamId로 멤버 ID 목록을 반환한다")
    void getTeamMemberUserIdsByTeamId() {
        given(teamReader.readTeamMemberUserIdsByTeamId(10)).willReturn(List.of(1L, 2L));

        List<Long> result = teamService.getTeamMemberUserIdsByTeamId(10);

        assertThat(result).containsExactly(1L, 2L);
    }
}
