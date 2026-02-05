package qtedu.Impact_design.storage.repository.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.ImpactCheckModel;
import qtedu.Impact_design.domain.repository.ImpactCheckRepository;
import qtedu.Impact_design.storage.jpaentity.ImpactCheck;
import qtedu.Impact_design.storage.jparepository.web.ImpactCheckJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ImpactCheckRepositoryImpl implements ImpactCheckRepository {

    private final ImpactCheckJpaRepository impactCheckJpaRepository;

    @Override
    public Optional<ImpactCheckModel> findByUserId(Long userId) {
        return impactCheckJpaRepository.findByUserId(userId)
                .map(this::toModel);
    }

    @Override
    public ImpactCheckModel save(ImpactCheckModel model) {
        ImpactCheck entity;
        if (model.getAnswerId() != null) {
            entity = impactCheckJpaRepository.findById(model.getAnswerId())
                    .orElseThrow(() -> new IllegalArgumentException("ImpactCheck not found: " + model.getAnswerId()));
            entity.update(
                    model.getQ1Score(), model.getQ2Score(), model.getQ3Score(), model.getQ4Score(),
                    model.getQ5Score(), model.getQ6Score(), model.getQ7Score(), model.getQ8Score(),
                    model.getQ9Score(), model.getQ10Score(), model.getQ11Score(), model.getQ12Score(),
                    model.getQ13Text(), model.getQ14Text(), model.getQ15Text(), model.getQ16Text()
            );
        } else {
            entity = ImpactCheck.builder()
                    .userId(model.getUserId())
                    .q1Score(model.getQ1Score())
                    .q2Score(model.getQ2Score())
                    .q3Score(model.getQ3Score())
                    .q4Score(model.getQ4Score())
                    .q5Score(model.getQ5Score())
                    .q6Score(model.getQ6Score())
                    .q7Score(model.getQ7Score())
                    .q8Score(model.getQ8Score())
                    .q9Score(model.getQ9Score())
                    .q10Score(model.getQ10Score())
                    .q11Score(model.getQ11Score())
                    .q12Score(model.getQ12Score())
                    .q13Text(model.getQ13Text())
                    .q14Text(model.getQ14Text())
                    .q15Text(model.getQ15Text())
                    .q16Text(model.getQ16Text())
                    .build();
        }
        ImpactCheck saved = impactCheckJpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public List<ImpactCheckModel> findByUserIdIn(List<Long> userIds) {
        return impactCheckJpaRepository.findByUserIdIn(userIds).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    private ImpactCheckModel toModel(ImpactCheck entity) {
        return ImpactCheckModel.builder()
                .answerId(entity.getAnswerId())
                .q1Score(entity.getQ1Score())
                .q2Score(entity.getQ2Score())
                .q3Score(entity.getQ3Score())
                .q4Score(entity.getQ4Score())
                .q5Score(entity.getQ5Score())
                .q6Score(entity.getQ6Score())
                .q7Score(entity.getQ7Score())
                .q8Score(entity.getQ8Score())
                .q9Score(entity.getQ9Score())
                .q10Score(entity.getQ10Score())
                .q11Score(entity.getQ11Score())
                .q12Score(entity.getQ12Score())
                .q13Text(entity.getQ13Text())
                .q14Text(entity.getQ14Text())
                .q15Text(entity.getQ15Text())
                .q16Text(entity.getQ16Text())
                .userId(entity.getUserId())
                .build();
    }
}
