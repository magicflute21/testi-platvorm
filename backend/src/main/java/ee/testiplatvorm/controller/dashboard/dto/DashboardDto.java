package ee.testiplatvorm.controller.dashboard.dto;

import lombok.Data;

@Data
public class DashboardDto {
    private Long assignedTestCount;
    private Long recentTestCount;
    private Long recentQuestionCount;
    private Long pendingAiQuestionCount;
    private Integer recentDays;
}
