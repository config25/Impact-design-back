package qtedu.Impact_design.domain.repository.win_canvas;

import qtedu.Impact_design.domain.model.win_canvas.TeamworkModel;

import java.util.Optional;

public interface TeamworkRepository {
    Optional<TeamworkModel> findByCanvasId(Long canvasId);
    TeamworkModel save(TeamworkModel teamworkModel);
}
