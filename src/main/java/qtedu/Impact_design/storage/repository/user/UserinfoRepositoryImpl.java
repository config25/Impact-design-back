package qtedu.Impact_design.storage.repository.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;
import qtedu.Impact_design.storage.jpaentity.user.Userinfo;
import qtedu.Impact_design.storage.jparepository.user.UserinfoJpaRepository;

import qtedu.Impact_design.domain.implementation.teach.StudentWithTeamProjection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .writer(model.getWriter())
                .build();
        Userinfo saved = userinfoJpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<UserinfoModel> findByUserId(Long userId) {
        return userinfoJpaRepository.findById(userId)
                .map(this::toModel);
    }

    @Override
    public Optional<UserinfoModel> findWriterByUserIds(List<Long> userIds) {
        return userinfoJpaRepository.findFirstByUserIdInAndWriter(userIds, "1")
                .map(this::toModel);
    }

    @Override
    public List<UserinfoModel> findAllByUserIds(List<Long> userIds) {
        return userinfoJpaRepository.findAllById(userIds).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void setWriter(Long userId) {
        userinfoJpaRepository.setWriter(userId);
    }

    @Override
    public void clearWriterByUserIds(List<Long> userIds) {
        userinfoJpaRepository.clearWriterByUserIds(userIds);
    }

    @Override
    public List<StudentWithTeamProjection> findStudentsWithTeamByCode(String code) {
        return userinfoJpaRepository.findStudentsWithTeamByCode(code);
    }

    private UserinfoModel toModel(Userinfo entity) {
        return UserinfoModel.builder()
                .userId(entity.getUserId())
                .loginId(entity.getLoginId())
                .password(entity.getPassword())
                .userName(entity.getUserName())
                .code(entity.getCode())
                .userRole(entity.getUserRole())
                .writer(entity.getWriter())
                .build();
    }
}
