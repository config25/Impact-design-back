package qtedu.Impact_design.storage.repository.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.domain.model.auth.LoggedInModel;
import qtedu.Impact_design.domain.repository.auth.LoggedInRepository;
import qtedu.Impact_design.storage.jpaentity.auth.LoggedIn;
import qtedu.Impact_design.storage.jparepository.auth.LoggedInJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LoggedInRepositoryImpl implements LoggedInRepository {

    private final LoggedInJpaRepository loggedInJpaRepository;

    @Override
    public Optional<LoggedInModel> findByUserId(Long userId) {
        return loggedInJpaRepository.findByUserId(userId)
                .map(this::toModel);
    }

    @Override
    public LoggedInModel save(LoggedInModel model) {
        LoggedIn entity;
        if (model.getLoggedInId() != null) {
            entity = loggedInJpaRepository.findById(model.getLoggedInId())
                    .orElseThrow(() -> new IllegalStateException("LoggedIn not found: " + model.getLoggedInId()));
            entity.updateRefreshToken(model.getRefreshToken(), model.getExpiredAt());
        } else {
            entity = LoggedIn.builder()
                    .userId(model.getUserId())
                    .refreshToken(model.getRefreshToken())
                    .expiredAt(model.getExpiredAt())
                    .build();
        }
        LoggedIn saved = loggedInJpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        loggedInJpaRepository.deleteByUserId(userId);
    }

    private LoggedInModel toModel(LoggedIn entity) {
        return LoggedInModel.builder()
                .loggedInId(entity.getLoggedInId())
                .refreshToken(entity.getRefreshToken())
                .expiredAt(entity.getExpiredAt())
                .userId(entity.getUserId())
                .build();
    }
}
