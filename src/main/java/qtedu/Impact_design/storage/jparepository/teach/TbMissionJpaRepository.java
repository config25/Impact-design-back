package qtedu.Impact_design.storage.jparepository.teach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.teach.TbMission;

import java.util.Optional;

@Repository
public interface TbMissionJpaRepository extends JpaRepository<TbMission, Integer> {

    // 최신 mission 조회 (mission_id DESC)
    Optional<TbMission> findFirstByGameIdOrderByMissionIdDesc(Long gameId);
}
