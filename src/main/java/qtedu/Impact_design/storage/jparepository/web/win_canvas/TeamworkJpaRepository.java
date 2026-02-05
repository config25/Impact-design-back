package qtedu.Impact_design.storage.jparepository.web.win_canvas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.win_canvas.Teamwork;

import java.util.Optional;

@Repository
public interface TeamworkJpaRepository extends JpaRepository<Teamwork, Long> {
    Optional<Teamwork> findByCanvasId(Long canvasId);
}
