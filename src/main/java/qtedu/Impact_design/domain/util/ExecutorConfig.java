package qtedu.Impact_design.domain.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ExecutorConfig {

    // 1코어 / 힙 256MB 환경 기준 보수 설정.
    // - 풀 5: getReport(4작업) + 여유 1. CPU 1코어지만 작업 대부분 IO 대기(OpenAI/DB)라 의미 있음.
    // - 큐 50: 무제한 큐는 메모리 압박/OOM 위험. 작은 힙에서는 반드시 상한 필요.
    // - CallerRunsPolicy: 큐가 차면 호출 스레드(톰캣)가 직접 실행 → 자연스러운 백프레셔.
    //   예외/거부 없이 처리 지연으로 부하 전달.
    // - 스레드 이름 io-N: jstack/로그 분석 시 식별 용이.
    @Bean(name = "ioExecutor")
    public ExecutorService ioExecutor() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "io-" + counter.getAndIncrement());
                t.setDaemon(false);
                return t;
            }
        };

        return new ThreadPoolExecutor(
                5, 5,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(50),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}