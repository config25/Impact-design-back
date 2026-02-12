package qtedu.Impact_design.domain.repository.teach;

import qtedu.Impact_design.domain.model.team.ContentsModel;

import java.util.Optional;

public interface ContentsRepository {
    Optional<ContentsModel> findGameLogo(Integer gameId);
}
