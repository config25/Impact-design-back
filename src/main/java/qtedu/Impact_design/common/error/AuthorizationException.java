package qtedu.Impact_design.common.error;

import lombok.Getter;

@Getter
public class AuthorizationException extends RuntimeException {

    private final ErrorCode errorCode;

    public AuthorizationException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // RuntimeException 생성자에 "메시지" 전달(실행화면에 에러출력)
        this.errorCode = errorCode;
    }



}