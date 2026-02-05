package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.domain.implementation.team.TeamReader;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamReader teamReader;

    public List<Long> getTeamMemberUserIds(Long userId) {
        return teamReader.readTeamMemberUserIds(userId);
    }
}
