package qtedu.Impact_design.domain.implementation.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.model.ai.AiModel;
import qtedu.Impact_design.domain.repository.ai.AiClient;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AiClientFactory {

    private final List<AiClient> aiClients;

    public AiClient getClient(AiModel model) {
        Map<String, AiClient> clientMap = aiClients.stream()
                .collect(Collectors.toMap(AiClient::getProvider, Function.identity()));

        AiClient client = clientMap.get(model.getProvider());
        if (client == null) {
            throw new IllegalArgumentException("지원하지 않는 AI 모델입니다: " + model);
        }
        return client;
    }
}
