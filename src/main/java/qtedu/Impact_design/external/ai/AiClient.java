package qtedu.Impact_design.external.ai;

import qtedu.Impact_design.domain.model.ai.AiRequest;
import qtedu.Impact_design.domain.model.ai.AiResponse;

public interface AiClient {

    AiResponse chat(AiRequest request);

    String getProvider();
}
