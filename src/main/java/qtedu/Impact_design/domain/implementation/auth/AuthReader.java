package qtedu.Impact_design.domain.implementation.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.auth.TeamInfoResponse;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthReader {

    private final UserinfoRepository userinfoRepository;
    private final TbTeamRepository tbTeamRepository;

    public UserinfoModel findUserByLoginId(String loginId) {
        return userinfoRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    log.warn("로그인 실패 - 존재하지 않는 loginId: {}", loginId);
                    return new NotFoundException(ErrorCode.USER_NOT_FOUND);
                });
    }

    public List<TeamInfoResponse> checkCode(String code) {
        List<TbTeamModel> teams = tbTeamRepository.findByCode(code);
        if (teams.isEmpty()) {
            throw new NotFoundException(ErrorCode.CODE_NOT_FOUND);
        }
        return teams.stream()
                .map(TeamInfoResponse::from)
                .collect(Collectors.toList());
    }
}
