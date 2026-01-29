package qtedu.Impact_design.domain.util;

@FunctionalInterface
public interface AsyncFunction<T, R> {
    R apply(T item) throws Exception;
}