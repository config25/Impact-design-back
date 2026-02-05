package qtedu.Impact_design.storage.repository.web.win_canvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.win_canvas.TaskInputModel;
import qtedu.Impact_design.domain.repository.win_canvas.TaskInputRepository;
import qtedu.Impact_design.storage.jpaentity.win_canvas.TaskInput;
import qtedu.Impact_design.storage.jparepository.web.win_canvas.TaskInputJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TaskInputRepositoryImpl implements TaskInputRepository {

    private final TaskInputJpaRepository taskInputJpaRepository;

    @Override
    public List<TaskInputModel> findByCanvasId(Long canvasId) {
        return taskInputJpaRepository.findByCanvasIdOrderByOrderNoAsc(canvasId)
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<TaskInputModel> findByCanvasIdAndOrderNo(Long canvasId, Integer orderNo) {
        return taskInputJpaRepository.findByCanvasIdAndOrderNo(canvasId, orderNo)
                .map(this::toModel);
    }

    @Override
    public TaskInputModel save(TaskInputModel taskInputModel) {
        TaskInput entity = taskInputModel.getInputId() != null
                ? taskInputJpaRepository.findById(taskInputModel.getInputId())
                    .map(existing -> {
                        existing.update(taskInputModel.getResourceName(), taskInputModel.getQuantity(),
                                taskInputModel.getOrderNo());
                        return existing;
                    })
                    .orElseGet(() -> TaskInput.builder()
                            .canvasId(taskInputModel.getCanvasId())
                            .resourceName(taskInputModel.getResourceName())
                            .quantity(taskInputModel.getQuantity())
                            .orderNo(taskInputModel.getOrderNo())
                            .build())
                : TaskInput.builder()
                        .canvasId(taskInputModel.getCanvasId())
                        .resourceName(taskInputModel.getResourceName())
                        .quantity(taskInputModel.getQuantity())
                        .orderNo(taskInputModel.getOrderNo())
                        .build();
        return toModel(taskInputJpaRepository.save(entity));
    }

    private TaskInputModel toModel(TaskInput entity) {
        return TaskInputModel.builder()
                .inputId(entity.getInputId())
                .resourceName(entity.getResourceName())
                .quantity(entity.getQuantity())
                .orderNo(entity.getOrderNo())
                .canvasId(entity.getCanvasId())
                .build();
    }
}
