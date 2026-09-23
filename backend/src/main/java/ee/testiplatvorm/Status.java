package ee.testiplatvorm;

import lombok.Getter;

@Getter
public enum Status {
    STATUS_ACTIVE("A");

    private final String code;

    Status(String code) {
        this.code = code;
    }
}
