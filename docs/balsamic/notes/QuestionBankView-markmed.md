# QuestionBankView.vue — Balsamiq märkmed

Vaade "Küsimuste pank" (`/questions`). Vaade teeb 2 backend kutset.

## Vaate märkmed

```text
Roll: Admin, Haldur
Failinimi: QuestionBankView.vue
Frontend rada: /questions

Vaatega seotud lisainfo:
Menüü link "Küsimuste pank → Kõik küsimused" on nähtav ainult siis, kui sessionStorage'is olev roleName on ADMIN või MANAGER. Vaate avamisel laetakse kompetentsid filtri jaoks (GET /api/competences) ja kõik küsimused (GET /api/question-bank). Rippmenüüst "Kompetents" valides laetakse küsimused uuesti ainult valitud kompetentsi kohta (?competenceId={competenceId}); valik "Kõik kompetentsid" näitab jälle kõiki küsimusi.

Iga küsimus kuvatakse eraldi kaardina: pealkiri, küsimuse tüüp, kompetentsi silt ja staatuse silt. Küsimuse tüüp kuvatakse questionTypeName välja põhjal: SINGLE_CHOICE = "Ühe õige vastusega", MULTIPLE_CHOICE = "Mitme õige vastusega", TRUE_FALSE = "Tõene või väär". Staatuse silt kuvatakse questionStatus välja põhjal (Status.js): A = "Aktiivne", I = "Mitteaktiivne".

Kaardid on vaikimisi kinni. Noolele vajutades avaneb kaart ja näitab küsimuse infot (kompetentsi tase competenceLevelName, punktid score), kirjeldust ning vastusevariante (eraldi API kutset ei tehta, andmed tulevad juba nimekirjaga kaasa). Õige(d) vastus(ed) (correctChoice = true) on esile tõstetud rohelise taustaga. Nupp "..." ei tee hetkel midagi — menüü (nt muutmine, deaktiveerimine) lisatakse hilisemas taskis.

Nupule "Lisa uus küsimus" vajutades suunatakse kasutaja uue küsimuse loomise vaatele /questions/new (ilma API kutseta). Kui küsimusi pole, kuvatakse tekst "Küsimusi ei leitud".
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
Tagastatakse ainult aktiivsed kompetentsid (competence tabeli status = 'A'). Sama endpoint on kasutusel ka TestCreateView.vue vaates.

Veateated: —
```

## API märkmed — GET /api/question-bank

```text
API: GET /api/question-bank?competenceId={competenceId}

QuestionBankDto.java
Response (200):
[
  {
    "questionId": 2,
    "questionTitle": "Millised järgnevatest on JS primitiivtüübid?",
    "questionDescription": "Vali kõik JavaScripti primitiivtüübid.",
    "questionTypeName": "MULTIPLE_CHOICE",
    "competenceId": 1,
    "competenceName": "JavaScript",
    "competenceLevelName": "Kesktase",
    "score": 10,
    "questionStatus": "A",
    "answers": [
      {
        "questionAnswerId": 4,
        "answerText": "string",
        "correctChoice": true
      },
      ...
    ]
  },
  ...
]

API teenuse lisainfo:
competenceId on valikuline — kui see puudub, tagastatakse kõigi kompetentside küsimused. Tagastatakse küsimused kõigi staatustega (A ja I), iga küsimuse juures ainult aktiivsed vastusevariandid (question_answer tabeli status = 'A'). Kui küsimusi pole (ka tundmatu competenceId korral), tagastatakse tühi list.

Veateated: —
```
