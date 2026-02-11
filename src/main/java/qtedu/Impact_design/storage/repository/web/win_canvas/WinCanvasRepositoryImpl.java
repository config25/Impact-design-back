package qtedu.Impact_design.storage.repository.web.win_canvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.win_canvas.WinCanvasModel;
import qtedu.Impact_design.domain.repository.win_canvas.WinCanvasRepository;
import qtedu.Impact_design.storage.jpaentity.win_canvas.WinCanvas;
import qtedu.Impact_design.storage.jparepository.web.win_canvas.WinCanvasJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WinCanvasRepositoryImpl implements WinCanvasRepository {

    private final WinCanvasJpaRepository winCanvasJpaRepository;

    @Override
    public Optional<WinCanvasModel> findByUserIdAndCanvasType(Long userId, CanvasType canvasType) {
        return winCanvasJpaRepository.findByUserIdAndCanvasType(userId, canvasType)
                .map(this::toModel);
    }

    @Override
    public WinCanvasModel save(WinCanvasModel winCanvasModel) {
        WinCanvas entity = winCanvasJpaRepository.findById(winCanvasModel.getCanvasId() != null ? winCanvasModel.getCanvasId() : 0L)
                .map(existing -> {
                    existing.update(winCanvasModel.getStrategicGoal(), winCanvasModel.getTaskName(),
                            winCanvasModel.getTaskDescription(), winCanvasModel.getCrisisSignal(),
                            winCanvasModel.getPainTouchPoint());
                    return existing;
                })
                .orElseGet(() -> WinCanvas.builder()
                        .userId(winCanvasModel.getUserId())
                        .canvasType(winCanvasModel.getCanvasType())
                        .strategicGoal(winCanvasModel.getStrategicGoal())
                        .taskName(winCanvasModel.getTaskName())
                        .taskDescription(winCanvasModel.getTaskDescription())
                        .crisisSignal(winCanvasModel.getCrisisSignal())
                        .painTouchPoint(winCanvasModel.getPainTouchPoint())
                        .build());
        return toModel(winCanvasJpaRepository.save(entity));
    }

    @Override
    public List<WinCanvasModel> findByUserIdInAndCanvasType(List<Long> userIds, CanvasType canvasType) {
        return winCanvasJpaRepository.findByUserIdInAndCanvasType(userIds, canvasType)
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public boolean existsSubmittedByUserIdAndCanvasType(Long userId, CanvasType canvasType) {
        return winCanvasJpaRepository.existsByUserIdAndCanvasTypeAndSubmittedTrue(userId, canvasType);
    }

    @Override
    public void submitByUserIdAndCanvasType(Long userId, CanvasType canvasType) {
        winCanvasJpaRepository.findByUserIdAndCanvasType(userId, canvasType)
                .ifPresent(entity -> {
                    entity.submit();
                    winCanvasJpaRepository.save(entity);
                });
    }

    private WinCanvasModel toModel(WinCanvas entity) {
        return WinCanvasModel.builder()
                .canvasId(entity.getCanvasId())
                .canvasType(entity.getCanvasType())
                .strategicGoal(entity.getStrategicGoal())
                .taskName(entity.getTaskName())
                .taskDescription(entity.getTaskDescription())
                .crisisSignal(entity.getCrisisSignal())
                .painTouchPoint(entity.getPainTouchPoint())
                .userId(entity.getUserId())
                .submitted(entity.getSubmitted())
                .build();
    }
}
