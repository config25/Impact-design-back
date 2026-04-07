package qtedu.Impact_design.external.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AiConfig {

    // OpenAI 호출용. 타임아웃이 없으면 응답 지연 시 워커 스레드가 무한 점유됨.
    // ReportFacade의 60s 타임아웃이 cancel(true)을 호출해도
    // RestTemplate은 블로킹 IO라 인터럽트로 끊을 수 없으므로, 여기서 직접 제한해야 함.
    // readTimeout(50s)은 ReportFacade 타임아웃(60s)보다 작게 설정 →
    // 큐 대기로 호출 시작이 지연되더라도 ReportFacade 타임아웃 이전에 백그라운드 스레드가 정리됨.
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(50));
        return new RestTemplate(factory);
    }
}
