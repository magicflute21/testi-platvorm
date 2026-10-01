# Küsimuse tüüpide nimekirja päring — implementatsiooni plaan

**Seotud task:** `docs/tasks/backend/Kusimuse-tuupide-nimekirja-paring.md`

Allpool on kõik backendi teed antud baaspaketi `backend/src/main/java/ee/testiplatvorm/` suhtes.

## Hetkeseis (mis on juba olemas)

- `persistence/QuestionType.java` — JPA entiteet tabelile `question_type` (`id`, `name`). **Asub otse `persistence/` all, mitte oma alampaketis** (vt "Avatud küsimused").
- `persistence/QuestionTypeRepository.java` — `JpaRepository<QuestionType, Integer>`, kohandatud meetodeid pole. Kasutusel `AiQuestionService`-is (`findAll()`, `findById()`).
- Eeskuju: `GET /api/competences` on sama mustriga nimekirja-teenus, kõik kihid on valmis:
  - `controller/competence/CompetenceController.java`
  - `controller/competence/dto/CompetenceResponseDto.java`
  - `service/CompetenceService.java`
  - `persistence/competence/CompetenceMapper.java`
- Puuduvad: mapper, response DTO, service ja controller. Endpointi `/api/question-types` koodibaasis pole.
- Teste projektis veel pole: kausta `backend/src/test/` pole olemas. Testisõltuvused (`spring-boot-starter-test`, `spring-boot-starter-webmvc-test`) on `build.gradle`-is olemas.

## Puuduv/muudetav

- (Soovituslik) `QuestionType` ja `QuestionTypeRepository` tõstmine alampaketti `persistence/questiontype/`
- `QuestionTypeResponseDto` — uus response DTO
- `QuestionTypeMapper` — uus MapStruct mapper
- `QuestionTypeService` — uus teenus
- `QuestionTypeController` — uus kontroller endpointiga `GET /api/question-types`
- Test endpointile

## Sammud

1. **(Soovituslik) Tõsta entiteet ja repositoorium oma alampaketti** — failid: `persistence/questiontype/QuestionType.java`, `persistence/questiontype/QuestionTypeRepository.java`
   - `docs/backend/projekti-struktuur.md` järgi on igal entiteedil oma alampakett (`persistence/<entiteet>/`).
   - IntelliJ-s: paremklõps klassil → Refactor → Move Class. Nii uuendatakse ka importid failides `persistence/question/Question.java` ja `service/AiQuestionService.java`.
   - Kui otsustate mitte tõsta, pane järgmise sammu mapper samuti `persistence/` alla entiteedi kõrvale.

2. **Loo response DTO** — fail: `controller/questiontype/dto/QuestionTypeResponseDto.java`
   - Väljad taski JSON-i järgi: `Integer questionTypeId`, `String questionTypeName`.
   - Lomboki annotatsioonid nagu `CompetenceResponseDto`-l (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`).
   - DTO-d kasutab ainult see ressurss, seega kuulub see `controller/questiontype/dto/`, mitte `controller/common/dto/`.

3. **Loo mapper** — fail: `persistence/questiontype/QuestionTypeMapper.java`
   - Sama kuju nagu `CompetenceMapper`:
   ```java
   @Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
   public interface QuestionTypeMapper {
       @Mapping(source = "id", target = "questionTypeId")
       @Mapping(source = "name", target = "questionTypeName")
       QuestionTypeResponseDto toQuestionTypeResponseDto(QuestionType questionType);

       List<QuestionTypeResponseDto> toQuestionTypeResponseDtos(List<QuestionType> questionTypes);
   }
   ```

4. **Repositoorium** — juba olemas, muudatust ei vaja.
   - Filtrit pole (tabelis puudub `status`), seega piisab `findAll()`-ist.
   - Kui tahate kindlat järjekorda id järgi, kasuta `questionTypeRepository.findAll(Sort.by("id"))`.

5. **Loo service** — fail: `service/QuestionTypeService.java`
   ```java
   public List<QuestionTypeResponseDto> findQuestionTypes() {
       List<QuestionType> questionTypes = questionTypeRepository.findAll();
       List<QuestionTypeResponseDto> questionTypeResponseDtos = questionTypeMapper.toQuestionTypeResponseDtos(questionTypes);
       return questionTypeResponseDtos;
   }
   ```
   - Muutujate nimed peegeldavad täistüüpi (backend/CLAUDE.md).
   - Sellesse teenusesse sobib ka `getValidQuestionTypeBy(Integer questionTypeId)`, mida vajab task "Uue küsimuse loomine" (vt `Uue-kusimuse-loomine-IMPLEMENTATSIOON.md`).

6. **Loo controller** — fail: `controller/questiontype/QuestionTypeController.java`
   - `@RestController`, `@RequiredArgsConstructor`, täielik tee `@GetMapping`-us (klassitasemel `@RequestMapping`-ut pole, nagu `CompetenceController`-is).
   ```java
   @GetMapping("/api/question-types")
   @Operation(summary = "Tagastatakse kõik küsimuse tüübid (question_type tabel). questionTypeName on koodinimi, nt SINGLE_CHOICE.")
   @ApiResponse(responseCode = "200", description = "OK")
   public List<QuestionTypeResponseDto> findQuestionTypes() {
       List<QuestionTypeResponseDto> questionTypeResponseDtos = questionTypeService.findQuestionTypes();
       return questionTypeResponseDtos;
   }
   ```

7. **Testid** — vt jaotist "Testid".

## Veakäsitlus

Kohandatud veaolukordi pole (märkmetes `Veateated: —`). Sisendeid pole, seega valideerimist ega `PrimaryKeyNotFoundException`-it pole vaja. Ootamatu viga (nt andmebaas maas) annab Springi vaikimisi 500 vastuse, selleks midagi lisada ei ole vaja.

## Testid

- Loo kaust `backend/src/test/java/ee/testiplatvorm/`. Projektis teste veel pole, seega tuleb teha esimene testiklass.
- **Kontrolleri test** (`@WebMvcTest(QuestionTypeController.class)` + `MockMvc`, `QuestionTypeService` mockituna):
  - `GET /api/question-types` → `200 OK`, JSON-is on väljad `questionTypeId` ja `questionTypeName`.
- **Service'i ühiktest** (Mockito, mockitud `QuestionTypeRepository` ja `QuestionTypeMapper`):
  - Repositooriumi tagastatud list jõuab mapperi kaudu vastusesse.
- Soovi korral integratsioonitest (`@SpringBootTest` + päris andmebaas), mis kontrollib, et tagastatakse 3 rida: `1 SINGLE_CHOICE`, `2 MULTIPLE_CHOICE`, `3 TRUE_FALSE`.

## Avatud küsimused

1. **`QuestionType` asukoht:** entiteet ja repositoorium on otse `persistence/` all, aga projekti struktuur nõuab alampaketti (`persistence/questiontype/`). Kas tõstame need koos selle taskiga ümber (samm 1)? Muudatus puudutab ka `Question.java` ja `AiQuestionService.java` importe.
2. **Järjekord:** kas nimekiri peab olema sorteeritud id järgi (`findAll(Sort.by("id"))`) või piisab andmebaasi vaikejärjekorrast?
