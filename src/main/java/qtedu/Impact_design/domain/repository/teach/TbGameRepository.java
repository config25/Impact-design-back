package qtedu.Impact_design.domain.repository.teach;

import qtedu.Impact_design.domain.model.team.TbGameModel;

import java.util.Optional;

public interface TbGameRepository {
    Optional<TbGameModel> findById(Integer gameId);
    TbGameModel save(TbGameModel model);
    String findGameNameByUserId(Long userId);
    String findGameImageUrlByUserId(Long userId);
    void incrementNumTeam(Integer gameId);
    void decrementNumTeam(Integer gameId);
    void updateImageUrl(Integer gameId, String imageUrl);
    Optional<TbGameModel> findByUserId(Long userId);
    String findStepByUserId(Long userId);
}
