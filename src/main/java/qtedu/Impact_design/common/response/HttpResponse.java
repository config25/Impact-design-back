package qtedu.Impact_design.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HttpResponse<T> {
    private final int status; // 200, 201, 409 등등등
    private final T data; // 성공 메시지 or 에러 메시지와 코드

}