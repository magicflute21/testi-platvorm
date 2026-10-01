package ee.testiplatvorm.controller.aiquestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Üks varasem sõnum AI chati vestlusest: sender on "user" (küsimuste autor) või "ai".
 */
@Data
public class AiChatMessageDto {
    @NotBlank
    @Pattern(regexp = "user|ai")
    private String sender;
    @NotBlank
    @Size(max = 1000)
    private String text;
}
