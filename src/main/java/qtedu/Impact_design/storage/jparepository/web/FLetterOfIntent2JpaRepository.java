package qtedu.Impact_design.storage.jparepository.web;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.storage.jpaentity.FLetterOfIntent2;

import java.util.List;

@Repository
public interface FLetterOfIntent2JpaRepository extends JpaRepository<FLetterOfIntent2, Integer> {
    List<FLetterOfIntent2> findByCanvasIdIn(List<Long> canvasIds);
}
