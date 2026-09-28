# POST /api/tests/{testId}/complete — Balsamiq märkmed (lihtsustatud versioon)

Lihtsustatud (demo) versioon testi lõpetamisest: kõik vastused saadetakse korraga ühe päringuga ning salvestatakse ainult `result` rida. Küsimuste kaupa vastuste salvestamine (`test_question_result`, `test_question_answer`) lisatakse hiljem — vt täisversiooni kirjeldust failis `TestAttemptView-markmed.md`.

## API märkmed — POST /api/tests/{testId}/complete

```text
API: POST /api/tests/{testId}/complete

SubmittedAnswersDto.java
Request body:
[
  {
    "questionId": 1,
    "answerIds": [1]
  },
  {
    "questionId": 2,
    "answerIds": [4, 5]
  }
]

Response (200): NONE

API teenuse lisainfo:
Sisselogitud kasutaja user_test rida leitakse userId ja testId järgi. Küsimus on õige, kui answerIds ühtivad täpselt küsimuse question_answer.correct_choice = true variantidega (õigsust kontrollitakse backendis). Luuakse üks result rida: score_total = õigete küsimuste question.score summa, max_score = kõigi testi küsimuste score'i summa, status = 'P', kui score_total / max_score * 100 >= test.pass_percent, muidu 'F'; started_at = completed_at = now. user_test.status seatakse väärtusele 'C'. test_question_result ja test_question_answer ridu selles versioonis ei looda.

Veateated:
HTTP: 403
errorCode: NO_TEST_ASSIGNMENT_FOR_THIS_USER
message: "Kasutajale ei ole vastavat testi määratud"
```
