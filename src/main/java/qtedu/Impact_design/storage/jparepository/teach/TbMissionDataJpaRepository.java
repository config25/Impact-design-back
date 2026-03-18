package qtedu.Impact_design.storage.jparepository.teach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.teach.TbMissionData;

import java.util.List;

@Repository
public interface TbMissionDataJpaRepository extends JpaRepository<TbMissionData, Integer> {

    List<TbMissionData> findByMissionId(Integer missionId);

    List<TbMissionData> findByMissionIdIn(List<Integer> missionIds);
}
