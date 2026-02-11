package qtedu.Impact_design.storage.jparepository.teach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.teach.GameAdmin;
import qtedu.Impact_design.storage.jpaentity.teach.GameAdminId;

@Repository
public interface GameAdminJpaRepository extends JpaRepository<GameAdmin, GameAdminId> {
}
