package qtedu.Impact_design.domain.implementation.teach;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class TeachValidator {

    private static final int MAX_TEAM_COUNT = 6;
    private static final int MAX_MEMBER_COUNT = 10;

    public void validateClassSaveInput(String name, Integer numTeam, Integer numMember) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("클래스 이름은 필수입니다.");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("클래스 이름은 100자 이하여야 합니다.");
        }
        if (numTeam == null || numTeam <= 0) {
            throw new IllegalArgumentException("팀 수는 1 이상이어야 합니다.");
        }
        if (numTeam > MAX_TEAM_COUNT) {
            throw new IllegalArgumentException("팀은 최대 " + MAX_TEAM_COUNT + "개까지 생성할 수 있습니다.");
        }
        if (numMember > MAX_MEMBER_COUNT) {
            throw new IllegalArgumentException("팀원은 최대 " + MAX_MEMBER_COUNT + "명까지 가능합니다.");
        }
    }

    public void validateClassUpdateInput(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("클래스 이름은 필수입니다.");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("클래스 이름은 100자 이하여야 합니다.");
        }
    }

    public void validateEnddate(String enddate) {
        if (enddate == null || enddate.isBlank()) {
            throw new IllegalArgumentException("종료일은 필수입니다.");
        }
        try {
            LocalDate parsed = LocalDate.parse(enddate);
            if (parsed.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("종료일은 현재 날짜 이후여야 합니다.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("종료일 형식이 올바르지 않습니다. (yyyy-MM-dd)");
        }
    }
}
