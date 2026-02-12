package qtedu.Impact_design.storage.repository.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.team.TbMissionModel;
import qtedu.Impact_design.domain.repository.teach.TbMissionRepository;
import qtedu.Impact_design.storage.jpaentity.teach.TbMission;
import qtedu.Impact_design.storage.jparepository.teach.TbMissionJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TbMissionRepositoryImpl implements TbMissionRepository {

    private final TbMissionJpaRepository tbMissionJpaRepository;

    @Override
    public Optional<TbMissionModel> findLatestByGameId(Long gameId) {
        return tbMissionJpaRepository.findFirstByGameIdOrderByMissionIdDesc(gameId)
                .map(this::toModel);
    }

    @Override
    public TbMissionModel save(TbMissionModel model) {
        TbMission entity = TbMission.builder()
                .missionId(model.getMissionId())
                .sequence(model.getSequence())
                .subject(model.getSubject())
                .summary(model.getSummary())
                .startdate(model.getStartdate())
                .enddate(model.getEnddate())
                .ddYear(model.getDdYear())
                .ddTerm(model.getDdTerm())
                .mlevel(model.getMlevel())
                .gameId(model.getGameId())
                .toinform(model.getToinform())
                .build();
        return toModel(tbMissionJpaRepository.save(entity));
    }

    private TbMissionModel toModel(TbMission entity) {
        return TbMissionModel.builder()
                .missionId(entity.getMissionId())
                .sequence(entity.getSequence())
                .subject(entity.getSubject())
                .summary(entity.getSummary())
                .startdate(entity.getStartdate())
                .enddate(entity.getEnddate())
                .ddYear(entity.getDdYear())
                .ddTerm(entity.getDdTerm())
                .mlevel(entity.getMlevel())
                .gameId(entity.getGameId())
                .toinform(entity.getToinform())
                .build();
    }
}
