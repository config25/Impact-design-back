package qtedu.Impact_design.domain.repository.teach;

import qtedu.Impact_design.storage.jparepository.teach.ClassInfoProjection;

import java.util.List;

public interface GameRepository {
    List<ClassInfoProjection> findClassList(Long userId, Integer status);
}
