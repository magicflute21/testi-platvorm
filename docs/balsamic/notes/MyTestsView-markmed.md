# MyTestsView.vue — Balsamiq märkmed

## Vaate märkmed

```text
Roll: Kõik rollid (sisse logitud kasutaja näeb ainult endale määratud teste)
Failinimi: MyTestsView.vue
Frontend rada: /my-tests

Vaatega seotud lisainfo:
Vaate avamisel küsitakse backendilt GET /api/me/user-tests sõnumiga sisse logitud kasutajale määratud testid (kasutaja tuvastatakse backendis sessioonist, userId-d eraldi kaasa ei saadeta). Iga määratud test kuvatakse eraldi PreviewCard.vue kaardina (testi nimi, staatuse silt, lühikirjeldus). Kui kasutajale pole ühtegi testi määratud, kuvatakse tekst "Sulle pole ühtegi testi määratud".

Staatuse silt kuvatakse userTestStatus välja põhjal (Status.js): O = "Avatud", C = "Lõpetatud".

Nupule "Soorita test" vajutades suunatakse kasutaja valitud määratud testi (userTestId) alustamise vaatele. Lõpetatud testi puhul on "Soorita test" mitteaktiivne. Nupule "Vaata tulemusi" vajutades suunatakse kasutaja valitud testi tulemuste vaatele /test-result; nupp on aktiivne ainult Lõpetatud testi puhul.

Kui backend vastab HTTP 401 (kasutaja pole sisse logitud), suunatakse kasutaja vaatele /login. Muu vea korral kuvatakse AlertDanger.vue komponendiga teade "Testide laadimine ebaõnnestus".
```

## API märkmed — GET /api/me/user-tests

```text
API: GET /api/me/user-tests

UserTestSummaryDto.java
Response (200):
[
  {
    "userTestId": 3,
    "testId": 1,
    "testName": "JavaScripti algtaseme test",
    "testShortDescription": "JavaScripti algteadmiste kontroll.",
    "userTestStatus": "C"
  },
  {
    "userTestId": 5,
    "testId": 3,
    "testName": "SQL-i aluste test",
    "testShortDescription": "SQL-i põhiteadmiste kontroll.",
    "userTestStatus": "C"
  }
]

API teenuse lisainfo:
userId võetakse sessioonist (CurrentUserService.getUserId()), mitte path/query parameetrist — nii ei saa kasutaja teiste kasutajate teste pärida. Tagastatakse user_test read, kus user_id = sisse logitud kasutaja ja seotud test.status = 'A'. userTestStatus: O = avatud, C = lõpetatud. Kui teste pole, tagastatakse tühi list [].

Veateated: —
```

## Märkused ja lahtised küsimused (ei lähe Balsamiq kasti)

1. **Pooleli testi ("Jätka") ei ole.** Otsus: testi ei saa pooleli jätta, seega on staatusi ainult kaks — `O` (Avatud) ja `C` (Lõpetatud). "Jätka" silt eemaldatakse mockupist. Frontendis tuleb `Status.js` faili lisada `O` ja `C` sildi nimed ja badge klassid.
2. **Aegunud testid (`closes_at` möödas).** Tulevikus lisatav: info tuleb `user_test` tabelist (`opens_at`, `closes_at`) ning seda saab hiljem lisada DTO-sse ja kaardile (nt silt "Sooritamata"). Selle taski jaoks pole vaja.
3. **Route peab kasutama `userTestId`-d.** Praegune router (`/tests/:testId/start` → `/tests/:testId/attempt`) annab edasi `testId`, aga `GET /api/user-tests/{userTestId}/attempt` vajab `userTestId`-d. "Soorita test" peaks edasi andma `userTestId` (nt `/user-tests/:userTestId/start`); testi üldinfo (`testId`) on saadaval sama user_test rea kaudu.
4. **"..." menüü** on valikuline (otsus veel tegemata), **"+" ikoon** eemaldatakse mockupist.
5. **Sidebar:** mockupil on aktiivne "Kõik testid", õige on "Minu testid" — navbar/sidebar on eraldi veel lahendamata.
