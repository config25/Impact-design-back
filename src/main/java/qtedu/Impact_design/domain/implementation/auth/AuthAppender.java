package qtedu.Impact_design.domain.implementation.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

@Component
@RequiredArgsConstructor
public class AuthAppender {

    private final UserinfoRepository userinfoRepository;
    private final TeamUserRepository teamUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(String loginId, String password, String code, Integer teamId) {
        String encodedPassword = passwordEncoder.encode(password);
        UserinfoModel userinfo = UserinfoModel.createStudent(loginId, encodedPassword, code);
        UserinfoModel savedUserinfo = userinfoRepository.save(userinfo);

        TeamUserModel teamUser = TeamUserModel.createStudent(savedUserinfo.getUserId(), teamId);
        teamUserRepository.save(teamUser);
    }
}
