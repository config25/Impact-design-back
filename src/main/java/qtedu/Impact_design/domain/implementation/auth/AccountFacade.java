package qtedu.Impact_design.domain.implementation.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.util.security.JwtTokenUtil;
import qtedu.Impact_design.common.error.AuthorizationException;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.model.en.UserRole;
import qtedu.Impact_design.domain.repository.LoggedInRepository;
import qtedu.Impact_design.domain.repository.UserinfoRepository;
import qtedu.Impact_design.storage.jpaentity.LoggedIn;
import qtedu.Impact_design.storage.jpaentity.User;

@Component
@RequiredArgsConstructor
public class AccountFacade {

    private final UserinfoRepository userinfoRepository;
    private final LoggedInRepository loggedInRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    public JwtToken login(String loginId, String password) {
        User user = userinfoRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthorizationException(ErrorCode.WRONG_PASSWORD);
        }

        // STUDENT만 허용
        if (user.getUserRole() != UserRole.STUDENT) {
            throw new AuthorizationException(ErrorCode.ACCESS_DENIED);
        }

        return generateAndSaveToken(user);
    }

    @Transactional
    public JwtToken teacherLogin(String loginId, String password) {
        User user = userinfoRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthorizationException(ErrorCode.WRONG_PASSWORD);
        }

        // ADMIN, TEACHER만 허용
        if (user.getUserRole() != UserRole.ADMIN && user.getUserRole() != UserRole.TEACHER) {
            throw new AuthorizationException(ErrorCode.ACCESS_DENIED);
        }

        return generateAndSaveToken(user);
    }

    @Transactional
    public void signup(String loginId, String password, String code) {
        if (userinfoRepository.existsByLoginId(loginId)) {
            throw new ConflictException(ErrorCode.USER_ALREADY_CREATED);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = User.createStudent(loginId, encodedPassword, code);
        userinfoRepository.save(user);
    }

    @Transactional
    public void logout(UserId userId) {
        loggedInRepository.deleteByUserNo(userId.getId());
    }

    private JwtToken generateAndSaveToken(User user) {
        UserId userId = UserId.of(user.getUserId());
        JwtToken jwtToken = jwtTokenUtil.createJwtToken(userId);

        loggedInRepository.findByUserNo(userId.getId())
                .ifPresentOrElse(
                        loggedIn -> {
                            loggedIn.updateRefreshToken(
                                    jwtToken.getRefreshToken().getToken(),
                                    jwtToken.getRefreshToken().getExpiredAt()
                            );
                            loggedInRepository.save(loggedIn);
                        },
                        () -> {
                            LoggedIn newLoggedIn = LoggedIn.create(
                                    userId.getId(),
                                    jwtToken.getRefreshToken().getToken(),
                                    jwtToken.getRefreshToken().getExpiredAt()
                            );
                            loggedInRepository.save(newLoggedIn);
                        }
                );

        return jwtToken;
    }
}
