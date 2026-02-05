package qtedu.Impact_design.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qtedu.Impact_design.api.dto.request.ai.AiChatRequest;
import qtedu.Impact_design.api.dto.response.ai.AiChatResponse;
import qtedu.Impact_design.api.util.ResponseHelper;
import qtedu.Impact_design.common.response.HttpResponse;
import qtedu.Impact_design.domain.model.ai.AiResponse;
import qtedu.Impact_design.domain.service.AiService;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    // 테스트용
    @PostMapping("/chat")
    public ResponseEntity<HttpResponse<AiChatResponse>> chat(
            @RequestBody AiChatRequest request
    ) {
        AiResponse response = aiService.chat(
                request.getModel(),
                request.getSystemPrompt(),
                request.getUserPrompt()
        );
        return ResponseHelper.success(AiChatResponse.from(response));
    }
}
