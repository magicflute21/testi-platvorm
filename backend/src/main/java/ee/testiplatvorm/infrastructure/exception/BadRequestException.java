package ee.testiplatvorm.infrastructure.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final String message;
    private final String errorCode;

    public BadRequestException(String message, String errorCode) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }
}
