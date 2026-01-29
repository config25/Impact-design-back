package qtedu.Impact_design.domain.util;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Component
public class AsyncJobExecutor {

    private final ExecutorService ioExecutor;

    public AsyncJobExecutor(@Qualifier("ioExecutor") ExecutorService ioExecutor) {
        this.ioExecutor = ioExecutor;
    }

    public <T> void executeAsyncJobs(List<T> items, AsyncAction<T> action) throws InterruptedException, ExecutionException {
        List<CompletableFuture<Void>> futures = items.stream()
                .map(item -> CompletableFuture.runAsync(() -> {
                    try {
                        action.execute(item);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, ioExecutor))
                .collect(Collectors.toList());

        // Wait for all to complete
        for (CompletableFuture<Void> future : futures) {
            future.get();
        }
    }

    public <T> void executeAsyncJob(T item, AsyncAction<T> action) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                action.execute(item);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, ioExecutor);

        future.get();
    }

    public <T, R> R executeAsyncReturnJob(T item, AsyncFunction<T, R> action) throws ExecutionException, InterruptedException {
        CompletableFuture<R> future = CompletableFuture.supplyAsync(() -> {
            try {
                return action.apply(item);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, ioExecutor);

        return future.get();
    }
}