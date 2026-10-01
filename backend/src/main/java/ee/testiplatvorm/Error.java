package ee.testiplatvorm;

import lombok.Getter;

@Getter
public enum Error {
    INCORRECT_CREDENTIALS("Vale emaili aadress või salasõna"),
    NO_PERMISSION("Sul puudub õigus testi luua"),
    NO_PERMISSION_TO_CREATE_QUESTIONS("Sul puudub õigus küsimusi luua"),
    INVALID_AI_QUESTION("Küsimus ei vasta reeglitele (vastuste arv, õigete vastuste arv, koodinäite pikkus või vastuses on vihje õigele vastusele)"),
    NO_TEST_ASSIGNMENT_FOR_THIS_USER("Kasutajale ei ole vastavat testi määratud"),
    NO_RESULT_FOUND("Tulemust ei leitud");

    private final String message;

    Error(String message) {
        this.message = message;
    }
}
