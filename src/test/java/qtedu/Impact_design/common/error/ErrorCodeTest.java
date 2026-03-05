package qtedu.Impact_design.common.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodeTest {

    @Test
    @DisplayName("from - 등록된 메시지로 조회하면 해당 ErrorCode를 반환한다")
    void returnsMatchingErrorCode() {
        ErrorCode result = ErrorCode.from("비밀번호가 틀렸습니다.");

        assertThat(result).isEqualTo(ErrorCode.WRONG_PASSWORD);
        assertThat(result.getCode()).isEqualTo("AUTH_4");
    }

    @Test
    @DisplayName("from - 등록되지 않은 메시지면 INTERNAL_SERVER_ERROR를 반환한다")
    void returnsInternalServerErrorForUnknownMessage() {
        ErrorCode result = ErrorCode.from("알 수 없는 에러");

        assertThat(result).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("from - 다양한 ErrorCode를 메시지로 조회할 수 있다")
    void looksUpVariousErrorCodes() {
        assertThat(ErrorCode.from("회원을 찾을 수 없음.")).isEqualTo(ErrorCode.USER_NOT_FOUND);
        assertThat(ErrorCode.from("팀을 찾을 수 없습니다.")).isEqualTo(ErrorCode.TEAM_NOT_FOUND);
        assertThat(ErrorCode.from("이미 가입된 사용자입니다.")).isEqualTo(ErrorCode.USER_ALREADY_CREATED);
        assertThat(ErrorCode.from("총 투자 금액이 1억을 초과할 수 없습니다.")).isEqualTo(ErrorCode.INVESTMENT_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("각 ErrorCode는 고유한 code와 message를 가진다")
    void hasCodeAndMessage() {
        assertThat(ErrorCode.NOT_AUTHORIZED.getCode()).isEqualTo("AUTH_1");
        assertThat(ErrorCode.NOT_AUTHORIZED.getMessage()).isEqualTo("인증되지 않았습니다.");
        assertThat(ErrorCode.GAME_NOT_FOUND.getCode()).isEqualTo("GAME_1");
    }
}
