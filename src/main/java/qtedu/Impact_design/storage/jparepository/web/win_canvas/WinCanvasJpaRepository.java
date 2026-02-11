package qtedu.Impact_design.storage.jparepository.web.win_canvas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.storage.jpaentity.win_canvas.WinCanvas;

import java.util.List;
import java.util.Optional;

@Repository
public interface WinCanvasJpaRepository extends JpaRepository<WinCanvas, Long> {
    Optional<WinCanvas> findByUserIdAndCanvasType(Long userId, CanvasType canvasType);
    List<WinCanvas> findByUserIdInAndCanvasType(List<Long> userIds, CanvasType canvasType);
    boolean existsByUserIdAndCanvasTypeAndSubmittedTrue(Long userId, CanvasType canvasType);
    boolean existsByUserIdAndCanvasTypeAndSubmitted(Long userId, CanvasType canvasType, Boolean submitted);
}
