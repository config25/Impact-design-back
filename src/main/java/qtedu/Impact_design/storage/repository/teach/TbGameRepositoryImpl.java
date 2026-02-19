package qtedu.Impact_design.storage.repository.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;
import qtedu.Impact_design.storage.jpaentity.teach.TbGame;
import qtedu.Impact_design.storage.jparepository.teach.TbGameJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TbGameRepositoryImpl implements TbGameRepository {

    private final TbGameJpaRepository tbGameJpaRepository;

    @Value("${file.base-url:}")
    private String fileBaseUrl;

    @Override
    public Optional<TbGameModel> findById(Integer gameId) {
        return tbGameJpaRepository.findById(gameId).map(this::toModel);
    }

    @Override
    public TbGameModel save(TbGameModel model) {
        TbGame entity = TbGame.builder()
                .gameId(model.getGameId())
                .name(model.getName())
                .code(model.getCode())
                .num(model.getNum())
                .numTeam(model.getNumTeam())
                .numMember(model.getNumMember())
                .createdAt(model.getCreatedAt())
                .endedAt(model.getEndedAt())
                .status(model.getStatus())
                .eStatus(model.getEStatus())
                .summary(model.getSummary())
                .totalDd(model.getTotalDd())
                .lang(model.getLang())
                .worldType(model.getWorldType())
                .step(model.getStep())
                .classType(model.getClassType())
                .isDoing(model.getIsDoing())
                .regDate(model.getRegDate())
                .popupId(model.getPopupId())
                .imageUrl(model.getImageUrl())
                .target(model.getTarget())
                .projectDate(model.getProjectDate())
                .build();
        return toModel(tbGameJpaRepository.save(entity));
    }

    @Override
    public String findGameNameByUserId(Long userId) {
        return tbGameJpaRepository.findByUserId(userId)
                .map(TbGame::getName)
                .orElse(null);
    }

    @Override
    public String findGameImageUrlByUserId(Long userId) {
        return tbGameJpaRepository.findByUserId(userId)
                .map(TbGame::getImageUrl)
                .map(this::resolveImageUrl)
                .orElse(null);
    }

    @Override
    public void incrementNumTeam(Integer gameId) {
        tbGameJpaRepository.incrementNumTeam(gameId);
    }

    @Override
    public void decrementNumTeam(Integer gameId) {
        tbGameJpaRepository.decrementNumTeam(gameId);
    }

    @Override
    public Optional<TbGameModel> findByUserId(Long userId) {
        return tbGameJpaRepository.findByUserId(userId).map(this::toModel);
    }

    @Override
    public Optional<TbGameModel> findByTeamId(Integer teamId) {
        return tbGameJpaRepository.findByTeamId(teamId).map(this::toModel);
    }

    @Override
    public void updateImageUrl(Integer gameId, String imageUrl) {
        tbGameJpaRepository.updateImageUrl(gameId, imageUrl);
    }

    @Override
    public String findStepByUserId(Long userId) {
        return tbGameJpaRepository.findByUserId(userId)
                .map(TbGame::getStep)
                .orElse("");
    }

    private TbGameModel toModel(TbGame entity) {
        return TbGameModel.builder()
                .gameId(entity.getGameId())
                .name(entity.getName())
                .code(entity.getCode())
                .num(entity.getNum())
                .numTeam(entity.getNumTeam())
                .numMember(entity.getNumMember())
                .createdAt(entity.getCreatedAt())
                .endedAt(entity.getEndedAt())
                .status(entity.getStatus())
                .eStatus(entity.getEStatus())
                .summary(entity.getSummary())
                .totalDd(entity.getTotalDd())
                .lang(entity.getLang())
                .worldType(entity.getWorldType())
                .step(entity.getStep())
                .classType(entity.getClassType())
                .isDoing(entity.getIsDoing())
                .regDate(entity.getRegDate())
                .popupId(entity.getPopupId())
                .imageUrl(entity.getImageUrl())
                .target(entity.getTarget())
                .projectDate(entity.getProjectDate())
                .build();
    }

    private String resolveImageUrl(String path) {
        if (path == null || path.isBlank()) return null;
        return fileBaseUrl + "/" + path;
    }
}
