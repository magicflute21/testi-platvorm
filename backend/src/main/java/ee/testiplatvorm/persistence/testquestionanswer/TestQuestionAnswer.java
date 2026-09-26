package ee.testiplatvorm.persistence.testquestionanswer;

import ee.testiplatvorm.persistence.TestQuestionResult;
import ee.testiplatvorm.persistence.questionanswer.QuestionAnswer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "test_question_answer", schema = "testi_platvorm")
public class TestQuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_question_result_id", nullable = false)
    private TestQuestionResult testQuestionResult;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_answer_id", nullable = false)
    private QuestionAnswer questionAnswer;

    @Column(name = "correct_choice_user_answer")
    private Boolean correctChoiceUserAnswer;

    @Column(name = "correct_position_user_answer")
    private Integer correctPositionUserAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paired_answer_id_user_answer")
    private QuestionAnswer pairedAnswerIdUserAnswer;
}