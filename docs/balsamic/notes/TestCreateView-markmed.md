# TestCreateView.vue — Balsamiq märkmed

Vaade "Testi koostamine" (`/tests/new`). Vaade teeb 4 backend kutset.

## Vaate märkmed

```text
Roll: Admin, Haldur
Failinimi: TestCreateView.vue
Frontend rada: /tests/new

Vaatega seotud lisainfo:
Menüü link "Testid → Testi koostamine" on nähtav ainult siis, kui sessionStorage'is olev roleName on ADMIN või MANAGER. Vaate avamisel laetakse kompetentsid (GET /api/competences). Rippmenüü "Tase" muutub aktiivseks pärast kompetentsi valimist (GET /api/competence-levels) ja rippmenüü "Vali küsimus" pärast taseme valimist (GET /api/questions). Kui kompetentsi või taset muudetakse, tühjendatakse juba valitud küsimused.

Kõik väljad on kohustuslikud ja valitud peab olema vähemalt üks küsimus. Kui mõni väli on täitmata, kuvatakse AlertDanger.vue komponendiga teade "Täida kõik väljad".

Taimer on minutites; valik "Ilma taimerita" tähendab, et testil taimerit pole (isTimed = false).
Vormil on ka valik "Punktide ümardamine" (üles / alla) — üks neist peab olema valitud (roundScoreUp = true / false).

Nupp "+" küsimuste real on mõeldud uue küsimuse lisamiseks modaalaknas.

Nupule "Loo test" vajutades kogutakse vormi andmed ja saadetakse backendile POST /api/tests sõnumiga. Õnnestumise korral suunatakse kasutaja vaatele /tests ("Kõik testid"). Nupule "Tühista" vajutades suunatakse kasutaja vaatele /tests (ilma API kutseta).
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
Tagastatakse valitud kompetentsi aktiivsed tasemed (competence_level tabeli status = 'A', levelName tuleb level tabelist). Näide vastab päringule competenceId=1. Kui kompetentsil tasemeid pole, tagastatakse tühi list.

Veateated: —
```

## API märkmed — GET /api/questions

```text
API: GET /api/questions?competenceLevelId={competenceLevelId}

QuestionResponseDto.java
Response (200):
[
  {
    "questionId": 1,
    "questionTitle": "Mis on sulund (closure)?"
  },
  ...
]

API teenuse lisainfo:
Tagastatakse valitud kompetentsi taseme aktiivsed küsimused (question tabeli status = 'A'). Näide vastab päringule competenceLevelId=1. Kui küsimusi pole, tagastatakse tühi list.

Veateated: —
```

## API märkmed — POST /api/tests

```text
API: POST /api/tests

TestCreateRequestDto.java
Request body:
{
  "userId": 1,
  "competenceId": 1,
  "competenceLevelId": 1,
  "testName": "JavaScripti sulundite test",
  "testShortDescription": "Sulundite ja skoobi kontroll.",
  "testDescription": "Closure'ite ja skoobi teadmiste kontroll.",
  "isTimed": true,
  "timerMin": 30,
  "passPercent": 60,
  "roundScoreUp": true,
  "questions": [
    {
      "questionId": 1
    },
    ...
  ]
}

Response (200): NONE

API teenuse lisainfo:
Kõik väljad on kohustuslikud, v.a timerMin, mis on null, kui isTimed = false. userId (sessionStorage'ist) järgi leitakse andmebaasist kasutaja roll — testi saab luua ainult ADMIN või MANAGER rolliga kasutaja. userId salvestatakse test.created_by ja test_question.added_by väljale. Test salvestatakse test tabelisse (status = 'A'), iga küsimus test_question tabelisse; position tuleb questions listi järjekorrast (1, 2, 3 ...).

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "testName: must not be blank"

HTTP: 403
errorCode: NO_PERMISSION
message: "Sul puudub õigus testi luua"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'userId' väärtusega: 123"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'competenceLevelId' väärtusega: 123"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'questionId' väärtusega: 123"
```
