package qtedu.Impact_design.storage.repository.web.flow_canvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;
import qtedu.Impact_design.domain.repository.flow_canvas.TacticalRepository;
import qtedu.Impact_design.storage.jpaentity.flow_canvas.Tactical;
import qtedu.Impact_design.storage.jparepository.web.flow_canvas.TacticalJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TacticalRepositoryImpl implements TacticalRepository {

    private final TacticalJpaRepository tacticalJpaRepository;

    @Override
    public List<TacticalModel> findByGoalIdIn(List<Long> goalIds) {
        return tacticalJpaRepository.findByGoalIdInOrderByOrderNoAsc(goalIds)
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<TacticalModel> findByGoalIdAndOrderNo(Long goalId, Integer orderNo) {
        return tacticalJpaRepository.findByGoalIdAndOrderNo(goalId, orderNo).map(this::toModel);
    }

    @Override
    public TacticalModel save(TacticalModel model) {
        Tactical entity;
        if (model.getMetricId() != null) {
            entity = tacticalJpaRepository.findById(model.getMetricId())
                    .orElse(Tactical.builder()
                            .goalId(model.getGoalId())
                            .build());
            entity.update(model.getTacticalMetric(), model.getTacticalGoal(), model.getOrderNo());
        } else {
            entity = Tactical.builder()
                    .goalId(model.getGoalId())
                    .tacticalMetric(model.getTacticalMetric())
                    .tacticalGoal(model.getTacticalGoal())
                    .orderNo(model.getOrderNo())
                    .build();
        }
        return toModel(tacticalJpaRepository.save(entity));
    }

    @Override
    public void deleteAllByIdIn(List<Long> metricIds) {
        tacticalJpaRepository.deleteAllByIdInBatch(metricIds);
    }

    @Override
    public void deleteByGoalIdIn(List<Long> goalIds) {
        tacticalJpaRepository.deleteByGoalIdIn(goalIds);
    }

    private TacticalModel toModel(Tactical entity) {
        return TacticalModel.builder()
                .metricId(entity.getMetricId())
                .tacticalMetric(entity.getTacticalMetric())
                .tacticalGoal(entity.getTacticalGoal())
                .orderNo(entity.getOrderNo())
                .goalId(entity.getGoalId())
                .build();
    }
}
