package ee.testiplatvorm.controller.ask.dto;

import lombok.Builder;

/**
 * Response body for POST /api/ask.
 *
 * <ul>
 *   <li>{@code answer} – a human-readable summary produced by a second LLM call</li>
 * </ul>
 */
@Builder
public record AskResponse(

        String answer

) {}

