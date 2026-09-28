package ee.testiplatvorm;

import lombok.Getter;

@Getter
public enum Status {
    STATUS_ACTIVE("A"),
    STATUS_OPEN("O"),
    STATUS_CLOSED("C"),
    STATUS_PASSED("P"),
    STATUS_FAILED("F");

    private final String code;

    Status(String code) {
        this.code = code;
    }
}
