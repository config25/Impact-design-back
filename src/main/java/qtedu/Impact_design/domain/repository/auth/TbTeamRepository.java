package qtedu.Impact_design.domain.repository.auth;

import qtedu.Impact_design.domain.model.team.TbTeamModel;

import java.util.List;

public interface TbTeamRepository {
    List<TbTeamModel> findByCode(String code);
}
