# Juhend: GET /api/competence-levels?competenceId={competenceId}

**Taski fail:** `docs/balsamic/notes/TestCreateView-markmed.md` (sektsioon "API märkmed — GET /api/competence-levels")
**Kontroller:** `CompetenceLevelController.java` (või sobiv olemasolev kontroller — vt Samm 1)
**Implementeerimise voog:** RestController → Service → Repository → Service → Mapper → RestController

---

## Sissejuhatus

See endpoint täidab vaate "Testi koostamine" rippmenüü **"Tase"**. Kui kasutaja on valinud kompetentsi, küsib frontend backendilt selle kompetentsi aktiivseid tasemeid. Vastuses on iga taseme kohta `competenceLevelId` ja `levelName`.

Siin on üks eripära: andmed tulevad **kahest tabelist**. Filtreerimine käib `competence_level` tabeli järgi (kompetents + staatus), aga taseme nimi on `level` tabelis. Selle harjutuse käigus õpid, kuidas päringu sisendiks olla muu väli kui tabeli `id` (st `@RequestParam`), kuidas JPQL-is seotud entiteedi väljale ligi pääseda ja kuidas mapperis seotud objekti välja "lamedaks" kaardistada.

> **Miks just `competence_level` id, mitte `level` id?** Vaata `docs/database/2_create.sql` — `test.competence_level_id` viitab tabelile `competence_level`. Seega peab frontend hiljem POST /api/tests juures saatma just selle id.

---

## Samm 1 — RestController

### Mida teha?

API tee on `/api/competence-levels` — see on eraldi ressurss (kompetentsi tase), mitte kompetents ise ega test. Mõtle, kas lisad meetodi olemasolevasse kontrollerisse või lood uue. Projektis on iga ressursi jaoks oma alampakett (`controller/competence/`, `controller/test/`, `controller/login/`).

Kontrolli esmalt, kas vastav kontrolleri klass juba eksisteerib:
- Kaust: `backend/src/main/java/ee/testiplatvorm/controller/`
- Kui **puudub** → loo uus alampakett ja klass IntelliJ'ga (File → New → Java Class)
- Kui **on olemas** → ava see klass ja lisa sinna uus meetod

> **Projekti tava:** Vaata `CompetenceController.java` ja `TestController.java` — need **ei kasuta** klassitasemel `@RequestMapping("/api")`, vaid kirjutavad kogu tee otse `@GetMapping` sisse. Järgi sama mustrit.

Vajalikud klassiannotatsioonid (kui lood uue kontrolleri):

```java
@RestController
@RequiredArgsConstructor
public class KontrolleriKlass {
    // ...
}
```

### Meetodi loomine

Alusta meetodist **ilma mappingannotatsioonideta**:

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    // tühi meetod esialgu
}
```

> **Mõtle:** Mis on selle meetodi hea nimi? Vaata, kuidas on nimetatud `CompetenceController` meetod — see teeb väga sarnast asja.

Seejärel lisa:
1. **Mappingannotatsioon** — `@GetMapping`
2. **Parameetri annotatsioon** — API tees on `?competenceId=...`. Kas see on `@PathVariable` või `@RequestParam`?
3. **Swagger annotatsioonid** — `@Operation` ja `@ApiResponse(s)`. Veaolukordi taskis pole, seega piisab `200` vastusest.

### Service klassi ettevalmistus

Kontrolli, kas sobiv service klass juba eksisteerib:
- Kaust: `backend/src/main/java/ee/testiplatvorm/service/`
- Kui **puudub** → loo uus klass

```java
@Service
@RequiredArgsConstructor
public class TeenusKlass {
    // ...
}
```

Lisa service muutuja kontrolleri klassi ja kutsu service meetod välja:

```java
private final TeenusKlass teenuseMuutuja;

public void meetodiNimi(SisendTüüp parameetriNimi) {
    teenuseMuutuja.meetodiNimi(parameetriNimi);
}
```

> **IntelliJ vihje:** Kui `teenuseMuutuja.meetodiNimi(...)` on punasega alla joonitud,
> vajuta **Alt+Enter** → **"Create method in TeenusKlass"**.

---

## Samm 2 — Service ja repository päring

### Mida teha?

Service meetodisse tuleb sisse kompetentsi id. Eesmärk on leida sellele kompetentsile kuuluvad **aktiivsed** `competence_level` read.

Mõtle: **millisest tabelist** päring algab? Filtreeritakse `competence_level.competence_id` ja `competence_level.status` järgi.

Entiteet on juba olemas: `persistence/competencelevel/CompetenceLevel.java`. Vaata sama kausta sisu — kas repositoorium on seal olemas?

Alusta kirjutama repositooriumi muutuja nime service meetodis:

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    entiteetRep  // <- kirjuta algus siia
}
```

> **IntelliJ vihje:** Kirjuta muutuja nime algus ja vajuta **Tab** → repositoorium lisatakse klassiväljana.

> **Kui repositooriumi interface pole olemas:** loo see (Alt+Enter või File → New) ja kontrolli, et see laiendab `JpaRepository`-t ja asub **samas paketis** kui entiteet.

**Küsi endalt:** Kas JPA `findById()` sobib? Sisendiks on kompetentsi id, mitte `competence_level` id.

> **Rusikareegel:** Kui päringusse läheb sisendina muu väärtus kui tabeli `id`,
> on tõenäoliselt vaja **uut meetodit** (vt Samm 5 "Uue meetodi loomine JPA Buddy abil").

> **Staatus:** Vaata, kuidas `CompetenceService` annab aktiivse staatuse päringule edasi. Projektis on selleks `Status` enum — kasuta sama lähenemist, mitte kõvakodeeritud `"A"`-d.

Kui repository meetod midagi tagastab, **pane tulemus kohe muutujasse**.

---

## Samm 3 — DTO klass

### Mida teha?

Vastuse kuju on taskis kirjas: `CompetenceLevelResponseDto` väljadega `competenceLevelId` ja `levelName`.

Kas DTO on juba olemas?
- Vaata kaustast: `backend/src/main/java/ee/testiplatvorm/controller/.../dto/`

**Kui DTO puudub** → kasuta JPA Buddy abi:

1. Paremklõps `CompetenceLevel` entity klassil → New → DTO
2. Kontrolli valikud:
    - **Package** → sinu kontrolleri alampaketi `dto` kaust
    - **DTO class name** → `CompetenceLevelResponseDto` (projekti tava: DTO nimi lõpeb alati `Dto`-ga)
    - **MapStruct Interface** → loo uus plussmärgiga (mapper läheb `persistence/competencelevel/` kausta)
    - **Mutable** → jäta märgituks
3. Väli `level` on foreign key objekt — vali **Flat** struktuur, et saaksid selle `name` välja otse DTO-sse
4. Peale loomist nimeta väljad ümber taski järgi ja eemalda üleliigsed

> **Võrdle:** `CompetenceResponseDto.java` on hea eeskuju — vaata, milliseid Lomboki annotatsioone seal kasutatakse.

---

## Samm 4 — Mapper

### Mida teha?

Entiteedi väljad tuleb nüüd DTO-sse kaardistada. `levelName` ei ole `CompetenceLevel` enda väli — see asub seotud objekti `level` sees.

Ava mapper interface. Eemalda JPA Buddy loodud mittevajalikud meetodid.

Vaata `CompetenceMapper.java` — seal on näha projekti mapperi annotatsioon (`@Mapper(...)`) ja meetodite nimetamise tava. Järgi sama mustrit.

Nimeta meetodid konventsiooni järgi:

```java
// Ühele DTO-le
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);

// Listi jaoks — mitmuses, ilma @Mapping annotatsioonideta
List<TagastatavDtoTüüp> toDtoKlassiNimid(List<EntiteetTüüp> entiteedid);
```

> **Tähelepanu:** `@Mapping` annotatsioonid käivad **üksiku objekti** meetodile. List-meetodi genereerib MapStruct ise.

Lisa `@Mapping` annotatsioonid:

```java
@Mapping(source = "", target = "")
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);
```

> **IntelliJ vihje:** Kliki `target = ""` jutumärkide vahele → vajuta **Ctrl+Space**.
> IntelliJ näitab DTO väljad — nii saad kiiresti malli.

> **Mõtle:** Kuidas viidata `source`-is seotud objekti väljale? Vihje: punktiga (`seotudObjekt.väli`).

Iga target-väli peab olema kaardistatud kas `source`-iga või `ignore = true`-ga.

### Service meetodi lõpetamine

Kutsu mapper service meetodis välja:

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    List<EntiteetTüüp> entiteedid = entiteetRepository.meetodiNimi(...);
    return mapperMuutuja.toDtoKlassiNimid(entiteedid);
}
```

> **IntelliJ vihje:** Tagastustüüp on `void`, aga `return` on sees → **Alt+Enter** → IntelliJ parandab tagastustüübi.

---

## Samm 5 — Repository (uus päring)

### Uue meetodi loomine JPA Buddy abil

Mine repository interface'i faili ja kasuta **JPA Buddy** abi:

1. Paremklõps repository klassis → JPA Buddy
2. Vali **Query**
3. Meetodi tüüp: **Find collection** — tasemeid võib olla mitu
4. **Wrap type**: `List<EntiteetKlass>`
5. **Query conditionid** — millised kaks tingimust on vaja? (Vihje: üks on seotud kompetentsi kohta, teine staatuse kohta)
6. **Advanced** → vali **Named parameters**
7. **Order By Attributes** — mis järjekorras tahaks kasutaja dropdownis tasemeid näha? Vaata `level` tabelis olevaid veerge.

Peale meetodi loomist:
- Kontrolli parameetrite nimesid — ebamäärane `id` asenda konkreetsemaga (nt `competenceId`)
- Tee sama muudatus `@Query` nimetud parameetris
- Nimeta meetod nii, et nimest on näha, **mida** ta tagastab (vt `CompetenceRepository.findCompetencesBy(...)`)

> **Tühi tulemus:** Kui kompetentsil tasemeid pole, peab tulema tühi list. Kas `List` tagastav päring teeb seda ise? Kas siin on vaja midagi eraldi käsitleda?

---

## Samm 6 — tagasi RestController'isse

Täienda kontrolleri meetodit — lisa `return` lause:

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    teenuseMuutuja.meetodiNimi(parameetriNimi);  // <- enne: tulemus kasutamata
}
```

> **IntelliJ vihje:** Lisa `return` → **Alt+Enter** → "Change return type".

Tulemus:

```java
public TagastatavTüüp meetodiNimi(SisendTüüp parameetriNimi) {
    return teenuseMuutuja.meetodiNimi(parameetriNimi);
}
```

---

## Samm 7 — kood ilusaks (refactor)

### Make it work → Make it beautiful

Kui kood töötab, vaata, kas saab koodi puhtamaks muuta.

**Extract Method IntelliJ'ga:** märgi koodilõik → paremklõps → Refactor → Extract Method.

> **Tähelepanu:** IntelliJ kasutab ekstraktimisel vahel kogu objekti parameetrina.
> Vaata üle, kas helper meetod vajab tegelikult kogu objekti või ainult üht välja.

Selle endpointi service meetod on tõenäoliselt lühike — kui refaktoreerida pole midagi, on see ka täiesti okei.

### Meetodite järjekord

1. `public` meetodid enne
2. `private` meetodid pärast
3. Väljakutsumise hierarhia järgi — peameetod üleval, helper meetodid all

---

## Kokkuvõte ja kontrollnimekiri

- [ ] Kontroller asub õiges alampaketis ja sellel on `@RestController` ja `@RequiredArgsConstructor`
- [ ] Kontrolleri meetodil on `@GetMapping("/api/competence-levels")` ja `@RequestParam`
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponse(s)` annotatsioonid, summary kirjeldab täpselt, mida endpoint teeb
- [ ] Service klass on olemas `@Service` ja `@RequiredArgsConstructor` annotatsiooniga
- [ ] Repository interface asub `persistence/competencelevel/` paketis ja laiendab `JpaRepository`-t
- [ ] Repository meetodil on `@Query` Named parameters stiilis, filtreerib kompetentsi **ja** aktiivse staatuse järgi ning sorteerib mõistlikult
- [ ] Aktiivne staatus tuleb `Status` enumist, mitte kõvakodeeritud stringist
- [ ] `CompetenceLevelResponseDto` väljad on `competenceLevelId` ja `levelName`
- [ ] Mapperil on sama `@Mapper(...)` annotatsioon nagu teistel projekti mapperitel
- [ ] Kõik `@Mapping` target-väljad on kaardistatud (`source` või `ignore = true`)
- [ ] List-mapperi meetodi nimi on mitmuses ja ilma `@Mapping` annotatsioonideta
- [ ] Meetodite järjekord: `public` enne, `private` pärast
- [ ] Kood kompileerub ja Swagger UI kaudu on endpoint nähtav

---

> **Järgmine samm:** Testi endpointi Swagger UI kaudu (`http://localhost:8080/swagger-ui/index.html`).
> `competenceId=1` peaks tagastama "Algaja" ja "Kesktase", `competenceId=3` tühja listi.
