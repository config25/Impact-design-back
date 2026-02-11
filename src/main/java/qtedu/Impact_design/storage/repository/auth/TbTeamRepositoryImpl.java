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
        return tbTeamJpaRepository.findByCodeAndTeamIdNot(code, excludeTeamId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TbTeamModel> findByTeamId(Integer teamId) {
        return tbTeamJpaRepository.findByTeamId(teamId).map(this::toModel);
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
                .teamGubun(entity.getTeamGubun())
                .numUser(entity.getNumUser())
                .build();
    }
}
