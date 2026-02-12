package qtedu.Impact_design.storage.repository.teach;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.repository.teach.GameRepository;
import qtedu.Impact_design.domain.model.team.ClassInfoProjection;
import qtedu.Impact_design.storage.jparepository.teach.TbGameJpaRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepository {

    private final TbGameJpaRepository tbGameJpaRepository;

    @Override
    public List<ClassInfoProjection> findClassList(Long userId, Integer status) {
        return tbGameJpaRepository.findClassList(userId, status);
    }

    @Override
    public List<ClassInfoProjection> findAllClassList() {
        return tbGameJpaRepository.findAllClassList();
    }

    @Override
    public List<ClassInfoProjection> findAllClassListByStatus(Integer status) {
        return tbGameJpaRepository.findAllClassListByStatus(status);
    }
}
