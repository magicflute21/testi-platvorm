package ee.testiplatvorm.infrastructure.exception;

import lombok.Getter;

@Getter
public class TooManyRequestsException extends RuntimeException {
    private final String message;
    private final String errorCode;

    public TooManyRequestsException(String message, String errorCode) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }
}
