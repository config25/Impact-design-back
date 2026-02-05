package qtedu.Impact_design.domain.repository;

import qtedu.Impact_design.domain.model.FLetterOfIntent2Model;

import java.util.List;

public interface FLetterOfIntent2Repository {
    List<FLetterOfIntent2Model> findByCanvasIdIn(List<Long> canvasIds);
}
