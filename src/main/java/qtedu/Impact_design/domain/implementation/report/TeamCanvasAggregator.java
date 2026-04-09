package qtedu.Impact_design.domain.implementation.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.dto.response.report.GameCanvasesResponse;
import qtedu.Impact_design.api.dto.response.wincanvas.WinCanvasResponse;
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

    public TeamCanvasBundle buildForTeam(Integer teamId, Long writerId, String teamName) {
        List<IdentityCanvasModel> identities = identityCanvasReader.readByUserIds(List.of(writerId));
        IdentityCanvasResponse identity = identities.isEmpty() ? null : IdentityCanvasResponse.from(identities.get(0));
        FlowCanvasResponse flow = flowCanvasReader.read(writerId);
        WinCanvasResponse quick = winCanvasReader.read(writerId, CanvasType.QUICK);
        WinCanvasResponse build = winCanvasReader.read(writerId, CanvasType.BUILD);

        GameCanvasesResponse.IdentityCanvasItem identityItem = identity == null ? null
                : GameCanvasesResponse.IdentityCanvasItem.builder()
                        .teamId(teamId).teamName(teamName).writerUserId(writerId).canvas(identity).build();
        GameCanvasesResponse.FlowCanvasItem flowItem = flow == null ? null
                : GameCanvasesResponse.FlowCanvasItem.builder()
                        .teamId(teamId).teamName(teamName).writerUserId(writerId).canvas(flow).build();
        GameCanvasesResponse.WinCanvasItem quickItem = quick == null ? null
                : GameCanvasesResponse.WinCanvasItem.builder()
                        .teamId(teamId).teamName(teamName).writerUserId(writerId).canvas(quick).build();
        GameCanvasesResponse.WinCanvasItem buildItem = build == null ? null
                : GameCanvasesResponse.WinCanvasItem.builder()
                        .teamId(teamId).teamName(teamName).writerUserId(writerId).canvas(build).build();

        return new TeamCanvasBundle(identityItem, flowItem, quickItem, buildItem);
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

    public static class TeamCanvasBundle {
        private final GameCanvasesResponse.IdentityCanvasItem identity;
        private final GameCanvasesResponse.FlowCanvasItem flow;
        private final GameCanvasesResponse.WinCanvasItem quick;
        private final GameCanvasesResponse.WinCanvasItem build;

        public TeamCanvasBundle(GameCanvasesResponse.IdentityCanvasItem identity,
                                GameCanvasesResponse.FlowCanvasItem flow,
                                GameCanvasesResponse.WinCanvasItem quick,
                                GameCanvasesResponse.WinCanvasItem build) {
            this.identity = identity;
            this.flow = flow;
            this.quick = quick;
            this.build = build;
        }

        public GameCanvasesResponse.IdentityCanvasItem getIdentity() { return identity; }
        public GameCanvasesResponse.FlowCanvasItem getFlow() { return flow; }
        public GameCanvasesResponse.WinCanvasItem getQuick() { return quick; }
        public GameCanvasesResponse.WinCanvasItem getBuild() { return build; }
    }
}
