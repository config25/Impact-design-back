package qtedu.Impact_design.storage.repository.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.storage.jpaentity.teach.TbTeam;
import qtedu.Impact_design.storage.jparepository.teach.TbTeamJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TbTeamRepositoryImpl implements TbTeamRepository {

    private final TbTeamJpaRepository tbTeamJpaRepository;

    @Override
    public List<TbTeamModel> findByCode(String code) {
        return tbTeamJpaRepository.findByCode(code).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TbTeamModel> findByCodeExcludingTeam(String code, Integer excludeTeamId) {
        return tbTeamJpaRepository.findByCodeExcludingDeletedTeam(code, excludeTeamId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TbTeamModel> findByTeamId(Integer teamId) {
        return tbTeamJpaRepository.findByTeamId(teamId).map(this::toModel);
    }

    @Override
    public List<TbTeamModel> findByTeamIdIn(List<Integer> teamIds) {
        return tbTeamJpaRepository.findByTeamIdIn(teamIds).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public TbTeamModel save(TbTeamModel model) {
        TbTeam entity = TbTeam.builder()
                .teamId(model.getTeamId())
                .name(model.getName())
                .sequence(model.getSequence())
                .status(model.getStatus())
                .aiPlay(model.getAiPlay())
                .code(model.getCode())
                .isDoing(model.getIsDoing())
                .teamCategory(model.getTeamCategory())
                .numUser(model.getNumUser())
                .build();
        return toModel(tbTeamJpaRepository.save(entity));
    }

    @Override
    public List<TbTeamModel> findDeletedByGameId(Integer gameId) {
        return tbTeamJpaRepository.findDeletedTeamsByGameId(gameId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public int findMaxSequenceByGameId(Integer gameId) {
        return tbTeamJpaRepository.findMaxSequenceByGameId(gameId);
    }

    @Override
    public void incrementNumUser(Integer teamId) {
        tbTeamJpaRepository.incrementNumUser(teamId);
    }

    @Override
    public void decrementNumUser(Integer teamId) {
        tbTeamJpaRepository.decrementNumUser(teamId);
    }

    private TbTeamModel toModel(TbTeam entity) {
        return TbTeamModel.builder()
                .teamId(entity.getTeamId())
                .name(entity.getName())
                .sequence(entity.getSequence())
                .status(entity.getStatus())
                .aiPlay(entity.getAiPlay())
                .code(entity.getCode())
                .isDoing(entity.getIsDoing())
                .teamCategory(entity.getTeamCategory())
                .numUser(entity.getNumUser())
                .build();
    }
}
