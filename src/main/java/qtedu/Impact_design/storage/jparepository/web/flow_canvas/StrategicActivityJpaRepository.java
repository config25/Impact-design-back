package qtedu.Impact_design.storage.jparepository.web.flow_canvas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.flow_canvas.StrategicActivity;

import java.util.List;
import java.util.Optional;

@Repository
public interface StrategicActivityJpaRepository extends JpaRepository<StrategicActivity, Long> {
    List<StrategicActivity> findByGoalIdInOrderByOrderNoAsc(List<Long> goalIds);
    Optional<StrategicActivity> findByGoalIdAndOrderNo(Long goalId, Integer orderNo);
    void deleteByGoalIdIn(List<Long> goalIds);
}
