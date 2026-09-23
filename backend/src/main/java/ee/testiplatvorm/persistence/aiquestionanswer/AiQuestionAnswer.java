package ee.testiplatvorm.persistence.aiquestionanswer;

import ee.testiplatvorm.persistence.aiquestion.AiQuestion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "ai_question_answer", schema = "testi_platvorm")
public class AiQuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ai_question_id", nullable = false)
    private AiQuestion aiQuestion;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "correct_position")
    private Integer correctPosition;

    @NotNull
    @Column(name = "status", nullable = false, length = Integer.MAX_VALUE)
    private String status;

    @Size(max = 255)
    @NotNull
    @Column(name = "answer_text", nullable = false)
    private String answerText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_question_answer_id")
    private AiQuestionAnswer aiQuestionAnswer;

    @NotNull
    @Column(name = "score", nullable = false)
    private Integer score;

    @NotNull
    @Column(name = "feedback", nullable = false)
    private Integer feedback;


}