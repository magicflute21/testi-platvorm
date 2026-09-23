package ee.testiplatvorm;

import lombok.Getter;

@Getter
public enum Error {
    INCORRECT_CREDENTIALS("Vale emaili aadress või salasõna");

    private final String message;

    Error(String message) {
        this.message = message;
    }
}
