package ee.testiplatvorm;

import lombok.Getter;

@Getter
public enum Status {
    STATUS_ACTIVE("A"),
    STATUS_INACTIVE("I");

    private final String code;

    Status(String code) {
        this.code = code;
    }
}
