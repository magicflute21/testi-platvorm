package ee.testiplatvorm.controller.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusRequest {
    @NotBlank
    @Pattern(regexp = "[API]", message = "lubatud väärtused on A (aktiivne), P (ootel) või I (kustutatud)")
    private String status;
}
