package qtedu.Impact_design.storage.jparepository.web.flow_canvas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.flow_canvas.Tactical;

import java.util.List;
import java.util.Optional;

@Repository
public interface TacticalJpaRepository extends JpaRepository<Tactical, Long> {
    List<Tactical> findByGoalIdInOrderByOrderNoAsc(List<Long> goalIds);
    Optional<Tactical> findByGoalIdAndOrderNo(Long goalId, Integer orderNo);
    void deleteByGoalIdIn(List<Long> goalIds);
}
