# Uue küsimuse loomine

**Teenus:** `POST /api/questions`

**Vaste balsamic mockupis:** modal "Loo uus küsimus" (`QuestionCreateForm.vue`), vt märkmed `docs/balsamic/notes/QuestionCreateForm-markmed.md`, sektsioon "API märkmed — POST /api/questions". Mockupi PDF-i ja pilti (`docs/balsamic/pdf-images/`) veel pole, seega on task koostatud märkmete faili põhjal.

## Sisend

Sisendiks on request body. Sisselogitud kasutaja `userId` võetakse sessioonist (`CurrentUserService.getUserId()`), mitte request body'st.

`QuestionCreateRequestDto.java`

```json
{
  "competenceLevelId": 1,
  "questionTypeId": 1,
  "title": "Mis on sulund (closure)?",
  "description": "Vali JavaScripti sulundi kõige täpsem definitsioon.",
  "score": 10,
  "answers": [
    {
      "answerText": "Funktsioon, mis mäletab oma leksikaalset skoopi",
      "isCorrect": true
    },
    ...
  ]
}
```

| Väli | Tüüp | Kohustuslik | Selgitus |
|---|---|---|---|
| `competenceLevelId` | Integer | jah | `competence_level.id`; määrab nii kompetentsi kui taseme (vt selgitust tabeli all) |
| `questionTypeId` | Integer | jah | `question_type.id` |
| `title` | String | jah | max 100 märki (`question.title`) |
| `description` | String | jah | max 1000 märki (`question.description` on NOT NULL) |
| `score` | Integer | jah | punktid õige vastuse eest |
| `answers` | List | jah | vastusevariandid |
| `answers[].answerText` | String | jah | max 255 märki (`question_answer.answer_text`) |
| `answers[].isCorrect` | Boolean | jah | kas variant on õige (`question_answer.correct_choice`) |

Näidisväärtused on võetud `3_import.sql` küsimusest `id = 1` ja selle vastusest `id = 1`.

**Kuidas kompetents ja tase vormil valitakse:**
1. Kasutaja valib esmalt kompetentsi `competence` tabelist (`GET /api/competences`, nt `competenceId = 1` "JavaScript").
2. Seejärel valib ta taseme, mida pakutakse ainult valitud kompetentsi kohta `competence_level` tabelist (`GET /api/competence-levels?competenceId=1` tagastab nt `competenceLevelId = 1` "Algaja" ja `competenceLevelId = 2` "Kesktase").
3. Backendile saadetakse ainult valitud `competenceLevelId`. `competence_level` rida on alati seotud ühe kompetentsiga (`competence_level.competence_id`), seega saab backend kompetentsi sealt kätte ja salvestab selle väljale `question.competence_id`.

`competenceId` eraldi request body's ei ole, sest siis võiks frontend saata omavahel sobimatu paari (nt `competenceId = 2` SQL ja `competenceLevelId = 1`, mis kuulub JavaScriptile). Sama lahendus on kasutusel ka `POST /api/tests` ja `POST /api/ai-questions` juures.

## Väljund

**Response (200 OK):** loodud küsimuse `questionId` (Integer).

```json
10
```

Näites on `10`, sest `3_import.sql` järel on suurim `question.id` 9. Frontend kuvab eduka vastuse korral teate "Küsimus edukalt salvestatud". Tulevikus saab `/tests/new` vaade tagastatud id abil uue küsimuse kohe testi valitud küsimuste hulka lisada.

## Eesmärk

Admin või haldur loob modalis "Loo uus küsimus" käsitsi uue küsimuse koos vastusevariantidega. Modal avaneb vaatelt `/questions` ("Küsimuste pank") ja tulevikus ka `/tests/new` ("Testi koostamine"). Teenus salvestab küsimuse `question` tabelisse ja vastusevariandid `question_answer` tabelisse. Enne salvestamist kontrollitakse kasutaja rolli ja seda, et õigete vastuste arv vastaks küsimuse tüübile.

## Seotud andmebaasi tabelid

Vt `docs/database/2_create.sql`.

### question (kirjutamine)

```sql
CREATE TABLE question (
                          id serial  NOT NULL,
                          competence_id int  NOT NULL,
                          competence_level_id int  NOT NULL,
                          title varchar(100)  NOT NULL,
                          description varchar(1000)  NOT NULL,
                          question_type_id int  NOT NULL,
                          score int  NOT NULL,
                          status char(1)  NOT NULL,
                          created_at timestamp  NOT NULL,
                          created_by int  NOT NULL,
                          updated_at timestamp  NOT NULL,
                          CONSTRAINT question_pk PRIMARY KEY (id)
);
```

- `competence_id` võetakse leitud `competence_level` rea küljest (request body's `competenceId` pole)
- `status` = `'A'`
- `created_by` = sessioonist võetud `userId`
- `created_at`, `updated_at` = salvestamise hetk

### question_answer (kirjutamine)

```sql
CREATE TABLE question_answer (
                                 id serial  NOT NULL,
                                 question_id int  NOT NULL,
                                 answer_text varchar(255)  NOT NULL,
                                 correct_choice boolean  NULL,
                                 correct_position int  NULL,
                                 paired_answer_id int  NULL,
                                 status char(1)  NOT NULL,
                                 CONSTRAINT question_answer_pk PRIMARY KEY (id)
);
```

- Iga `answers` listi elemendi kohta üks rida
- `isCorrect` → `correct_choice`
- `status` = `'A'`
- `correct_position` ja `paired_answer_id` jäävad `NULL`-iks (neid selle taski küsimuse tüübid ei kasuta)

Näidisandmed (`3_import.sql`, küsimus 1):

| id | question_id | answer_text | correct_choice | status |
|---|---|---|---|---|
| 1 | 1 | Funktsioon, mis mäletab oma leksikaalset skoopi | true | A |
| 2 | 1 | Tsükkel, mis ei lõppe kunagi | false | A |
| 3 | 1 | CSS-i omadus | false | A |

### Lugemiseks kasutatavad tabelid

- `competence_level` — leitakse `competenceLevelId` järgi; sealt tuleb ka `competence`
- `question_type` — leitakse `questionTypeId` järgi (`1 SINGLE_CHOICE`, `2 MULTIPLE_CHOICE`, `3 TRUE_FALSE`)
- `user` + `role` — kasutaja rolli kontroll (`role` tabelis: `1 ADMIN`, `2 HALDUR`, `3 KASUTAJA`)

Tabeleid `ai_question` ja `ai_question_answer` see teenus ei puuduta: need on AI abil loodud küsimuste jaoks (`POST /api/ai-questions`).

### Õigete vastuste reeglid

| Küsimuse tüüp | Reegel |
|---|---|
| SINGLE_CHOICE | täpselt 1 õige vastus |
| MULTIPLE_CHOICE | vähemalt 1 õige vastus |
| TRUE_FALSE | täpselt 2 varianti ("Tõene", "Väär"), neist täpselt 1 õige |

## Veaolukorrad

| Olukord | Status code | Response body |
|---|---|---|
| Kohustuslik väli puudub või on tühi (nt `title` on `""`) | 400 Bad Request | `{"message": "title: must not be blank", "errorCode": "INCORRECT_INPUT"}` |
| Õigete vastuste arv ei vasta küsimuse tüübile | 400 Bad Request | `{"message": "Õigete vastuste arv ei vasta küsimuse tüübile", "errorCode": "INVALID_CORRECT_ANSWER_COUNT"}` |
| Kasutaja pole sisse logitud (sessioonis pole `userId`-d) | 401 Unauthorized | Springi vaikimisi veavastus (`CurrentUserService` viskab `ResponseStatusException`-i) |
| Kasutaja roll pole ADMIN ega HALDUR | 403 Forbidden | `{"message": "Sul puudub õigus küsimusi luua", "errorCode": "NO_PERMISSION_TO_CREATE_QUESTIONS"}` |
| `competenceLevelId` järgi rida ei leitud | 404 Not Found | `{"message": "Ei leidnud primary keyd 'competenceLevelId' väärtusega: 123", "errorCode": "PRIMARY_KEY_NOT_FOUND"}` |
| `questionTypeId` järgi rida ei leitud | 404 Not Found | `{"message": "Ei leidnud primary keyd 'questionTypeId' väärtusega: 123", "errorCode": "PRIMARY_KEY_NOT_FOUND"}` |
| Ootamatu serveriviga | 500 Internal Server Error | Springi vaikimisi veavastus |

`INVALID_CORRECT_ANSWER_COUNT` on uus veakood. See tuleb lisada `Error` enumisse ja visata `BadRequestException`-ina. `NO_PERMISSION_TO_CREATE_QUESTIONS` on `Error` enumis juba olemas.

## Vastuvõtu kriteeriumid

- [ ] Endpoint `POST /api/questions` on olemas ja võtab sisse `QuestionCreateRequestDto` request body
- [ ] Õnnestunud päring tagastab `200 OK` ja loodud küsimuse `questionId`
- [ ] `question` tabelisse tekib uus rida: `competence_id` tuleb kompetentsi taseme küljest, `status = 'A'`, `created_by` on sisselogitud kasutaja
- [ ] Iga vastusevariandi kohta tekib `question_answer` tabelisse rida, kus `correct_choice` vastab `isCorrect` väärtusele ja `status = 'A'`
- [ ] Salvestamine toimub ühes transaktsioonis: vea korral ei jää andmebaasi poolikut küsimust
- [ ] Puuduva/tühja kohustusliku välja korral tagastatakse `400 INCORRECT_INPUT`
- [ ] Vale õigete vastuste arvu korral tagastatakse `400 INVALID_CORRECT_ANSWER_COUNT` (kõik 3 tüüpi)
- [ ] KASUTAJA rolliga kasutaja saab `403 NO_PERMISSION_TO_CREATE_QUESTIONS`
- [ ] Olematu `competenceLevelId` või `questionTypeId` korral tagastatakse `404 PRIMARY_KEY_NOT_FOUND`
- [ ] Endpoint on Swaggeris nähtav, `@Operation` ja `@ApiResponse` kirjeldustega (200, 400, 401, 403, 404)
- [ ] Endpointi jaoks on automaattestid (edukas loomine + veaolukorrad)
