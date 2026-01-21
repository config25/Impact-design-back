package qtedu.Impact_design.common.error;


import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final ErrorCode errorCode;

    public ConflictException(ErrorCode errorCode) {
        super(errorCode.getMessage());   // RuntimeException 생성자에 "메시지" 전달
        this.errorCode = errorCode;
    }


}