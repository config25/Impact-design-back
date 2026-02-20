package qtedu.Impact_design.api.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import qtedu.Impact_design.common.error.AuthorizationException;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.InvalidDateException;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.common.response.ErrorResponse;
import qtedu.Impact_design.common.response.HttpResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 공통 예외 처리 메서드
    private ResponseEntity<HttpResponse<ErrorResponse>> handleException(Exception e, ErrorCode errorCode, HttpStatus status) {
        log.info("ErrorCode: {}, Message: {}, Class: {}", errorCode.getCode(), errorCode.getMessage(), e.getStackTrace()[0].getClassName());
        return ResponseHelper.error(status, ErrorResponse.from(errorCode));
    }

    // 컨트롤러에서 필수 요청 파라미터가 누락되었을 때
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<HttpResponse<ErrorResponse>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return handleException(e, ErrorCode.VARIABLE_WRONG, HttpStatus.BAD_REQUEST);
    }

    // 잘못된 HTTP 메서드 요청 시 발생 (예: GET만 지원하는 API에 POST 요청)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<HttpResponse<ErrorResponse>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("HTTP Method Not Supported: {} is not supported. Supported: {}", e.getMethod(), e.getSupportedHttpMethods());
        return handleException(e, ErrorCode.PATH_WRONG, HttpStatus.BAD_REQUEST);
    }

    // 잘못된 URL로 요청했을 때
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<HttpResponse<ErrorResponse>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("HTTP URL Not Supported: {}", e.getRequestURL());
        return handleException(e, ErrorCode.PATH_WRONG, HttpStatus.BAD_REQUEST);
    }

    // 권한 예외 처리 -> Security 기본 응답(403 Forbidden)에 맡김
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException e) {
        log.error("Access Denied: {}", e.getMessage());
    }

    // 잘못된 메서드 인자(파라미터) 값이 넘어왔을 때
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<HttpResponse<ErrorResponse>> handleIllegalArgumentException(IllegalArgumentException e) {
        return handleException(e, ErrorCode.VARIABLE_WRONG, HttpStatus.BAD_REQUEST);
    }

    // 인증/인가 실패 시 발생(정의됨)
    @ExceptionHandler(AuthorizationException.class)
    protected ResponseEntity<HttpResponse<ErrorResponse>> handleAuthorizationException(AuthorizationException e) {
        return handleException(e, e.getErrorCode(), HttpStatus.UNAUTHORIZED);
    }

    // 요청 Body(JSON 등)가 파싱 불가할 때
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<HttpResponse<ErrorResponse>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("HttpMessageNotReadableException: method={}, url={}, remoteAddr={}, message={}",
                request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), e.getMessage());
        return handleException(e, ErrorCode.VARIABLE_WRONG, HttpStatus.BAD_REQUEST);
    }

    // 해당 사항을 찾을수 없음(정의됨)
    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<HttpResponse<ErrorResponse>> handleNotFoundException(NotFoundException e){
        return handleException(e, e.getErrorCode(), HttpStatus.NOT_FOUND);
    }

    // 비즈니스 충돌 예외(정의됨)
    @ExceptionHandler(ConflictException.class)
    protected ResponseEntity<HttpResponse<ErrorResponse>>  handleConflictException(ConflictException e) {
        return handleException(e, e.getErrorCode(), HttpStatus.CONFLICT);
    }

    // Spring Security가 인증 정보 부족할 때 던짐 (예: 토큰 없음, 로그인 안 함)
    @ExceptionHandler(InsufficientAuthenticationException.class)
    protected ResponseEntity<HttpResponse<ErrorResponse>> handleInsufficientAuthenticationException(HttpServletRequest req){
        System.out.println(">> GEH InsufficientAuthenticationException uri=" + req.getRequestURI());
        return handleException(new InsufficientAuthenticationException("Unauthorized"), ErrorCode.NOT_AUTHORIZED, HttpStatus.UNAUTHORIZED);
    }

    // 유효하지 않은 날짜 예외(정의됨)
    @ExceptionHandler(InvalidDateException.class)
    protected ResponseEntity<HttpResponse<ErrorResponse>> handleInvalidDateException(InvalidDateException e) {
        return handleException(e, e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    // 위에서 잡히지 않은 모든 예외를 최종적으로 처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<HttpResponse<ErrorResponse>> handleGeneric(Exception e) {
        log.error("예기치 않은 오류 발생: {}", e.getMessage(), e);
        return ResponseHelper.error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
    }





}