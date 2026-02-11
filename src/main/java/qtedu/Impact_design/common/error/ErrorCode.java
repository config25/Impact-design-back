package qtedu.Impact_design.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // AUTH
    NOT_AUTHORIZED("AUTH_1", "인증되지 않았습니다."),
    TOKEN_EXPIRED("AUTH_2", "토큰이 만료되었습니다."),
    INVALID_TOKEN("AUTH_3", "토큰을 확인해주세요"),
    WRONG_PASSWORD("AUTH_4", "비밀번호가 틀렸습니다."),
    ACCESS_DENIED("AUTH_5", "권한이 없습니다."),

    // Common
    PATH_WRONG("COMMON_1", "잘못된 메세드입니다."), // 코드, 메시지 순
    VARIABLE_WRONG("COMMON_2", "요청 변수가 잘못되었습니다."),
    WRONG_ACCESS("COMMON_3", "잘못된 접근입니다."),
    INTERNAL_SERVER_ERROR("COMMON_4", "Internal Server Error"),


    // File
    FILE_UPLOAD_FAILED("FILE_1", "파일 업로드를 실패하였습니다."),
    FILE_DELETE_FAILED("FILE_2", "파일 삭제를 실패하였습니다."),
    FILE_CONVERT_FAILED("FILE_3", "파일 변환에 실패하였습니다."),
    FILE_NAME_COULD_NOT_EMPTY("FILE_4", "파일 이름이 없습니다"),
    NOT_SUPPORT_FILE_TYPE("FILE_5", "지원하지 않는 형식의 파일입니다."),
    FILE_NAME_INCORRECT("FILE_6", "파일 이름이 잘못되었습니다."),


    // User
    USER_NOT_FOUND("USER_1", "회원을 찾을 수 없음."),
    USER_NOT_ACCESS("USER_2", "사용자가 활성화되지 않았습니다."),
    USER_ALREADY_CREATED("USER_3", "이미 가입된 사용자입니다."),
    USER_NOT_CREATED("USER_4", "가입되지 않은 사용자입니다."),
    USER_PUSH_TOKEN_NOT_FOUND("USER_5", "푸시 토큰을 찾을 수 없음."),
    USER_NICKNAME_EXISTS("USER_6", "이미 존재하는 유저 닉네임입니다."),

    // Team
    CODE_NOT_FOUND("TEAM_1", "코드가 존재하지 않습니다."),
    TEAM_NOT_FOUND("TEAM_2", "팀을 찾을 수 없습니다."),

    // Game
    GAME_NOT_FOUND("GAME_1", "게임을 찾을 수 없습니다."),

    // Canvas
    ALREADY_SUBMITTED("CANVAS_1", "이미 제출 완료된 캔버스입니다.");




    private final String code;
    private final String message;

    private static final Map<String, ErrorCode> ERROR_CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values())
                    .collect(Collectors.toMap(ErrorCode::getMessage, Function.identity())));
    // 맵 만들어 반환하기 위함
    // 예시
//    {
//        "잘못된 요청입니다.": ErrorCode.BAD_REQUEST,
//    }


    public static ErrorCode from(final String message){
        if(ERROR_CODE_MAP.containsKey(message)){
            return ERROR_CODE_MAP.get(message);
        }// 정의된 에러 처리

        return ErrorCode.INTERNAL_SERVER_ERROR; // 그 외는 서버 내부에러로 간주

    }




}