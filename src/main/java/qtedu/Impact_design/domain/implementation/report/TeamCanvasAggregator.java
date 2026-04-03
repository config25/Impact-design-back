package qtedu.Impact_design.domain.implementation.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.dto.response.report.TeamCanvasResponse;
import qtedu.Impact_design.domain.implementation.flowcanvas.FlowCanvasReader;
import qtedu.Impact_design.domain.implementation.identitycanvas.IdentityCanvasReader;
import qtedu.Impact_design.domain.implementation.wincanvas.WinCanvasReader;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeamCanvasAggregator {

    private final IdentityCanvasReader identityCanvasReader;
    private final FlowCanvasReader flowCanvasReader;
    private final WinCanvasReader winCanvasReader;
    private final TeamUserRepository teamUserRepository;
    private final UserinfoRepository userinfoRepository;

    public TeamCanvasResponse buildForTeam(Integer teamId, Long writerId, String teamName, String imageUrl) {
        List<IdentityCanvasModel> identities = identityCanvasReader.readByUserIds(List.of(writerId));

        return TeamCanvasResponse.builder()
                .teamId(teamId)
                .teamName(teamName)
                .writerUserId(writerId)
                .imageUrl(imageUrl)
                .identityCanvas(identities.isEmpty() ? null : IdentityCanvasResponse.from(identities.get(0)))
                .flowCanvas(flowCanvasReader.read(writerId))
                .quickWinCanvas(winCanvasReader.read(writerId, CanvasType.QUICK))
                .buildWinCanvas(winCanvasReader.read(writerId, CanvasType.BUILD))
                .build();
    }

    public Map<Integer, Long> findWriterByTeam(List<Integer> teamIds) {
        List<TeamUserModel> allTeamUsers = teamUserRepository.findByTeamIdIn(teamIds);
        Map<Integer, List<Long>> teamUserMap = allTeamUsers.stream()
                .collect(Collectors.groupingBy(
                        TeamUserModel::getTeamId,
                        Collectors.mapping(TeamUserModel::getUserId, Collectors.toList())
                ));

        Map<Integer, Long> writerByTeam = new LinkedHashMap<>();
        for (Integer teamId : teamIds) {
            List<Long> memberIds = teamUserMap.getOrDefault(teamId, Collections.emptyList());
            if (memberIds.isEmpty()) continue;

            Long writerId = userinfoRepository.findWriterByUserIds(memberIds)
                    .map(u -> u.getUserId())
                    .orElse(memberIds.get(0));
            writerByTeam.put(teamId, writerId);
        }
        return writerByTeam;
    }
}
