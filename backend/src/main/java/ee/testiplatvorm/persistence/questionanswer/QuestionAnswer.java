package ee.testiplatvorm.persistence.questionanswer;

import ee.testiplatvorm.persistence.Question;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "question_answer", schema = "testi_platvorm")
public class QuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Size(max = 255)
    @NotNull
    @Column(name = "answer_text", nullable = false)
    private String answerText;

    @Column(name = "correct_choice")
    private Boolean correctChoice;

    @Column(name = "correct_position")
    private Integer correctPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paired_answer_id")
    private QuestionAnswer pairedAnswer;

    @NotNull
    @Column(name = "status", nullable = false, length = Integer.MAX_VALUE)
    private String status;


}