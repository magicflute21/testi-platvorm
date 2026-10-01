# QuestionCreateForm.vue — Balsamiq märkmed

Modaalaken "Loo uus küsimus". Modalil pole oma route'i: selle avab vaate nupp. Praegu on see vaates `/questions` ("Küsimuste pank") nupp "Lisa uus küsimus +", tulevikus ka `/tests/new` ("Testi koostamine") küsimuste real olev nupp "+". Modal koosneb kahest komponendist: `BaseModal.vue` on kogu projektis kasutatav üldine modali põhi ja `QuestionCreateForm.vue` on küsimuse loomise vorm, mis on BaseModali sees. Vorm teeb 4 backend kutset.

**Lahtine küsimus (tuleb hiljem üle vaadata):** vastusevariantide ja õige vastuse valimise kasutajaliides. Praegu kirjutatakse variandid ülal ja õige vastus valitakse eraldi all. Võib-olla on lihtsam märkida õige vastus otse iga variandi real (nt linnuke või raadionupp). Märkmed kirjeldavad praegu mockupil olevat lahendust (variant A). Allpool on teine võimalus (variant B).

### Variant B — õige vastus märgitakse otse variandi real

Selle variandi puhul pole vormil eraldi "Õige vastus" plokki. Iga vastusevariandi rea ees on märkeväli ja märgitud variant on õige. Backendile saadetav request body ei muutu: märgitud rea puhul on `isCorrect: true`, märkimata rea puhul `isCorrect: false`, ning see salvestatakse `question_answer.correct_choice` väljale.

Kui see variant valitakse, asendab järgmine lõik `QuestionCreateForm.vue` vaate märkmetes lõigu "Väljad: ..." ja reeglid "Õigete vastuste arv sõltub küsimuse tüübist":

```text
Väljad: Pealkiri, Kirjeldus, Kompetents, Tase, Küsimuse tüüp, Punktid (mitu punkti saab õige vastuse eest) ja Vastusevariandid. Nupp "+" lisab uue vastusevariandi rea ja "x" eemaldab rea. Õige vastus märgitakse otse vastusevariandi rea ees oleva märkeväljaga (märgitud = õige, märkimata = vale).

Märkevälja tüüp sõltub küsimuse tüübist:
- SINGLE_CHOICE (üks õige vastus): iga rea ees on raadionupp, seega saab õigeks märkida ainult ühe variandi.
- MULTIPLE_CHOICE (mitu õiget vastust): iga rea ees on linnuke (checkbox) ja märkida saab nii mitu varianti kui tahes (vähemalt ühe).
- TRUE_FALSE (tõene/väär): kaks ette antud rida, "Tõene" ja "Väär", raadionupuga. Ridu lisada ega eemaldada ei saa.
Kui küsimuse tüüpi muudetakse, tühjendatakse märgitud õiged vastused.
```

**Uus veakood:** `INVALID_CORRECT_ANSWER_COUNT` tuleb backendis lisada `Error` enumisse (visatakse `BadRequestException`-ina → HTTP 400).

## Vaate märkmed — BaseModal.vue

```text
Roll: Kõik rollid (sõltub vaatest, mis modali avab)
Failinimi: BaseModal.vue
Frontend rada: — (modaalaken, oma route'i pole)

Vaatega seotud lisainfo:
Üldine modali põhi, mida saab kasutada kogu projektis. Modal kuvatakse vaate peal ja taust tumeneb. Avav vaade annab modalile pealkirja (nt "Loo uus küsimus") ning sisu, mis pannakse modali sisse (slot, nt QuestionCreateForm.vue).

Modal avaneb, kui vaates vajutatakse seda avavat nuppu või ikooni. Modal sulgub, kui sisu komponent annab märku, et töö on tehtud või tühistatud. Modal ise API kutseid ei tee.
```

## Vaate märkmed — QuestionCreateForm.vue

```text
Roll: Admin, Haldur
Failinimi: QuestionCreateForm.vue
Frontend rada: — (avaneb BaseModal.vue sees vaadetelt /questions ja tulevikus /tests/new)

Vaatega seotud lisainfo:
Küsimusi saavad luua ainult ADMIN ja HALDUR rolliga kasutajad (sessionStorage'is olev roleName). Tavakasutaja (KASUTAJA) ei pääse küsimuste pangale ligi ega näe modali avamise nuppu.

Vormi avamisel laetakse kompetentsid (GET /api/competences) ja küsimuse tüübid (GET /api/question-types). Rippmenüü "Tase" muutub aktiivseks alles pärast kompetentsi valimist (GET /api/competence-levels). Kui kompetentsi muudetakse, tühjendatakse valitud tase.

Väljad: Pealkiri, Kirjeldus, Kompetents, Tase, Küsimuse tüüp, Punktid (mitu punkti saab õige vastuse eest), Vastusevariandid ja Õige vastus. Nupp "+" lisab uue vastusevariandi rea ja "x" eemaldab rea. Rippmenüüs "Õige vastus" saab valida ainult juba sisestatud vastusevariante.

Õigete vastuste arv sõltub küsimuse tüübist:
- SINGLE_CHOICE (üks õige vastus): õigeks saab märkida ainult ühe variandi, "Õige vastus" rea "+" nupp on peidetud.
- MULTIPLE_CHOICE (mitu õiget vastust): "Õige vastus" rea "+" lisab veel ühe õige vastuse valiku, õigeid võib olla kui palju tahes.
- TRUE_FALSE (tõene/väär): vastusevariandid on ette antud ("Tõene" ja "Väär") ning neid lisada ega eemaldada ei saa. Õigeks märgitakse üks neist.
Kui küsimuse tüüpi muudetakse, tühjendatakse õige vastuse valik. Neid reegleid kontrollib vorm frontendis. Rippmenüüs "Küsimuse tüüp" kuvatakse tüübi eestikeelne nimi (nt MULTIPLE_CHOICE → "Mitu õiget vastust"), mille tõlgib frontend.

Kõik väljad on kohustuslikud. Kui mõni väli on täitmata, kuvatakse AlertDanger.vue komponendiga teade "Täida kõik väljad". Kui backend vastab veaga, kuvatakse samas AlertDanger.vue's backendi message väli.

Nupule "Loo küsimus" vajutades kogutakse vormi andmed ja saadetakse backendile POST /api/questions sõnumiga. Kui salvestamine õnnestub, kuvatakse AlertSuccess.vue komponendiga teade "Küsimus edukalt salvestatud" ja modal suletakse. Nupule "Tühista" vajutades modal suletakse (ilma API kutseta).
```

## API märkmed — GET /api/competences

```text
API: GET /api/competences

CompetenceResponseDto.java
Response (200):
[
  {
    "competenceId": 1,
    "competenceName": "JavaScript"
  },
  ...
]

API teenuse lisainfo:
Tagastatakse ainult aktiivsed kompetentsid (competence tabeli status = 'A').

Veateated: —
```

## API märkmed — GET /api/competence-levels

```text
API: GET /api/competence-levels?competenceId={competenceId}

CompetenceLevelResponseDto.java
Response (200):
[
  {
    "competenceLevelId": 1,
    "levelName": "Algaja"
  },
  ...
]

API teenuse lisainfo:
Tagastatakse valitud kompetentsi aktiivsed tasemed (competence_level tabeli status = 'A'). levelName tuleb level tabelist. See, milline tase millisele kompetentsile kuulub, on kirjas competence_level tabelis. Näide vastab päringule competenceId=1. Kui kompetentsil tasemeid pole, tagastatakse tühi list.

Veateated: —
```

## API märkmed — GET /api/question-types

```text
API: GET /api/question-types

QuestionTypeResponseDto.java
Response (200):
[
  {
    "questionTypeId": 1,
    "questionTypeName": "SINGLE_CHOICE"
  },
  ...
]

API teenuse lisainfo:
Tagastatakse kõik question_type tabeli read (SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE). Tabelis status veergu pole, seega filtreerida pole vaja. questionTypeName tagastatakse koodinimena, eestikeelse nime kuvab frontend.

Veateated: —
```

## API märkmed — POST /api/questions

```text
API: POST /api/questions

QuestionCreateRequestDto.java
Request body:
{
  "competenceLevelId": 1,
  "questionTypeId": 1,
  "questionTitle": "Mis on sulund (closure)?",
  "questionDescription": "Vali JavaScripti sulundi kõige täpsem definitsioon.",
  "questionScore": 10,
  "questionAnswers": [
    {
      "answerText": "Funktsioon, mis mäletab oma leksikaalset skoopi",
      "isCorrect": true
    },
    ...
  ]
}

Response (200):
10

API teenuse lisainfo:
Vastuseks tagastatakse loodud küsimuse questionId. Tulevikus saab /tests/new vaade selle abil uue küsimuse kohe testi valitud küsimuste hulka lisada. Sisselogitud kasutaja userId võetakse sessioonist ning küsimusi saab luua ainult ADMIN või HALDUR rolliga kasutaja. userId salvestatakse question.created_by väljale. competence_id tuleb competenceLevelId kaudu competence_level tabelist. Küsimus salvestatakse question tabelisse (status = 'A') ja iga vastusevariant question_answer tabelisse (isCorrect → correct_choice, status = 'A'). Backend kontrollib õigete vastuste arvu tüübi järgi: SINGLE_CHOICE puhul täpselt 1 õige, MULTIPLE_CHOICE puhul vähemalt 1 õige, TRUE_FALSE puhul 2 varianti ja neist täpselt 1 õige.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "questionTitle: must not be blank"

HTTP: 403
errorCode: NO_PERMISSION_TO_CREATE_QUESTIONS
message: "Sul puudub õigus küsimusi luua"

HTTP: 400
errorCode: INVALID_CORRECT_ANSWER_COUNT
message: "Õigete vastuste arv ei vasta küsimuse tüübile"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'competenceLevelId' väärtusega: 123"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'questionTypeId' väärtusega: 123"
```
