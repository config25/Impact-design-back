package qtedu.Impact_design.domain.implementation.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.response.teach.DeletedTeamResponse;
import qtedu.Impact_design.api.dto.response.teach.TeamInfoResponse;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TeachTeamReaderTest {

    @Mock private TbTeamRepository tbTeamRepository;
    @Mock private TeamUserRepository teamUserRepository;
    @Mock private UserinfoRepository userinfoRepository;

    @InjectMocks
    private TeachTeamReader teachTeamReader;

    @Nested
    @DisplayName("getDeletedTeams - 삭제된 팀 목록 조회")
    class GetDeletedTeams {

        @Test
        @DisplayName("삭제된 팀 목록을 반환한다")
        void returnsDeletedTeams() {
            given(tbTeamRepository.findDeletedByGameId(1)).willReturn(List.of(
                    TbTeamModel.builder().teamId(1).name("알파팀").sequence(1).build(),
                    TbTeamModel.builder().teamId(2).name("베타팀").sequence(2).build()));

            List<DeletedTeamResponse> result = teachTeamReader.getDeletedTeams(1);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTeamName()).isEqualTo("알파팀");
            assertThat(result.get(1).getSequence()).isEqualTo(2);
        }

        @Test
        @DisplayName("삭제된 팀이 없으면 빈 리스트를 반환한다")
        void returnsEmptyWhenNoDeletedTeams() {
            given(tbTeamRepository.findDeletedByGameId(1)).willReturn(Collections.emptyList());

            List<DeletedTeamResponse> result = teachTeamReader.getDeletedTeams(1);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getTeamInfo - 팀 상세 조회")
    class GetTeamInfo {

        @Test
        @DisplayName("팀 정보와 멤버 목록을 반환한다")
        void returnsTeamWithMembers() {
            given(tbTeamRepository.findByTeamId(1)).willReturn(Optional.of(
                    TbTeamModel.builder().teamId(1).name("알파팀").sequence(1).isDoing(1).build()));
            given(teamUserRepository.findByTeamId(1)).willReturn(List.of(
                    TeamUserModel.builder().userId(10L).teamId(1).build(),
                    TeamUserModel.builder().userId(11L).teamId(1).build()));
            given(userinfoRepository.findAllByUserIds(List.of(10L, 11L))).willReturn(List.of(
                    UserinfoModel.builder().userId(10L).loginId("a11").userName("학생1").writer("1").build(),
                    UserinfoModel.builder().userId(11L).loginId("a12").userName("학생2").writer("0").build()));

            TeamInfoResponse result = teachTeamReader.getTeamInfo(1);

            assertThat(result.getTeamName()).isEqualTo("알파팀");
            assertThat(result.getNumUser()).isEqualTo(2);
            assertThat(result.getMembers()).hasSize(2);
            assertThat(result.getMembers().get(0).getLoginId()).isEqualTo("a11");
        }

        @Test
        @DisplayName("팀원이 없으면 빈 멤버 리스트를 반환한다")
        void returnsEmptyMembersWhenNoUsers() {
            given(tbTeamRepository.findByTeamId(1)).willReturn(Optional.of(
                    TbTeamModel.builder().teamId(1).name("알파팀").sequence(1).isDoing(1).build()));
            given(teamUserRepository.findByTeamId(1)).willReturn(Collections.emptyList());

            TeamInfoResponse result = teachTeamReader.getTeamInfo(1);

            assertThat(result.getNumUser()).isEqualTo(0);
            assertThat(result.getMembers()).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 팀이면 NotFoundException 발생")
        void throwsWhenTeamNotFound() {
            given(tbTeamRepository.findByTeamId(99)).willReturn(Optional.empty());

            assertThatThrownBy(() -> teachTeamReader.getTeamInfo(99))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
