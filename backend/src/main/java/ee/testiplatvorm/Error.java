package ee.testiplatvorm;

import lombok.Getter;

@Getter
public enum Error {
    INCORRECT_CREDENTIALS("Vale emaili aadress või salasõna"),
    NO_PERMISSION("Sul puudub õigus testi luua");
    INCORRECT_CREDENTIALS("Vale emaili aadress või salasõna"),
    NO_TEST_ASSIGNMENT_FOR_THIS_USER("Kasutajale ei ole vastavat testi määratud");

    private final String message;

    Error(String message) {
        this.message = message;
    }
}
