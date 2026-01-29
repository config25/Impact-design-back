package qtedu.Impact_design.storage.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.repository.UserinfoRepository;
import qtedu.Impact_design.storage.jpaentity.User;
import qtedu.Impact_design.storage.jparepository.UserinfoJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserinfoRepositoryImpl implements UserinfoRepository {

    private final UserinfoJpaRepository userinfoJpaRepository;

    @Override
    public Optional<User> findByLoginId(String loginId) {
        return userinfoJpaRepository.findByLoginId(loginId);
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return userinfoJpaRepository.existsByLoginId(loginId);
    }

    @Override
    public User save(User user) {
        return userinfoJpaRepository.save(user);
    }
}
