package qtedu.Impact_design.domain.implementation.impactcheck;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.request.impactcheck.ImpactCheckRequest;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.ImpactCheckModel;
import qtedu.Impact_design.domain.repository.ImpactCheckRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ImpactCheckAppender {

    private final ImpactCheckRepository impactCheckRepository;

    @Transactional
    public ImpactCheckResponse append(Long userId, ImpactCheckRequest request) {
        Optional<ImpactCheckModel> existing = impactCheckRepository.findByUserId(userId);

        Long existingId = existing.map(ImpactCheckModel::getAnswerId).orElse(null);

        ImpactCheckModel model = ImpactCheckModel.builder()
                .answerId(existingId)
                .userId(userId)
                .q1Score(request.getQ1Score())
                .q2Score(request.getQ2Score())
                .q3Score(request.getQ3Score())
                .q4Score(request.getQ4Score())
                .q5Score(request.getQ5Score())
                .q6Score(request.getQ6Score())
                .q7Score(request.getQ7Score())
                .q8Score(request.getQ8Score())
                .q9Score(request.getQ9Score())
                .q10Score(request.getQ10Score())
                .q11Score(request.getQ11Score())
                .q12Score(request.getQ12Score())
                .q13Text(request.getQ13Text())
                .q14Text(request.getQ14Text())
                .q15Text(request.getQ15Text())
                .q16Text(request.getQ16Text())
                .build();

        ImpactCheckModel saved = impactCheckRepository.save(model);
        return ImpactCheckResponse.from(saved);
    }

    @Transactional
    public void submit(Long userId) {
        if (impactCheckRepository.existsSubmittedByUserId(userId)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }
        impactCheckRepository.submitByUserId(userId);
    }
}
