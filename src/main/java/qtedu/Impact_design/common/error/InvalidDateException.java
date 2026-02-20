package qtedu.Impact_design.common.error;

import lombok.Getter;

@Getter
public class InvalidDateException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidDateException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
