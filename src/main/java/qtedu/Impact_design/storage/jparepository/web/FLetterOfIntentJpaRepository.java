package qtedu.Impact_design.storage.jparepository.web;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.FLetterOfIntent;

import java.util.List;

@Repository
public interface FLetterOfIntentJpaRepository extends JpaRepository<FLetterOfIntent, Integer> {
    List<FLetterOfIntent> findByCanvasIdIn(List<Long> canvasIds);
}
