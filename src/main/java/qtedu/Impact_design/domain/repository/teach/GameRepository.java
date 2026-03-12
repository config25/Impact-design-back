package qtedu.Impact_design.domain.repository.teach;

import qtedu.Impact_design.domain.model.team.ClassInfoProjection;

import java.util.List;

public interface GameRepository {
    List<ClassInfoProjection> findClassList(Long userId, Integer status);
    List<ClassInfoProjection> findAllActiveClassList();
}
