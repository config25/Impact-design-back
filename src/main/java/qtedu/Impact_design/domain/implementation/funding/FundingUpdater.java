package qtedu.Impact_design.domain.implementation.funding;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.repository.FLetterOfIntent2Repository;
import qtedu.Impact_design.domain.repository.FLetterOfIntentRepository;

@Component
@RequiredArgsConstructor
public class FundingUpdater {

    private final FLetterOfIntentRepository fLetterOfIntentRepository;
    private final FLetterOfIntent2Repository fLetterOfIntent2Repository;

    @Transactional
    public void submit(String canvasType, Long userId) {
        if (isBuildType(canvasType)) {
            if (fLetterOfIntentRepository.existsSubmittedByUserId(userId)) {
                throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
            }
            fLetterOfIntentRepository.submitAllByUserId(userId);
        } else {
            if (fLetterOfIntent2Repository.existsSubmittedByUserId(userId)) {
                throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
            }
            fLetterOfIntent2Repository.submitAllByUserId(userId);
        }
    }

    private boolean isBuildType(String canvasType) {
        return "build".equalsIgnoreCase(canvasType);
    }
}
