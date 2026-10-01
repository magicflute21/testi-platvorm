# Juhend: GET /api/question-bank?competenceId={competenceId}

**Taski fail:** `docs/tasks/backend/Kusimuste-panga-nimekirja-paring.md`
**Kontroller:** `QuestionController.java` (olemasolev)
**Implementeerimise voog:** RestController → Service → Repository → Service → Mapper → RestController

---

## Sissejuhatus

See endpoint täidab vaate "Küsimuste pank" (`/questions`) küsimuste nimekirja. Iga küsimuse juurde tuleb kaasa kompetentsi nimi, küsimuse tüüp, staatus ja aktiivsed vastusevariandid, et frontend saaks kaardi lahti klõpsamisel kõike näidata ilma lisapäringuta.

Uus asi võrreldes varasemate GET taskidega on kaks:
1. **Valikuline filter** — `competenceId` võib puududa ja siis tagastatakse kõik küsimused.
2. **Pesastatud list** — iga küsimuse DTO sees on omakorda vastusevariantide list, mis tuleb teisest tabelist (`question_answer`).

> **Tähtis:** Olemasolev `GET /api/questions?competenceLevelId=` (sama kontroller, sama service) peab jääma täpselt samaks — seda kasutab `TestCreateView.vue`.

---

## Samm 1 — RestController

### Mida teha?

Küsimustega seotud kontroller on juba olemas: `controller/question/QuestionController.java`. Uus meetod läheb samasse klassi, olemasoleva meetodi kõrvale.

- Kaust: `backend/src/main/java/ee/testiplatvorm/controller/question/`
- Projekti tava: **klassitasemel `@RequestMapping`-ut ei kasutata**, kogu tee on `@GetMapping` sees.

### Meetodi loomine

Alusta meetodist **ilma mappingannotatsioonideta**:

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    // tühi meetod esialgu
}
```

> **Mõtle:** Mis on selle meetodi hea nimi? Olemasolev meetod samas klassis on `findQuestionsBy` — uus nimi peaks sellest selgelt eristuma.

Seejärel lisa:
1. **Mappingannotatsioon** — `@GetMapping` koos kogu teega
2. **`@RequestParam`** — parameeter on **valikuline**. Mõtle: milline `@RequestParam` atribuut ütleb Springile, et parameetrit ei pruugi URL-is olla? Ja mis väärtuse saab muutuja siis, kui parameetrit pole?
3. **Swagger annotatsioonid** — `@Operation` summary kirjeldagu täpselt, mida tagastatakse (kõik staatused, valikuline filter)

### Service meetodi väljakutse

Service klass `QuestionService` on juba olemas ja kontrolleris juba väljana olemas. Kutsu sealt uut meetodit:

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    teenuseMuutuja.meetodiNimi(parameetriNimi);
}
```

> **IntelliJ vihje:** Punasel meetodinimel **Alt+Enter** → **"Create method in QuestionService"**.

---

## Samm 2 — Service ja esimene repository päring

### Mida teha?

Service meetodisse tuleb sisse `competenceId`, mis võib olla `null`. Esimese asjana on vaja kätte saada küsimused tabelist `question`.

`QuestionRepository` on juba olemas ja service'is juba väljana olemas. Selles on meetod `findQuestionsBy(...)` — **seda ei muuda**, sest selle filter (tase + ainult aktiivsed) ei sobi siia.

**Küsi endalt:**
- Kas `findAll()` sobiks? Mis siis saab filtrist?
- Kas saab ühe päringuga katta mõlemad juhud — filtriga ja ilma?
- Kas staatuse järgi on siin vaja filtreerida? (Vaata taski "Seotud andmebaasi tabelid" osa.)
- Mis järjekorras küsimused tulevad? (Vaata taski vastuvõtu kriteeriume.)

> **Rusikareegel:** Kui päringus on sisendiks midagi muud kui tabeli `id` → tõenäoliselt on vaja uut repository meetodit (vt Samm 5).

Pane päringu tulemus **kohe muutujasse** — sellega hakkad järgmistes sammudes tööle.

---

## Samm 3 — DTO klassid

### Mida teha?

Väljundis on kaks taset: küsimuse DTO ja selle sees vastusevariandi DTO. Mõlemaid veel pole — need tuleb luua kausta `controller/question/dto/`.

Vaata taskifaili "Väljund" osa: seal on JSON näidis ja tabel, kust iga väli pärineb.

1. **Vastusevariandi DTO** (taskis ettepanek `QuestionBankAnswerDto`) — loo JPA Buddy abil `QuestionAnswer` entity pealt (paremklõps entity'l → New → DTO), vali ainult vajalikud väljad ja nimeta need taski järgi ümber.
2. **Küsimuse DTO** (`QuestionBankDto`) — väljad tulevad **mitmest entity'st** (`Question`, `Competence`, `QuestionType`) ja lisaks on seal list vastusevariantidest. Siin sobib JPA Buddy **Flat** struktuur seotud objektide jaoks, aga kontrolli tulemus taski JSON-i järgi üle ja paranda käsitsi.

> **Mõtle:** Mis tüüpi peab olema vastusevariantide väli küsimuse DTO-s?

Kontrolli, et ID-väljad oleks subjektiga (`questionId`, `questionAnswerId`, `competenceId`), mitte lihtsalt `id`.

---

## Samm 4 — Mapper ja vastusevariantide lisamine

### Mida teha?

Küsimused tuleb mapida DTO-deks ja siis igale küsimusele lisada tema aktiivsed vastusevariandid.

### Mapper

Kasuta olemasolevaid mappereid:
- `persistence/question/QuestionMapper.java` — küsimus → küsimuse DTO
- `persistence/questionanswer/QuestionAnswerMapper.java` — vastus → vastuse DTO

Iga mapperisse tuleb **kaks meetodit**: üks üksiku objekti jaoks ja üks listi jaoks.

```java
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);

List<TagastatavDtoTüüp> toDtoKlassiNimid(List<EntiteetTüüp> entiteedid);
```

`@Mapping` annotatsioonid käivad **ainult üksiku objekti meetodile**. List-meetodi genereerib MapStruct ise.

Lisa üksiku objekti meetodile `@Mapping` read:

```java
@Mapping(source = "", target = "")
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);
```

> **IntelliJ vihje:** Kliki `target = ""` jutumärkide vahele → **Ctrl+Space**. Nii näed kõiki DTO välju ja saad malli, kus igal väljal on oma rida.

Kõik target-väljad peavad olema kirjas — kas `source = "..."` või `ignore = true`:

```java
@Mapping(source = "seotudObjekt.id", target = "seotudObjektiId")
@Mapping(source = "seotudObjekt.nimi", target = "seotudObjektiNimi")
@Mapping(ignore = true, target = "väliMidaEntityEiKata")
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);
```

> **Mõtle:** Millist küsimuse DTO välja ei saa `Question` entity'st kätte? See jääb mapperis `ignore = true` ja täidetakse service'is.

### Vastusevariantide lisamine service'is

Pärast mappimist on sul list küsimuste DTO-dest, mille vastusevariantide väli on tühi. Iga küsimuse jaoks tuleb:
1. pärida tabelist `question_answer` selle küsimuse **aktiivsed** vastused
2. need vastuse DTO-deks mapida
3. panna tulemus küsimuse DTO-le

> **Mõtle:** Sul on list DTO-sid — kuidas iga elemendiga midagi teha? Ja kust saad iga DTO puhul teada, millise küsimuse vastuseid pärida?

`QuestionAnswerRepository` on olemas — kontrolli, kas seal on juba sobiv meetod, või on vaja uus (Samm 5).

Lõpuks tagasta list:

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    // ... päring, mappimine, vastuste lisamine
    return dtoList;
}
```

> **IntelliJ vihje:** `void` + `return` → **Alt+Enter** → parandab tagastustüübi.

---

## Samm 5 — Repository (uued päringud)

### Mida teha?

Vaata üle mõlemad repositoryd:
1. `QuestionRepository` — küsimused valikulise kompetentsi filtriga. Sellist meetodit veel pole.
2. `QuestionAnswerRepository` — ühe küsimuse vastused staatuse järgi. Enne uue loomist kontrolli, kas mõni olemasolev meetod juba teeb seda — mõni varasem task võis selle juba vajada.

### Uue meetodi loomine JPA Buddy abil

1. Paremklõps repository failis → JPA Buddy
2. Vali **Query**
3. Vali tüüp: **Find collection**
4. Wrap type: `List<EntiteetKlass>`
5. Lisa query conditionid — milliste veergude järgi filtreeritakse
6. **Advanced** → **Named parameters**
7. Mõtle läbi **Order By Attributes**

Peale loomist:
- Asenda ebamäärane `id` konkreetsema nimega (nt `kompetentsiId`) — ka `@Query` sees
- Eemalda üleliigsed `@Param()` annotatsioonid

> **Valikuline filter:** JPA Buddy genereerib tingimuse, mis eeldab, et parameeter on alati olemas. Mõtle: mis juhtub, kui `competenceId` on `null`? Kuidas saab JPQL-is kirjutada tingimuse "kui parameeter on null, siis ära filtreeri"? Testi mõlemat juhtu Swaggeris.

> **Staatus:** vaata, kuidas olemasolev `findQuestionsBy` annab staatuse ette (`Status` enum) — tee samamoodi, ära kirjuta `'A'` otse päringusse.

---

## Samm 6 — tagasi RestController'isse

### Mida teha?

Service meetod tagastab nüüd listi. Täienda kontrolleri meetodit — lisa `return` ja paranda tagastustüüp (**Alt+Enter** → "Change return type").

```java
public TagastatavTüüp meetodiNimi(SisendTüüp parameetriNimi) {
    return teenuseMuutuja.meetodiNimi(parameetriNimi);
}
```

Testi Swaggeris kolm juhtu (oodatud tulemused on taskifailis):
- ilma `competenceId`-ta
- `competenceId=1`
- `competenceId=123` (tundmatu)

---

## Samm 7 — kood ilusaks (refactor)

### Make it work → Make it beautiful

Kui kood töötab, vaata service meetod üle. Vastusevariantide lisamise osa on hea kandidaat eraldi helper meetodiks.

**Extract Method IntelliJ'ga:** märgi koodilõik → paremklõps → Refactor → Extract Method.

> **Tähelepanu:** IntelliJ annab helperile tihti kaasa rohkem, kui vaja. Vaata üle, kas helper vajab kogu objekti või ainult üht välja.

```java
kontrolliMidagiHelper(dtoObjekt.getMingiVäli());

private void kontrolliMidagiHelper(VäljaTüüp väljaNimi) {
    // ...
}
```

### Meetodite järjekord

1. `public` meetodid enne
2. `private` meetodid pärast
3. Peameetod üleval, helperid selle all väljakutsumise järjekorras

---

## Kokkuvõte ja kontrollnimekiri

- [ ] Uus meetod on `QuestionController`-is, olemasolev `findQuestionsBy` on muutmata
- [ ] `competenceId` on valikuline `@RequestParam`
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponse` annotatsioonid
- [ ] Uued DTO-d on kaustas `controller/question/dto/`, ID-väljad on subjektiga
- [ ] Repository meetoditel on `@Query` Named parameters stiilis, staatus tuleb `Status` enumist
- [ ] Küsimuste päring ei filtreeri staatuse järgi, vastuste päring tagastab ainult aktiivsed
- [ ] Mapperites on `@Mapping` ainult üksiku objekti meetodil, iga target-väli on kaardistatud (`source` või `ignore = true`)
- [ ] List-meetodite nimed on mitmuses
- [ ] Meetodite järjekord: `public` enne, `private` pärast
- [ ] Kood kompileerub ja Swaggeris töötavad kõik kolm testjuhtu
- [ ] `GET /api/questions?competenceLevelId=1` töötab edasi nagu enne

---

> **Järgmine samm:** Testi endpointi Swagger UI kaudu (`http://localhost:8080/swagger-ui/index.html`) ja võrdle vastust taskifaili näidisandmetega.
