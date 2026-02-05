package qtedu.Impact_design.storage.jparepository.teach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.teach.TbTeam;

import java.util.List;

@Repository
public interface TbTeamJpaRepository extends JpaRepository<TbTeam, Integer> {
    List<TbTeam> findByCode(String code);
}
