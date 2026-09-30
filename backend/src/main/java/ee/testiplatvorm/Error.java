package ee.testiplatvorm;

import lombok.Getter;

@Getter
public enum Error {
    INCORRECT_CREDENTIALS("Vale emaili aadress või salasõna"),
    NO_TEST_ASSIGNMENT_FOR_THIS_USER("Kasutajale ei ole vastavat testi määratud"),
    EMAIL_ALREADY_EXISTS("Selle e-posti aadressiga kasutaja on juba olemas");

    private final String message;

    Error(String message) {
        this.message = message;
    }
}
