package ee.testiplatvorm.controller.aiquestion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AskRequest {
    // Kasutaja viimane sõnum - see on alati esmatähtis
    @NotBlank
    @Size(max = 500)
    private String instructions;
    // Varasemad sõnumid samast vestlusest (vanemad eespool), et AI saaks täpsustustest ja muudatustest aru
    @Valid
    @Size(max = 10)
    private List<AiChatMessageDto> previousMessages;
}
