# Küsimuse tüüpide nimekirja päring

**Teenus:** `GET /api/question-types`

**Vaste balsamic mockupis:** modal "Loo uus küsimus" (`QuestionCreateForm.vue`), vt märkmed `docs/balsamic/notes/QuestionCreateForm-markmed.md`, sektsioon "API märkmed — GET /api/question-types". Mockupi PDF-i ja pilti (`docs/balsamic/pdf-images/`) veel pole, seega on task koostatud märkmete faili põhjal.

## Sisend

Teenusel puuduvad sisendid.

## Väljund

**Response (200 OK):** list kõigist küsimuse tüüpidest.

`QuestionTypeResponseDto.java`

```json
[
  {
    "questionTypeId": 1,
    "questionTypeName": "SINGLE_CHOICE"
  },
  ...
]
```

- `questionTypeId` — `question_type.id`
- `questionTypeName` — `question_type.name` koodinimena (nt `SINGLE_CHOICE`). Eestikeelse nime (nt "Ühe õige vastusega") kuvab frontend, backend seda ei tõlgi.

## Eesmärk

Modali "Loo uus küsimus" avamisel täidetakse selle päringuga rippmenüü "Küsimuse tüüp". Valitud tüübi `questionTypeId` saadetakse hiljem uue küsimuse loomisel (`POST /api/questions`) backendile. Tüübist sõltub ka, mitu õiget vastust saab küsimusele märkida, seega peab frontend teadma nii id-d kui koodinime.

## Seotud andmebaasi tabelid

Vt `docs/database/2_create.sql`.

### question_type

Küsimuse tüüpide klassifikaatortabel. Tabelis pole `status` veergu, seega tagastatakse kõik read.

```sql
CREATE TABLE question_type (
                               id serial  NOT NULL,
                               name varchar(20)  NOT NULL,
                               CONSTRAINT question_type_pk PRIMARY KEY (id)
);
```

Näidisandmed (`3_import.sql`):

| id | name |
|----|------|
| 1 | SINGLE_CHOICE |
| 2 | MULTIPLE_CHOICE |
| 3 | TRUE_FALSE |

Entiteet `persistence/QuestionType.java` ja `persistence/QuestionTypeRepository.java` on koodis juba olemas (kasutusel `AiQuestionService`-is).

## Veaolukorrad

| Olukord | Status code | Response body |
|---|---|---|
| Ootamatu serveriviga (nt andmebaas pole kättesaadav) | 500 Internal Server Error | Springi vaikimisi veavastus |

Märkmete järgi teenusel kohandatud veateateid pole (`Veateated: —`).

## Vastuvõtu kriteeriumid

- [ ] Endpoint `GET /api/question-types` on olemas
- [ ] Õnnestunud päring tagastab `200 OK` ja listi objektidest väljadega `questionTypeId`, `questionTypeName`
- [ ] Tagastatakse kõik 3 `question_type` tabeli rida (SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE) koos õigete id-dega
- [ ] `questionTypeName` tagastatakse koodinimena, nii nagu see on andmebaasis
- [ ] Endpoint on Swaggeris nähtav ja sellel on `@Operation` kirjeldus
- [ ] Endpointi jaoks on automaattest
