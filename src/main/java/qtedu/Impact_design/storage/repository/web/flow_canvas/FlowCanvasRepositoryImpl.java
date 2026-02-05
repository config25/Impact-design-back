package qtedu.Impact_design.storage.repository.web.flow_canvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.repository.flow_canvas.FlowCanvasRepository;
import qtedu.Impact_design.storage.jpaentity.flow_canvas.FlowCanvas;
import qtedu.Impact_design.storage.jparepository.web.flow_canvas.FlowCanvasJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FlowCanvasRepositoryImpl implements FlowCanvasRepository {

    private final FlowCanvasJpaRepository flowCanvasJpaRepository;

    @Override
    public List<FlowCanvasModel> findByUserId(Long userId) {
        return flowCanvasJpaRepository.findByUserIdOrderByOrderNoAsc(userId)
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<FlowCanvasModel> findById(Long goalId) {
        return flowCanvasJpaRepository.findById(goalId).map(this::toModel);
    }

    @Override
    public Optional<FlowCanvasModel> findByUserIdAndOrderNo(Long userId, Integer orderNo) {
        return flowCanvasJpaRepository.findByUserIdAndOrderNo(userId, orderNo).map(this::toModel);
    }

    @Override
    public FlowCanvasModel save(FlowCanvasModel model) {
        FlowCanvas entity;
        if (model.getGoalId() != null) {
            entity = flowCanvasJpaRepository.findById(model.getGoalId())
                    .orElse(FlowCanvas.builder()
                            .userId(model.getUserId())
                            .build());
            entity.update(model.getGoalTitle(), model.getGoalDescription(), model.getOrderNo());
        } else {
            entity = FlowCanvas.builder()
                    .userId(model.getUserId())
                    .goalTitle(model.getGoalTitle())
                    .goalDescription(model.getGoalDescription())
                    .orderNo(model.getOrderNo())
                    .build();
        }
        return toModel(flowCanvasJpaRepository.save(entity));
    }

    @Override
    public void deleteAllByIdIn(List<Long> goalIds) {
        flowCanvasJpaRepository.deleteAllByIdInBatch(goalIds);
    }

    @Override
    public List<FlowCanvasModel> findByUserIdIn(List<Long> userIds) {
        return flowCanvasJpaRepository.findByUserIdIn(userIds)
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    private FlowCanvasModel toModel(FlowCanvas entity) {
        return FlowCanvasModel.builder()
                .goalId(entity.getGoalId())
                .goalTitle(entity.getGoalTitle())
                .goalDescription(entity.getGoalDescription())
                .orderNo(entity.getOrderNo())
                .userId(entity.getUserId())
                .build();
    }
}
