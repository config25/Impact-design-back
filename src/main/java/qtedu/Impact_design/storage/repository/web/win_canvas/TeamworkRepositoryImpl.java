package qtedu.Impact_design.storage.repository.web.win_canvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.win_canvas.TeamworkModel;
import qtedu.Impact_design.domain.repository.win_canvas.TeamworkRepository;
import qtedu.Impact_design.storage.jpaentity.win_canvas.Teamwork;
import qtedu.Impact_design.storage.jparepository.web.win_canvas.TeamworkJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TeamworkRepositoryImpl implements TeamworkRepository {

    private final TeamworkJpaRepository teamworkJpaRepository;

    @Override
    public Optional<TeamworkModel> findByCanvasId(Long canvasId) {
        return teamworkJpaRepository.findByCanvasId(canvasId)
                .map(this::toModel);
    }

    @Override
    public TeamworkModel save(TeamworkModel teamworkModel) {
        Teamwork entity = teamworkModel.getTeamworkId() != null
                ? teamworkJpaRepository.findById(teamworkModel.getTeamworkId())
                    .map(existing -> {
                        existing.update(teamworkModel.getActivityTeamwork(), teamworkModel.getWorkType());
                        return existing;
                    })
                    .orElseGet(() -> Teamwork.builder()
                            .canvasId(teamworkModel.getCanvasId())
                            .activityTeamwork(teamworkModel.getActivityTeamwork())
                            .workType(teamworkModel.getWorkType())
                            .build())
                : Teamwork.builder()
                        .canvasId(teamworkModel.getCanvasId())
                        .activityTeamwork(teamworkModel.getActivityTeamwork())
                        .workType(teamworkModel.getWorkType())
                        .build();
        return toModel(teamworkJpaRepository.save(entity));
    }

    private TeamworkModel toModel(Teamwork entity) {
        return TeamworkModel.builder()
                .teamworkId(entity.getTeamworkId())
                .activityTeamwork(entity.getActivityTeamwork())
                .workType(entity.getWorkType())
                .canvasId(entity.getCanvasId())
                .build();
    }
}
