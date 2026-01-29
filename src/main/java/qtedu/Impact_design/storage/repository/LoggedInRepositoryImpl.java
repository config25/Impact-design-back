package qtedu.Impact_design.storage.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.domain.repository.LoggedInRepository;
import qtedu.Impact_design.storage.jpaentity.LoggedIn;
import qtedu.Impact_design.storage.jparepository.LoggedInJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LoggedInRepositoryImpl implements LoggedInRepository {

    private final LoggedInJpaRepository loggedInJpaRepository;

    @Override
    public Optional<LoggedIn> findByUserNo(Long userNo) {
        return loggedInJpaRepository.findByUserNo(userNo);
    }

    @Override
    public LoggedIn save(LoggedIn loggedIn) {
        return loggedInJpaRepository.save(loggedIn);
    }

    @Override
    @Transactional
    public void deleteByUserNo(Long userNo) {
        loggedInJpaRepository.deleteByUserNo(userNo);
    }
}
