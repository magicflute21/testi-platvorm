package ee.testiplatvorm.persistence;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "result", schema = "testi_platvorm")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_test_id", nullable = false)
    private UserTest userTest;

    @NotNull
    @Column(name = "status", nullable = false, length = Integer.MAX_VALUE)
    private String status;

    @NotNull
    @Column(name = "max_score", nullable = false)
    private Integer maxScore;

    @NotNull
    @Column(name = "score_total", nullable = false)
    private Integer scoreTotal;

    @NotNull
    @Column(name = "completed_at", nullable = false)
    private OffsetDateTime completedAt;

    @NotNull
    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @NotNull
    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @NotNull
    @Column(name = "questions_answered", nullable = false)
    private Integer questionsAnswered;


}