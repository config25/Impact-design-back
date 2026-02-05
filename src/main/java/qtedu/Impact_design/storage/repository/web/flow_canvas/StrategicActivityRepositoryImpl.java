package qtedu.Impact_design.storage.repository.web.flow_canvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;
import qtedu.Impact_design.domain.repository.flow_canvas.StrategicActivityRepository;
import qtedu.Impact_design.storage.jpaentity.flow_canvas.StrategicActivity;
import qtedu.Impact_design.storage.jparepository.web.flow_canvas.StrategicActivityJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StrategicActivityRepositoryImpl implements StrategicActivityRepository {

    private final StrategicActivityJpaRepository strategicActivityJpaRepository;

    @Override
    public List<StrategicActivityModel> findByGoalIdIn(List<Long> goalIds) {
        return strategicActivityJpaRepository.findByGoalIdInOrderByOrderNoAsc(goalIds)
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<StrategicActivityModel> findByGoalIdAndOrderNo(Long goalId, Integer orderNo) {
        return strategicActivityJpaRepository.findByGoalIdAndOrderNo(goalId, orderNo).map(this::toModel);
    }

    @Override
    public StrategicActivityModel save(StrategicActivityModel model) {
        StrategicActivity entity;
        if (model.getActivityId() != null) {
            entity = strategicActivityJpaRepository.findById(model.getActivityId())
                    .orElse(StrategicActivity.builder()
                            .goalId(model.getGoalId())
                            .build());
            entity.update(model.getActivityMetric(), model.getInterCriteria(), model.getOrderNo());
        } else {
            entity = StrategicActivity.builder()
                    .goalId(model.getGoalId())
                    .activityMetric(model.getActivityMetric())
                    .interCriteria(model.getInterCriteria())
                    .orderNo(model.getOrderNo())
                    .build();
        }
        return toModel(strategicActivityJpaRepository.save(entity));
    }

    @Override
    public void deleteAllByIdIn(List<Long> activityIds) {
        strategicActivityJpaRepository.deleteAllByIdInBatch(activityIds);
    }

    @Override
    public void deleteByGoalIdIn(List<Long> goalIds) {
        strategicActivityJpaRepository.deleteByGoalIdIn(goalIds);
    }

    private StrategicActivityModel toModel(StrategicActivity entity) {
        return StrategicActivityModel.builder()
                .activityId(entity.getActivityId())
                .activityMetric(entity.getActivityMetric())
                .interCriteria(entity.getInterCriteria())
                .orderNo(entity.getOrderNo())
                .goalId(entity.getGoalId())
                .build();
    }
}
