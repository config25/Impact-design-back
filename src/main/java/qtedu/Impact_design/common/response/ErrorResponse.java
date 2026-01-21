package qtedu.Impact_design.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import qtedu.Impact_design.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final String errorCode;
    private final String message;


    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }

}