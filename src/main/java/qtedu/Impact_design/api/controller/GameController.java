package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.api.util.security.CurrentUser;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.service.GameService;

import java.util.Map;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /**
     * 학생용 공개범위(step) 조회
     */
    @GetMapping("/step")
    public ResponseEntity<HttpResponse<Map<String, String>>> getUserStep(
            @CurrentUser UserId userId
    ) {
        String step = gameService.getUserStep(userId.getId());
        return ResponseHelper.success(Map.of("step", step));
    }
}
