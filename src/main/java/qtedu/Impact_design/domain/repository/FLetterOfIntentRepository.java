package qtedu.Impact_design.domain.repository;

import qtedu.Impact_design.domain.model.FLetterOfIntentModel;

import java.util.List;

public interface FLetterOfIntentRepository {
    List<FLetterOfIntentModel> findByCanvasIdIn(List<Long> canvasIds);
}
