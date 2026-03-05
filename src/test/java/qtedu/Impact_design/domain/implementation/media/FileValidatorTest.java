package qtedu.Impact_design.domain.implementation.media;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.media.FileData;
import qtedu.Impact_design.domain.model.media.MediaType;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FileValidatorTest {

    private final FileValidator fileValidator = new FileValidator();

    private FileData createFileData(String name) {
        return FileData.of(new ByteArrayInputStream(new byte[0]), MediaType.IMAGE_PNG, name, 100);
    }

    @Test
    @DisplayName("validateFileNameCorrect - 정상 파일명이면 예외가 발생하지 않는다")
    void doesNotThrowForValidFileName() {
        FileData file = createFileData("test.png");

        assertDoesNotThrow(() -> fileValidator.validateFileNameCorrect(file));
    }

    @Test
    @DisplayName("validateFileNameCorrect - 파일명이 null이면 ConflictException이 발생한다")
    void throwsForNullFileName() {
        FileData file = createFileData(null);

        assertThatThrownBy(() -> fileValidator.validateFileNameCorrect(file))
                .isInstanceOf(ConflictException.class)
                .satisfies(e -> {
                    ConflictException ex = (ConflictException) e;
                    assert ex.getErrorCode() == ErrorCode.FILE_NAME_INCORRECT;
                });
    }

    @Test
    @DisplayName("validateFileNameCorrect - 파일명이 빈 문자열이면 ConflictException이 발생한다")
    void throwsForEmptyFileName() {
        FileData file = createFileData("");

        assertThatThrownBy(() -> fileValidator.validateFileNameCorrect(file))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("validateFilesNameCorrect - 모든 파일명이 정상이면 예외가 발생하지 않는다")
    void doesNotThrowForValidFileNames() {
        List<FileData> files = List.of(createFileData("a.png"), createFileData("b.jpg"));

        assertDoesNotThrow(() -> fileValidator.validateFilesNameCorrect(files));
    }

    @Test
    @DisplayName("validateFilesNameCorrect - 하나라도 파일명이 잘못되면 예외가 발생한다")
    void throwsIfAnyFileNameInvalid() {
        List<FileData> files = List.of(createFileData("good.png"), createFileData(null));

        assertThatThrownBy(() -> fileValidator.validateFilesNameCorrect(files))
                .isInstanceOf(ConflictException.class);
    }
}
