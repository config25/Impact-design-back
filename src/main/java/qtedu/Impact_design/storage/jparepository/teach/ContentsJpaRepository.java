package qtedu.Impact_design.storage.jparepository.teach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.teach.Contents;

import java.util.Optional;

@Repository
public interface ContentsJpaRepository extends JpaRepository<Contents, Integer> {

    // gameLogo 조회: st_tp = 1이고 world_id가 game_id와 일치하는 첫 번째 레코드
    Optional<Contents> findFirstByStatusTypeAndWorldId(Integer statusType, Integer worldId);
}
