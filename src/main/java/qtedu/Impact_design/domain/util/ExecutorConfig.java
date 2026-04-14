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

    // 1코어 / 힙 256MB / RAM 944MB 환경 기준.
    // - 풀 6: IO 대기 위주라 CPU 1코어여도 유효. 메모리 고려해 최소한으로.
    // - 큐 20: 작은 힙에서 OOM 방지용 상한.
    // - CallerRunsPolicy: 큐 포화 시 톰캣 스레드가 직접 실행 → 자연스러운 백프레셔.
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
                6, 6,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(20),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}