package qtedu.Impact_design.domain.implementation.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qtedu.Impact_design.domain.model.ai.AiModel;
import qtedu.Impact_design.domain.model.ai.AiRequest;
import qtedu.Impact_design.domain.model.ai.AiResponse;
import qtedu.Impact_design.domain.repository.ai.AiClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiClientProviderTest {

    @Test
    @DisplayName("모델의 provider와 일치하는 클라이언트를 반환한다")
    void returnsMatchingClient() {
        AiClient openaiClient = new FakeAiClient("openai");
        AiClientProvider factory = new AiClientProvider(List.of(openaiClient));

        AiClient result = factory.getClient(AiModel.GPT_4_1);

        assertThat(result.getProvider()).isEqualTo("openai");
    }

    @Test
    @DisplayName("여러 클라이언트 중 올바른 provider를 선택한다")
    void selectsCorrectProvider() {
        AiClient openaiClient = new FakeAiClient("openai");
        AiClient googleClient = new FakeAiClient("google");
        AiClientProvider factory = new AiClientProvider(List.of(openaiClient, googleClient));

        AiClient result = factory.getClient(AiModel.GPT_4_1_MINI);

        assertThat(result.getProvider()).isEqualTo("openai");
    }

    @Test
    @DisplayName("지원하지 않는 provider면 IllegalArgumentException이 발생한다")
    void throwsForUnsupportedProvider() {
        AiClient googleClient = new FakeAiClient("google");
        AiClientProvider factory = new AiClientProvider(List.of(googleClient));

        assertThatThrownBy(() -> factory.getClient(AiModel.GPT_4_1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 AI 모델");
    }

    private static class FakeAiClient implements AiClient {
        private final String provider;

        FakeAiClient(String provider) {
            this.provider = provider;
        }

        @Override
        public AiResponse chat(AiRequest request) {
            return null;
        }

        @Override
        public String getProvider() {
            return provider;
        }
    }
}
