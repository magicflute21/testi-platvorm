package ee.testiplatvorm;

import lombok.Getter;

@Getter
public enum Status {
    STATUS_ACTIVE("A"),
    STATUS_OPEN("O"),
    STATUS_CLOSED("C");

    private final String code;

    Status(String code) {
        this.code = code;
    }
}
