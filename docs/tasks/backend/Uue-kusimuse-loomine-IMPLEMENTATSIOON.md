# Uue küsimuse loomine — implementatsiooni plaan

**Seotud task:** `docs/tasks/backend/Uue-kusimuse-loomine.md`

Allpool on kõik backendi teed antud baaspaketi `backend/src/main/java/ee/testiplatvorm/` suhtes.

## Hetkeseis (mis on juba olemas)

- `persistence/question/Question.java` — entiteet tabelile `question`. Seosed `competence`, `competenceLevel`, `questionType`, `createdBy` (User) on `@ManyToOne` objektid.
- `persistence/question/QuestionRepository.java` — olemas (`findQuestionsBy`), `save()` tuleb `JpaRepository`-st.
- `persistence/question/QuestionMapper.java` — olemas, aga ainult suunas entiteet → `QuestionResponseDto`. DTO → entiteet meetod puudub.
- `persistence/questionanswer/QuestionAnswer.java` — entiteet tabelile `question_answer` (`question`, `answerText`, `correctChoice`, `correctPosition`, `pairedAnswer`, `status`).
- `persistence/questionanswer/QuestionAnswerRepository.java` — olemas, `save()`/`saveAll()` tulevad `JpaRepository`-st.
- `persistence/questionanswer/QuestionAnswerMapper.java` — olemas, aga ainult suunas entiteet → `TestAttemptAnswerDto`.
- `persistence/QuestionType.java`, `persistence/QuestionTypeRepository.java` — olemas (vt ka `Kusimuse-tuupide-nimekirja-paring-IMPLEMENTATSIOON.md`, samm 1).
- `persistence/competencelevel/CompetenceLevel.java` + `CompetenceLevelRepository` — olemas, `CompetenceLevel` küljes on `competence`.
- `service/QuestionService.java` — olemas (`findQuestionsBy`), loomise meetodit pole.
- `service/UserService.java` — olemas `getValidUserBy(Integer userId)`.
- `service/CurrentUserService.java` — `getUserId()` võtab `userId` sessioonist, puudumisel viskab 401.
- `controller/question/QuestionController.java` — olemas (`GET /api/questions`), `POST` meetodit pole.
- `Error.java` — `NO_PERMISSION_TO_CREATE_QUESTIONS` on olemas. `INVALID_CORRECT_ANSWER_COUNT` puudub.
- `infrastructure/exception/BadRequestException.java` ja `RestExceptionHandler` (400) on olemas. `RestExceptionHandler.handleMethodArgumentNotValid` teeb valideerimisvigadest automaatselt `400 INCORRECT_INPUT`.
- **Eeskujud:**
  - `AiQuestionService.saveQuestion` teeb peaaegu sama asja AI küsimuste tabelitesse. Seal on olemas rolli kontroll (`getValidQuestionAuthorId`, private) ja õigete vastuste arvu kontroll (`isValidQuestion`, private).
  - `TestService.createTest` on sama mustriga POST: mapper DTO → entiteet, `@Transactional`, seosed ja süsteemsed väljad määratakse service'is.
  - `controller/aiquestion/dto/AiQuestionSaveRequest.java` ja `GeneratedAnswerDto.java` on sarnase kujuga request DTO-d koos valideerimisannotatsioonidega.
- Teste projektis veel pole (`backend/src/test/` puudub).

## Puuduv/muudetav

- `Error.java` — uus väärtus `INVALID_CORRECT_ANSWER_COUNT`
- `QuestionCreateRequestDto` + `QuestionAnswerCreateDto` — uued request DTO-d
- `QuestionMapper` — uus meetod DTO → `Question`
- `QuestionAnswerMapper` — uus meetod DTO → `QuestionAnswer`
- `CompetenceLevelService` — uus `getValidCompetenceLevelBy`
- `QuestionTypeService` — uus `getValidQuestionTypeBy` (teenus tekib küsimuse tüüpide taskiga)
- `QuestionService` — uus meetod `addQuestion` koos rolli ja õigete vastuste kontrolliga
- `QuestionController` — uus `POST /api/questions` meetod
- Testid

## Sammud

1. **Lisa uus veakood** — fail: `Error.java`
   ```java
   INVALID_CORRECT_ANSWER_COUNT("Õigete vastuste arv ei vasta küsimuse tüübile"),
   ```

2. **Loo vastusevariandi DTO** — fail: `controller/question/dto/QuestionAnswerCreateDto.java`
   - Väljad: `String answerText` (`@NotBlank`, `@Size(max = 255)`), `Boolean isCorrect` (`@NotNull`).
   - Kuju sama nagu `GeneratedAnswerDto`-l, aga ära kasuta seda uuesti, sest see kuulub AI ressursi paketti.

3. **Loo request DTO** — fail: `controller/question/dto/QuestionCreateRequestDto.java`
   - Väljanimed täpselt taski JSON-ist (leping frontendiga):
   ```java
   @NotNull private Integer competenceLevelId;
   @NotNull private Integer questionTypeId;
   @NotBlank @Size(max = 100) private String title;
   @NotBlank @Size(max = 1000) private String description;
   @NotNull @Positive private Integer score;
   @NotEmpty private List<@Valid QuestionAnswerCreateDto> answers;
   ```
   - `description` on andmebaasis NOT NULL, seega `@NotBlank`. See erineb `AiQuestionSaveRequest`-ist, kus see on valikuline.
   - Kas `score` peab olema positiivne, vt "Avatud küsimused".

4. **Lisa mapperisse DTO → entiteet** — fail: `persistence/question/QuestionMapper.java`
   ```java
   @Mapping(ignore = true, target = "id")
   @Mapping(source = "title", target = "title")
   @Mapping(source = "description", target = "description")
   @Mapping(source = "score", target = "score")
   @Mapping(ignore = true, target = "competence")
   @Mapping(ignore = true, target = "competenceLevel")
   @Mapping(ignore = true, target = "questionType")
   @Mapping(ignore = true, target = "status")
   @Mapping(ignore = true, target = "createdBy")
   @Mapping(ignore = true, target = "createdAt")
   @Mapping(ignore = true, target = "updatedAt")
   Question toQuestion(QuestionCreateRequestDto questionCreateRequestDto);
   ```
   - Seosed ja süsteemsed väljad määratakse service'is (sama muster nagu `TestMapper.toTest`).

5. **Lisa mapperisse vastusevariant** — fail: `persistence/questionanswer/QuestionAnswerMapper.java`
   ```java
   @Mapping(ignore = true, target = "id")
   @Mapping(source = "answerText", target = "answerText")
   @Mapping(source = "isCorrect", target = "correctChoice")
   @Mapping(ignore = true, target = "question")
   @Mapping(ignore = true, target = "correctPosition")
   @Mapping(ignore = true, target = "pairedAnswer")
   @Mapping(ignore = true, target = "status")
   QuestionAnswer toQuestionAnswer(QuestionAnswerCreateDto questionAnswerCreateDto);

   List<QuestionAnswer> toQuestionAnswers(List<QuestionAnswerCreateDto> questionAnswerCreateDtos);
   ```
   - Kontrolli Lomboki `Boolean isCorrect` getterit: see on `getIsCorrect()`, seega MapStruct leiab allika `isCorrect`. `GeneratedAnswerDto` töötab samamoodi.

6. **Lisa `getValid...By` meetodid** (backend/CLAUDE.md: `findById().orElseThrow()` käib `public getValid<Entiteet>By` meetodisse vastava service klassi all)
   - `service/CompetenceLevelService.java`:
     ```java
     public CompetenceLevel getValidCompetenceLevelBy(Integer competenceLevelId) {
         return competenceLevelRepository.findById(competenceLevelId)
                 .orElseThrow(() -> new PrimaryKeyNotFoundException("competenceLevelId", competenceLevelId));
     }
     ```
   - `service/QuestionTypeService.java` (tekib taskiga "Küsimuse tüüpide nimekirja päring"):
     ```java
     public QuestionType getValidQuestionTypeBy(Integer questionTypeId) {
         return questionTypeRepository.findById(questionTypeId)
                 .orElseThrow(() -> new PrimaryKeyNotFoundException("questionTypeId", questionTypeId));
     }
     ```
   - Kasutaja jaoks on `UserService.getValidUserBy(Integer userId)` juba olemas.

7. **Service'i meetod** — fail: `service/QuestionService.java`
   - Uued väljad: `CurrentUserService`, `UserService`, `CompetenceLevelService`, `QuestionTypeService`, `QuestionAnswerRepository`, `QuestionAnswerMapper`.
   - Voog:
   ```java
   @Transactional
   public Integer addQuestion(QuestionCreateRequestDto questionCreateRequestDto) {
       User user = getValidQuestionAuthor();
       CompetenceLevel competenceLevel = competenceLevelService.getValidCompetenceLevelBy(questionCreateRequestDto.getCompetenceLevelId());
       QuestionType questionType = questionTypeService.getValidQuestionTypeBy(questionCreateRequestDto.getQuestionTypeId());
       validateCorrectAnswerCount(questionCreateRequestDto.getAnswers(), questionType);

       Question question = questionMapper.toQuestion(questionCreateRequestDto);
       handleQuestionFields(question, competenceLevel, questionType, user);   // competence = competenceLevel.getCompetence(), status, createdAt/updatedAt
       questionRepository.save(question);

       saveQuestionAnswers(questionCreateRequestDto.getAnswers(), question);
       return question.getId();
   }
   ```
   - **`getValidQuestionAuthor()`** (private): `currentUserService.getUserId()` → `userService.getValidUserBy(userId)` → kui roll pole `ADMIN` ega `HALDUR`, siis `ForbiddenException(NO_PERMISSION_TO_CREATE_QUESTIONS...)`. Loogika on sama nagu `AiQuestionService.getValidQuestionAuthorId`-il (vt "Avatud küsimused" dubleerimise kohta).
   - **`validateCorrectAnswerCount(...)`** (private): loe kokku `isCorrect == true` vastused ja kontrolli tüübi järgi:
     - `SINGLE_CHOICE` → täpselt 1
     - `MULTIPLE_CHOICE` → vähemalt 1
     - `TRUE_FALSE` → `answers.size() == 2` ja täpselt 1 õige
     - Rikkumise korral `BadRequestException(INVALID_CORRECT_ANSWER_COUNT.getMessage(), INVALID_CORRECT_ANSWER_COUNT.name())`.
   - **`saveQuestionAnswers(...)`** (private): `questionAnswerMapper.toQuestionAnswers(...)`, igale reale `setQuestion(question)` ja `setStatus(STATUS_ACTIVE.getCode())`, siis `questionAnswerRepository.saveAll(...)`.
   - `status` tuleb `Status` enumist (`STATUS_ACTIVE`), mitte kõvakodeeritud `"A"`.
   - `@Transactional` tagab, et vea korral (nt vastuse salvestamisel) ei jää andmebaasi poolikut küsimust.
   - Meetodite järjekord: `public` enne, `private` pärast, väljakutsumise järjekorras.

8. **Controller** — fail: `controller/question/QuestionController.java`
   ```java
   @PostMapping("/api/questions")
   @Operation(summary = "Loob uue küsimuse (question tabel, status = 'A') koos vastusevariantidega (question_answer tabel). Luua saab ainult ADMIN või HALDUR rolliga kasutaja. Tagastab loodud questionId.")
   @ApiResponse(responseCode = "200", description = "OK")
   @ApiResponse(responseCode = "400", description = "Vigased sisendandmed (INCORRECT_INPUT) või õigete vastuste arv ei vasta tüübile (INVALID_CORRECT_ANSWER_COUNT)")
   @ApiResponse(responseCode = "401", description = "Kasutaja pole sisse logitud")
   @ApiResponse(responseCode = "403", description = "Kasutajal puudub õigus küsimusi luua")
   @ApiResponse(responseCode = "404", description = "competenceLevelId või questionTypeId ei leitud")
   public Integer addQuestion(@RequestBody @Valid QuestionCreateRequestDto questionCreateRequestDto) {
       Integer questionId = questionService.addQuestion(questionCreateRequestDto);
       return questionId;
   }
   ```

9. **Testid** — vt jaotist "Testid".

## Veakäsitlus

| Olukord | Kus | Kuidas |
|---|---|---|
| Kohustuslik väli puudub/tühi, liiga pikk tekst | DTO annotatsioonid + `@Valid` kontrolleris | `RestExceptionHandler.handleMethodArgumentNotValid` → `400 INCORRECT_INPUT`, midagi lisada pole vaja |
| Õigete vastuste arv ei vasta tüübile | `QuestionService.validateCorrectAnswerCount` | `BadRequestException` + `Error.INVALID_CORRECT_ANSWER_COUNT` → 400 |
| Sessioonis pole `userId`-d | `CurrentUserService.getUserId()` | olemasolev `ResponseStatusException(UNAUTHORIZED)` → 401 |
| Roll pole ADMIN/HALDUR | `QuestionService.getValidQuestionAuthor` | `ForbiddenException` + `Error.NO_PERMISSION_TO_CREATE_QUESTIONS` → 403 |
| Kasutajat ei leitud | `UserService.getValidUserBy` | `PrimaryKeyNotFoundException("userId", ...)` → 404 |
| `competenceLevelId` ei leitud | `CompetenceLevelService.getValidCompetenceLevelBy` | `PrimaryKeyNotFoundException("competenceLevelId", ...)` → 404 |
| `questionTypeId` ei leitud | `QuestionTypeService.getValidQuestionTypeBy` | `PrimaryKeyNotFoundException("questionTypeId", ...)` → 404 |

Kõik erindid visatakse service kihist ja `RestExceptionHandler` teeb neist `ApiError` vastuse (`message`, `errorCode`).

## Testid

Teste projektis veel pole, seega loo kaust `backend/src/test/java/ee/testiplatvorm/`.

- **Service'i ühiktestid** (`QuestionServiceTest`, Mockito):
  - edukas loomine: `question` salvestatakse õigete väljadega (`competence` tuleb kompetentsi tasemelt, `status = "A"`, `createdBy` = kasutaja) ja vastused `correctChoice`-iga; tagastatakse id
  - SINGLE_CHOICE 2 õige vastusega → `BadRequestException` (`INVALID_CORRECT_ANSWER_COUNT`)
  - SINGLE_CHOICE 0 õige vastusega → `BadRequestException`
  - MULTIPLE_CHOICE 0 õige vastusega → `BadRequestException`; 2 õigega → õnnestub
  - TRUE_FALSE 3 variandiga või 2 õigega → `BadRequestException`
  - KASUTAJA roll → `ForbiddenException`
  - olematu `competenceLevelId` / `questionTypeId` → `PrimaryKeyNotFoundException`
- **Kontrolleri test** (`@WebMvcTest(QuestionController.class)` + `MockMvc`):
  - korrektne body → `200` ja vastuses number
  - tühi `title` → `400`, `errorCode: INCORRECT_INPUT`, `message: "title: must not be blank"`
  - tühi `answers` list → `400 INCORRECT_INPUT`
- Soovi korral integratsioonitest päris andmebaasiga: pärast päringut on `question` tabelis uus rida ja `question_answer` tabelis sama palju ridu kui saadeti vastuseid.

## Avatud küsimused

1. **Rolli kontrolli dubleerimine:** sama loogika on juba private meetodina `AiQuestionService.getValidQuestionAuthorId`-is (ja sarnane `TestService.createTest`-is). Kas tõstame selle ühte kohta, nt `UserService.getValidQuestionAuthorBy(Integer userId)`, ja kasutame mõlemas teenuses? Või kopeerime selle taski jaoks `QuestionService`-sse?
2. **Õigete vastuste kontrolli dubleerimine:** `AiQuestionService.isValidQuestion` sisaldab osaliselt sama reeglit (aga TRUE_FALSE-il ei kontrolli see, et variante oleks täpselt 2). Kas teeme ühise meetodi või hoiame need eraldi?
3. **Vastusevariantide arv:** kas lisaks õigete vastuste arvule peab piirama ka variantide koguarvu (nt vähemalt 2 SINGLE/MULTIPLE_CHOICE puhul, nagu AI küsimustel `@Size(min = 2, max = 5)`)? Märkmetes ja taskis seda nõuet pole.
4. **`score` piirang:** kas punktid peavad olema positiivsed (`@Positive`)? Märkmetes on öeldud ainult, et väli on kohustuslik.
5. **Vastus `Integer` vs DTO:** task ja märkmed tagastavad paljalt `questionId` numbri (nagu `POST /api/ai-questions`). Kui tulevikus on vaja tagastada rohkem infot, tasub kaaluda objekti `{"questionId": 10}`. Praegu järgib plaan taski.
6. **`QuestionType` paigutus:** vt `Kusimuse-tuupide-nimekirja-paring-IMPLEMENTATSIOON.md` (samm 1). Seda taski on mõistlik teha **pärast** küsimuse tüüpide taski, sest `QuestionTypeService` tekib seal.
7. **backend/CLAUDE.md vs kood:** CLAUDE.md räägib `ErrorResponse` enumist, koodis on see `Error.java`. Plaan järgib koodi.
