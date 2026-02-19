package qtedu.Impact_design.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;

@Service
@RequiredArgsConstructor
public class GameService {

    private final TbGameRepository tbGameRepository;

    public String getUserStep(Long userId) {
        return tbGameRepository.findStepByUserId(userId);
    }
}
