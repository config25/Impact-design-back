package qtedu.Impact_design.domain.util;

@FunctionalInterface
public interface AsyncAction<T> {
    void execute(T item) throws Exception;
}