package qtedu.Impact_design.storage.repository.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;
import qtedu.Impact_design.storage.jpaentity.user.Userinfo;
import qtedu.Impact_design.storage.jparepository.user.UserinfoJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserinfoRepositoryImpl implements UserinfoRepository {

    private final UserinfoJpaRepository userinfoJpaRepository;

    @Override
    public Optional<UserinfoModel> findByLoginId(String loginId) {
        return userinfoJpaRepository.findByLoginId(loginId)
                .map(this::toModel);
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return userinfoJpaRepository.existsByLoginId(loginId);
    }

    @Override
    public UserinfoModel save(UserinfoModel model) {
        Userinfo entity = Userinfo.builder()
                .userId(model.getUserId())
                .loginId(model.getLoginId())
                .password(model.getPassword())
                .userName(model.getUserName())
                .code(model.getCode())
                .userRole(model.getUserRole())
                .build();
        Userinfo saved = userinfoJpaRepository.save(entity);
        return toModel(saved);
    }

    private UserinfoModel toModel(Userinfo entity) {
        return UserinfoModel.builder()
                .userId(entity.getUserId())
                .loginId(entity.getLoginId())
                .password(entity.getPassword())
                .userName(entity.getUserName())
                .code(entity.getCode())
                .userRole(entity.getUserRole())
                .build();
    }
}
