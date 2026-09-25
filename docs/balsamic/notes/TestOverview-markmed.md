# TestOverview.vue — Balsamiq märkmed

## Vaate märkmed

```text
Roll: Admin, Manager
Failinimi: TestOverview.vue
Frontend rada: /tests

Vaatega seotud lisainfo:
Vaate avamisel küsitakse backendilt GET /api/tests sõnumiga kõik testid ning kuvatakse iga test eraldi kaardina (nimi, staatuse silt, lühikirjeldus). Backendi vastus (TestSummaryDto list) salvestatakse muutujasse tests, mille põhjal kuvatakse kaardid. Menüü link "Testid" → "Kõik testid" on nähtav ainult adminile ja managerile.

Staatuse silt kuvatakse testStatus välja põhjal: A = "Aktiivne" (roheline), K = "Koostamisel" (kollane), M = "Mitteaktiivne" (punane).

Nupule "Vaata testi" vajutades suunatakse kasutaja uuele vaatele, kus kuvatakse valitud testi andmed (sh pikk kirjeldus). Nupule "Vaata tulemusi" vajutades suunatakse kasutaja valitud testi tulemuste vaatele. "+" ikoonile vajutades suunatakse kasutaja testi loomise vaatele.

Testikaart on eraldi korduvkasutatav komponent, mida kasutatakse hiljem ka teistel vaadetel (erinev info ja nupud).
```

## API märkmed — GET /api/tests

```text
API: GET /api/tests

TestSummaryDto.java
Response (200):
[
  {
    "testId": 1,
    "testName": "JavaScripti algtaseme test",
    "testShortDescription": "JavaScripti algteadmiste kontroll.",
    "testStatus": "A"
  },
  {
    "testId": 3,
    "testName": "SQL-i aluste test",
    "testShortDescription": "SQL-i põhiteadmiste kontroll.",
    "testStatus": "A"
  }
]

API teenuse lisainfo:
Teenus on mõeldud ainult adminile ja managerile. Tagastatakse kõik testid sõltumata staatusest (A = aktiivne, K = koostamisel, M = mitteaktiivne). Kui ühtegi testi pole, tagastatakse tühi list [].

Veateated: —
```
