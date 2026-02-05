package qtedu.Impact_design.storage.repository.web.win_canvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.en.OutcomeType;
import qtedu.Impact_design.domain.model.win_canvas.TaskOutcomeModel;
import qtedu.Impact_design.domain.repository.win_canvas.TaskOutcomeRepository;
import qtedu.Impact_design.storage.jpaentity.win_canvas.TaskOutcome;
import qtedu.Impact_design.storage.jparepository.web.win_canvas.TaskOutcomeJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TaskOutcomeRepositoryImpl implements TaskOutcomeRepository {

    private final TaskOutcomeJpaRepository taskOutcomeJpaRepository;

    @Override
    public List<TaskOutcomeModel> findByCanvasId(Long canvasId) {
        return taskOutcomeJpaRepository.findByCanvasIdOrderByOrderNoAsc(canvasId)
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<TaskOutcomeModel> findByCanvasIdAndOrderNo(Long canvasId, Integer orderNo) {
        return taskOutcomeJpaRepository.findByCanvasIdAndOrderNo(canvasId, orderNo)
                .map(this::toModel);
    }

    @Override
    public TaskOutcomeModel save(TaskOutcomeModel taskOutcomeModel) {
        TaskOutcome entity = taskOutcomeModel.getOutcomeNo() != null
                ? taskOutcomeJpaRepository.findById(taskOutcomeModel.getOutcomeNo())
                    .map(existing -> {
                        existing.update(taskOutcomeModel.getOutcomeType(), taskOutcomeModel.getOutcomeContent(),
                                taskOutcomeModel.getOrderNo());
                        return existing;
                    })
                    .orElseGet(() -> TaskOutcome.builder()
                            .canvasId(taskOutcomeModel.getCanvasId())
                            .outcomeType(taskOutcomeModel.getOutcomeType())
                            .outcomeContent(taskOutcomeModel.getOutcomeContent())
                            .orderNo(taskOutcomeModel.getOrderNo())
                            .build())
                : TaskOutcome.builder()
                        .canvasId(taskOutcomeModel.getCanvasId())
                        .outcomeType(taskOutcomeModel.getOutcomeType())
                        .outcomeContent(taskOutcomeModel.getOutcomeContent())
                        .orderNo(taskOutcomeModel.getOrderNo())
                        .build();
        return toModel(taskOutcomeJpaRepository.save(entity));
    }

    private TaskOutcomeModel toModel(TaskOutcome entity) {
        return TaskOutcomeModel.builder()
                .outcomeNo(entity.getOutcomeNo())
                .outcomeType(entity.getOutcomeType())
                .outcomeContent(entity.getOutcomeContent())
                .orderNo(entity.getOrderNo())
                .canvasId(entity.getCanvasId())
                .build();
    }
}
