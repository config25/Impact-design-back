package qtedu.Impact_design.domain.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Bean(name = "ioExecutor")
    public ExecutorService ioExecutor() {
        return Executors.newFixedThreadPool(3);
    }
}