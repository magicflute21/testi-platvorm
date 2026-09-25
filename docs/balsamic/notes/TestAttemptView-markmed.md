# TestAttemptView — Balsamiq märkmed

## Vaate märkmed

```text
Roll: Kõik rollid (ainult kasutaja, kellele test on määratud)
Failinimi: TestAttemptView.vue
Frontend rada: /test-attempt?userTestId={id}

Vaatega seotud lisainfo:
Vaate avamisel laetakse GET /api/user-tests/{userTestId}/attempt kutsega korraga alla kõik testi küsimused koos vastusevariantidega ning kasutaja varem salvestatud vastused. Küsimuste vahel liikumine toimub ilma uute GET kutseteta. Kui kasutajal on juba vastuseid salvestatud, avatakse esimene vastamata küsimus.

Küsimusi näidatakse ükshaaval. "Question X of Y" ja progressiriba arvutatakse questions listi pikkusest. SINGLE_CHOICE ja TRUE_FALSE küsimuste juures kuvatakse raadionupud, MULTIPLE_CHOICE puhul märkeruudud.

Nupp "Previous" on esimese küsimuse juures mitteaktiivne. Nupp "Next" muutub aktiivseks, kui vähemalt üks vastus on valitud. Nupule "Next" vajutades saadetakse valitud vastus backendile PUT /api/user-tests/{userTestId}/questions/{questionId}/answer kutsega ja liigutakse järgmise küsimuse juurde.

Viimase küsimuse juures on "Next" asemel nupp "Lõpeta test": salvestatakse viimane vastus (PUT), seejärel saadetakse POST /api/user-tests/{userTestId}/complete ja kasutaja suunatakse vaatele /dashboard.
```

## API märkmed — GET /api/user-tests/{userTestId}/attempt

```text
API: GET /api/user-tests/{userTestId}/attempt

TestAttemptResponse.java
Response (200):
{
  "userTestId": 1,
  "testId": 1,
  "testName": "JavaScript Basics Test",
  "questions": [
    {
      "questionId": 1,
      "position": 1,
      "title": "What is a closure?",
      "description": "Select the best definition of a closure in JavaScript.",
      "questionTypeName": "SINGLE_CHOICE",
      "answers": [
        {
          "questionAnswerId": 1,
          "answerText": "A function that remembers its lexical scope"
        },
        {
          "questionAnswerId": 2,
          "answerText": "A loop that never ends"
        },
        {
          "questionAnswerId": 3,
          "answerText": "A CSS property"
        }
      ],
      "selectedQuestionAnswerIds": [1]
    },
    {
      "questionId": 2,
      "position": 2,
      "title": "Which of the following are JS primitive types?",
      "description": "Select all that are JavaScript primitive types.",
      "questionTypeName": "MULTIPLE_CHOICE",
      "answers": [
        {
          "questionAnswerId": 4,
          "answerText": "string"
        },
        {
          "questionAnswerId": 5,
          "answerText": "number"
        },
        {
          "questionAnswerId": 6,
          "answerText": "array"
        },
        {
          "questionAnswerId": 7,
          "answerText": "object"
        }
      ],
      "selectedQuestionAnswerIds": []
    }
  ]
}

API teenuse lisainfo:
Küsimused tulevad test_question tabelist, sorteerituna position järgi. Vastusevariantide juures ei tagastata õigsuse infot (correct_choice), et seda ei saaks frontendist välja lugeda. selectedQuestionAnswerIds on tühi list, kui küsimusele pole veel vastatud (result / test_question_result rida puudub).

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'userTestId' väärtusega: 123"
```

## API märkmed — PUT /api/user-tests/{userTestId}/questions/{questionId}/answer

```text
API: PUT /api/user-tests/{userTestId}/questions/{questionId}/answer

TestAnswerRequest.java
Request body:
{
  "questionAnswerIds": [1]
}

Response (200): NONE

API teenuse lisainfo:
Esimesel salvestamisel luuakse user_test'ile result rida (started_at = now, completed_at/status/score_total = NULL). Küsimuse varasem vastus kirjutatakse üle: vana test_question_result rida ja selle test_question_answer read asendatakse uutega. correct_answer = true ainult siis, kui valitud questionAnswerIds ühtivad täpselt küsimuse correct_choice = true variantidega.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'userTestId' väärtusega: 123"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'questionId' väärtusega: 123"
```

## API märkmed — POST /api/user-tests/{userTestId}/complete

```text
API: POST /api/user-tests/{userTestId}/complete

Response (200): NONE

API teenuse lisainfo:
Arvutatakse tulemus: score_total = õigesti vastatud küsimuste question.score summa, max_score = kõigi testi küsimuste score'i summa. Vastamata küsimused loetakse valeks. status = 'P', kui score_total / max_score * 100 >= test.pass_percent (round_score_up määrab ümardamise), muidu 'F'. Täidetakse completed_at = now ja questions_answered, ning user_test.status seatakse väärtusele 'C'.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'userTestId' väärtusega: 123"
```
