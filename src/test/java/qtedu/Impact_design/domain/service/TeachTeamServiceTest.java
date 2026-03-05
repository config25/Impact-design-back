package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.response.teach.DeletedTeamResponse;
import qtedu.Impact_design.api.dto.response.teach.TeamInfoResponse;
import qtedu.Impact_design.domain.implementation.teach.TeachTeamAppender;
import qtedu.Impact_design.domain.implementation.teach.TeachTeamReader;
import qtedu.Impact_design.domain.implementation.teach.TeachTeamRemover;
import qtedu.Impact_design.domain.implementation.teach.TeachTeamUpdater;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TeachTeamServiceTest {

    @InjectMocks
    private TeachTeamService teachTeamService;

    @Mock
    private TeachTeamAppender teachTeamAppender;
    @Mock
    private TeachTeamUpdater teachTeamUpdater;
    @Mock
    private TeachTeamRemover teachTeamRemover;
    @Mock
    private TeachTeamReader teachTeamReader;

    @Test
    @DisplayName("addTeam - appender에 위임하고 teamId를 반환한다")
    void addTeam() {
        given(teachTeamAppender.addTeam(1)).willReturn(10);

        Integer result = teachTeamService.addTeam(1);

        assertThat(result).isEqualTo(10);
        then(teachTeamAppender).should().addTeam(1);
    }

    @Test
    @DisplayName("addEvaluationTeam - 평가팀 생성을 appender에 위임한다")
    void addEvaluationTeam() {
        given(teachTeamAppender.addEvaluationTeam(1)).willReturn(20);

        Integer result = teachTeamService.addEvaluationTeam(1);

        assertThat(result).isEqualTo(20);
    }

    @Test
    @DisplayName("addTeamMember - 팀원 추가 후 loginId를 반환한다")
    void addTeamMember() {
        given(teachTeamAppender.addTeamMember(10, 1)).willReturn("a11");

        String loginId = teachTeamService.addTeamMember(10, 1);

        assertThat(loginId).isEqualTo("a11");
    }

    @Test
    @DisplayName("saveStep - updater에 위임한다")
    void saveStep() {
        teachTeamService.saveStep(1, "step2");

        then(teachTeamUpdater).should().saveStep(1, "step2");
    }

    @Test
    @DisplayName("deleteTeam - remover에 위임한다")
    void deleteTeam() {
        teachTeamService.deleteTeam(10, 1);

        then(teachTeamRemover).should().deleteTeam(10, 1);
    }

    @Test
    @DisplayName("restoreTeam - updater에 위임한다")
    void restoreTeam() {
        teachTeamService.restoreTeam(10, 1);

        then(teachTeamUpdater).should().restoreTeam(10, 1);
    }

    @Test
    @DisplayName("getDeletedTeams - reader에서 삭제된 팀 목록을 반환한다")
    void getDeletedTeams() {
        List<DeletedTeamResponse> expected = List.of(
                DeletedTeamResponse.builder().teamId(10).teamName("팀1").sequence(1).build()
        );
        given(teachTeamReader.getDeletedTeams(1)).willReturn(expected);

        List<DeletedTeamResponse> result = teachTeamService.getDeletedTeams(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTeamId()).isEqualTo(10);
    }

    @Test
    @DisplayName("updateTeamMember - updater에 위임한다")
    void updateTeamMember() {
        teachTeamService.updateTeamMember(1L, 10);

        then(teachTeamUpdater).should().updateTeamMember(1L, 10);
    }

    @Test
    @DisplayName("getTeamInfo - reader에서 팀 정보를 반환한다")
    void getTeamInfo() {
        TeamInfoResponse expected = TeamInfoResponse.builder()
                .teamId(10).teamName("팀1").sequence(1).numUser(3).build();
        given(teachTeamReader.getTeamInfo(10)).willReturn(expected);

        TeamInfoResponse result = teachTeamService.getTeamInfo(10);

        assertThat(result.getTeamName()).isEqualTo("팀1");
        assertThat(result.getNumUser()).isEqualTo(3);
    }

    @Test
    @DisplayName("updateTeamInfo - updater에 위임한다")
    void updateTeamInfo() {
        teachTeamService.updateTeamInfo(10, "새팀명", 2, 1, 0);

        then(teachTeamUpdater).should().updateTeamInfo(10, "새팀명", 2, 1, 0);
    }

    @Test
    @DisplayName("deleteTeamMembers - remover에 위임한다")
    void deleteTeamMembers() {
        List<Long> userIds = List.of(1L, 2L, 3L);

        teachTeamService.deleteTeamMembers(userIds);

        then(teachTeamRemover).should().deleteTeamMembers(userIds);
    }

    @Test
    @DisplayName("setTeamWriter - updater에 위임한다")
    void setTeamWriter() {
        teachTeamService.setTeamWriter(10, 1L);

        then(teachTeamUpdater).should().setTeamWriter(10, 1L);
    }
}
