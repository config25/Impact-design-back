package qtedu.Impact_design.storage.jparepository.teach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.teach.TbMission;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TbMissionJpaRepository extends JpaRepository<TbMission, Integer> {

    Optional<TbMission> findFirstByGameIdOrderByMissionIdDesc(Integer gameId);

    @Modifying
    @Query("UPDATE TbMission m SET m.enddate = :enddate WHERE m.missionId = :missionId")
    void updateEnddate(@Param("missionId") Integer missionId, @Param("enddate") LocalDateTime enddate);
}
