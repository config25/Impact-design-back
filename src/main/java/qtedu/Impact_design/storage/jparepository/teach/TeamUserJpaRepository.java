package qtedu.Impact_design.storage.jparepository.teach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.teach.TeamUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamUserJpaRepository extends JpaRepository<TeamUser, Integer> {
    List<TeamUser> findByTeamId(Integer teamId);
    Optional<TeamUser> findByUserId(Long userId);
}
