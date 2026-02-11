package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.api.dto.response.teach.DeletedTeamResponse;
import qtedu.Impact_design.api.dto.response.teach.TeamInfoResponse;
import qtedu.Impact_design.domain.implementation.teach.TeachTeamAppender;
import qtedu.Impact_design.domain.implementation.teach.TeachTeamReader;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TeachTeamService {

    private final TeachTeamAppender teachTeamAppender;
    private final TeachTeamReader teachTeamReader;

    public void saveStep(Integer gameId, String step) {
        teachTeamAppender.saveStep(gameId, step);
    }

    public Integer addTeam(Integer gameId) {
        return teachTeamAppender.addTeam(gameId);
    }

    public Integer addEvaluationTeam(Integer gameId) {
        return teachTeamAppender.addEvaluationTeam(gameId);
    }

    public void deleteTeam(Integer teamId, Integer gameId) {
        teachTeamAppender.deleteTeam(teamId, gameId);
    }

    public void restoreTeam(Integer teamId, Integer gameId) {
        teachTeamAppender.restoreTeam(teamId, gameId);
    }

    public List<DeletedTeamResponse> getDeletedTeams(Integer gameId) {
        return teachTeamReader.getDeletedTeams(gameId);
    }

    public void updateTeamMember(Long userId, Integer teamId) {
        teachTeamAppender.updateTeamMember(userId, teamId);
    }

    public TeamInfoResponse getTeamInfo(Integer teamId) {
        return teachTeamReader.getTeamInfo(teamId);
    }

    public void updateTeamInfo(Integer teamId, String teamName, Integer sequence, Integer isDoing, Integer aiPlay) {
        teachTeamAppender.updateTeamInfo(teamId, teamName, sequence, isDoing, aiPlay);
    }

    public void deleteTeamMembers(List<Long> userIds) {
        teachTeamAppender.deleteTeamMembers(userIds);
    }

    public void setTeamWriter(Integer teamId, Long userId) {
        teachTeamAppender.setTeamWriter(teamId, userId);
    }
}
