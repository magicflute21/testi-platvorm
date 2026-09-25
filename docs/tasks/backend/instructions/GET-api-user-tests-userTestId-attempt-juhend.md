# Juhend: GET /api/user-tests/{userTestId}/attempt

**Taski fail:** `docs/balsamic/notes/TestAttemptView-markmed.md` (sektsioon "API märkmed — GET /api/user-tests/{userTestId}/attempt")
**Kontroller:** `TestAttemptController.java`
**Implementeerimise voog:** RestController → Service → Repository → Service → Mapper → RestController

---

## Sissejuhatus

See endpoint laeb testi tegemise vaate jaoks korraga alla kõik vajaliku: millise määramise (`user_test`) ja testiga on tegu, kõik testi küsimused õiges järjekorras koos vastusevariantidega ning kasutaja varem salvestatud valikud. Kui kasutaja pole veel ühelegi küsimusele vastanud (`result` rida puudub), alustatakse testi algusest ja iga küsimuse `selectedQuestionAnswerIds` on tühi list.

Endpoint käib läbi kõigi kihtide ja kogub andmeid **mitmest tabelist** (`user_test`, `test`, `test_question`, `question`, `question_type`, `question_answer`, `result`, `test_question_result`, `test_question_answer`). Selle harjutuse käigus õpid:
- ehitama **pesastatud** vastuse DTO-d (DTO sees on list teistest DTO-dest)
- looma mitu uut repositoryt ja JPA Buddy päringut
- käsitlema `Optional`-it, kui seotud kirje võib puududa
- täitma DTO-d samm-sammult mitmest allikast

**Veaolukorrad (taskist):**

| Olukord | HTTP | errorCode | message |
|---|---|---|---|
| `userTestId` järgi rida ei leitud | 404 | `PRIMARY_KEY_NOT_FOUND` | `Ei leidnud primary keyd 'userTestId' väärtusega: 123` |

> **Tähelepanu:** Vastusevariantide juures **ei tohi** tagastada õigsuse infot (`correct_choice`) — muidu saaks kasutaja õiged vastused brauseri arendaja tööriistadest välja lugeda.

---

## Samm 1 — RestController

### Mida teha?

Kontrolleri klass `TestAttemptController` **on juba olemas** (`controller/testattempt/`), aga selles pole veel ühtegi meetodit. Lisad sinna esimese meetodi, mis võtab URL-ist vastu `userTestId`.

Vaata olemasolevat `TestController`-it — kuidas seal on teekond (`/api/...`) määratud: kas klassi tasemel `@RequestMapping`-ga või otse meetodi mappingannotatsioonis? Järgi sama stiili.

### Meetodi loomine

Alusta meetodist **ilma mappingannotatsioonideta** — see aitab kõigepealt loogika paika saada:

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    // tühi meetod esialgu
}
```

> **Mõtle:** Mis on selle meetodi hea nimi? Nimi peaks kirjeldama, mida meetod teeb.
> Vaata projekti CLAUDE.md meetodi nimetamise reeglit: `get...` lubab kindlat tagastust.
> Mida see endpoint tagastab — kas "testi katse andmed"?

Seejärel lisa:
1. **Mappingannotatsioon** — `@GetMapping` koos teega, milles on `{userTestId}` kohatäide
2. **Parameetri annotatsioon** — kas `@PathVariable` või `@RequestParam`? (Vihje: kus URL-is `userTestId` asub?)
3. **Swagger annotatsioonid** — `@Operation` ja `@ApiResponses` (200 ja 404)

```java
@GetMapping("/mingi/rada/{mingiId}/alamrada")
@Operation(summary = "Lühikokkuvõte")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Kirjeldus, mida viga sisaldab",
                content = @Content(schema = @Schema(implementation = VeaKlass.class)))})
public void meetodiNimi(@PathVariable Integer mingiId) {
}
```

> **Mõtle:** Milline on projekti veaklass, mida Swaggeris 404 puhul näidata? Vaata kausta `infrastructure/error/`.

### Service klassi ettevalmistus

Kontrolli, kas sobiv service klass juba eksisteerib:
- Kaust: `backend/src/main/java/ee/testiplatvorm/service/`
- Seal on `LoginService` ja `TestService`. Kas testi katse loogika sobib `TestService`-sse, või väärib see oma klassi (nt kontrolleri nimega kooskõlas)? Pea meeles, et samasse klassi tulevad hiljem ka PUT (vastuse salvestamine) ja POST (testi lõpetamine).
- Kui **puudub** → loo uus klass IntelliJ'ga (File → New → Java Class)

```java
@Service
@RequiredArgsConstructor
public class TeenusKlass {
    // ...
}
```

Lisa service muutuja kontrolleri klassi:

```java
private final TeenusKlass teenuseMuutuja;
```

Kutsu service meetodit välja (esialgne tühi väljakutse):

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    teenuseMuutuja.meetodiNimi(parameetriNimi);
}
```

> **IntelliJ vihje:** Kui `teenuseMuutuja.meetodiNimi(...)` on punasega alla joonitud,
> vajuta **Alt+Enter** punasel joonel → vali **"Create method in TeenusKlass"**.
> IntelliJ loob automaatselt vastava meetodi service klassi!

---

## Samm 2 — Service ja esimene repository päring

### Mida teha?

Service meetodisse tuleb sisse üks `Integer` — `userTestId`. Esimene ülesanne on leida selle järgi **`user_test` rida**. Sellest reast saad kätte ka seotud testi (`test_id` → `test.name`), mis on vajalik vastuse päise jaoks ja järgmistes päringutes küsimuste leidmiseks.

Kui rida ei leita → 404 `PRIMARY_KEY_NOT_FOUND`.

### Repository ühenduse loomine

Entiteet `UserTest` on olemas (`persistence/UserTest.java`), aga **repositoryt tal veel pole**.

> **Paketi küsimus:** `UserTest` asub praegu `persistence/` juurkaustas. Vaata, kuidas on korraldatud `persistence/test/` (entiteet + repository + mapper ühes alampaketis). Kas oleks mõistlik ka `UserTest` oma alampaketti tõsta enne repository loomist?
> **IntelliJ vihje:** Klassi saab turvaliselt teise paketti tõsta **F6** (Refactor → Move) — IntelliJ uuendab kõik importid ise.

Alusta kirjutama repositooriumi muutuja nime service meetodis:

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    entiteetRep  // <- kirjuta algus siia
}
```

> **Kui repositooriumi interface pole olemas:** IntelliJ pakub punase pirniga (Alt+Enter)
> võimaluse luua uus interface. Vali **JpaRepository** ja kontrolli, et fail läheks
> õigesse paketti (sama pakett kui entiteet).

Kui repository on olemas, vajuta muutuja nime alguse järel **Tab** — IntelliJ lisab selle klassiväljana.

**Küsi endalt:** Kas JPA pakub valmis meetodit rea leidmiseks primary key järgi? Mida see tagastab — entity või `Optional<Entiteet>`?

### Projekti konventsioon: `getValid<Entiteet>By`

Vaata `backend/CLAUDE.md` jaotist **"Entiteedi otsing ID järgi"**. Projekti reegel on, et `findById()` + `orElseThrow(...)` kombinatsioon elab eraldi `public` meetodis kujul `getValid<Entiteet>By(Integer <entiteet>Id)`.

```java
public EntiteetTüüp getValidEntiteetBy(Integer entiteetId) {
    return entiteetRepository.findById(entiteetId)
            .orElseThrow(() -> new SobivException(...));
}
```

> **Mõtle:** Millise exceptioni konstruktor sobib veateatega `Ei leidnud primary keyd 'userTestId' väärtusega: 123`? Vaata kausta `infrastructure/exception/` — mis parameetrid konstruktor ootab?

Kui meetod midagi tagastab, **pane tulemus kohe muutujasse**.

---

## Samm 3 — DTO klassid (loo kohe, mitte alles lõpus)

### Mida teha?

Nüüd, kui esimene entity on käes, on aeg luua väljundi DTO. Siin on DTO **kolmekihiline** — vaata taskifaili Response näidist:

```
TestAttemptResponse              ← ülemine tase: userTestId, testId, testName, questions
 └─ questions: List<...>         ← iga küsimus: questionId, position, title, description,
     │                              questionTypeName, answers, selectedQuestionAnswerIds
     └─ answers: List<...>       ← iga vastusevariant: questionAnswerId, answerText
```

**Miks kohe?** Service meetod kogub andmeid neljast-viiest allikast. Lihtsam on täita üht DTO objekti samm-sammult, kui hoida mitut eraldi muutujat ja need lõpus kokku panna.

Kontrolli, kas DTO-d on juba olemas:
- Kaust: `backend/src/main/java/ee/testiplatvorm/controller/testattempt/dto/` — seda kausta pole veel olemas

**Mõtle:**
- Taskifailis on ülemise DTO nimi antud (`TestAttemptResponseDto`). Kuidas nimetaksid kaks sisemist DTO-d nii, et nimest oleks aru saada, mis need on?
- Kas sisemistele DTO-dele on vaja eraldi faile, või sobivad need samasse paketti? (Projekti tava: igal DTO-l oma fail.)
- **Mis tüüpi** on väli `selectedQuestionAnswerIds`? Mis tüüpi on `answers`?
- Millist välja `QuestionAnswer` entity'st **ei tohi** DTO-sse lisada?

> **Mitme allikaga DTO:** Ülemise DTO väljad tulevad `UserTest`-ist ja seotud `Test`-ist, küsimuse DTO väljad `TestQuestion`-ist ja seotud `Question`/`QuestionType`-ist. JPA Buddy ühe-entity generaator ei loo seda struktuuri üksi — loo DTO-d **käsitsi taskifaili Response näidise järgi**. Kõige sisemise (vastusevariandi) DTO võid proovida teha JPA Buddy abil (`QuestionAnswer` → paremklõps → New → DTO), aga vali väljad hoolikalt.

Vaata `TestSummaryDto` — millised Lombok annotatsioonid seal on? Kasuta samu.

---

## Samm 4 — Mapper ja ülejäänud andmete kogumine

### Mida teha?

Vaja on kolme teisendust:
1. `UserTest` → ülemine response DTO (`questions` jääb `ignore = true`, täidetakse service'is)
2. `TestQuestion` → küsimuse DTO (`answers` ja `selectedQuestionAnswerIds` jäävad `ignore = true`)
3. `QuestionAnswer` → vastusevariandi DTO

Pärast igat päringut ja mappimist täidetakse `ignore = true` väljad service meetodis.

### Mapper

> **Mõtle:** Kas kõik kolm teisendust sobivad ühte mapper interface'i või tasub teha iga entity kõrvale oma mapper (vt `persistence/test/TestMapper.java` asukohta)? Mõlemad on okei — otsusta ja ole järjekindel.

Mapperi interface'i annotatsioon — vaata `TestMapper`-it ja kasuta sama kuju.

Nimeta meetodid konventsiooni järgi:

```java
// Ühele DTO-le
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);

// Lista DTO listiks
List<TagastatavDtoTüüp> toDtoKlassiNimid(List<EntiteetTüüp> entiteedid);
```

> **Tähtis:** `@Mapping` annotatsioonid käivad alati **üksiku objekti** meetodile (ainsuses). List-meetod (mitmuses) jääb annotatsioonideta — MapStruct genereerib selle ise, kutsudes iga elemendi kohta üksiku-objekti meetodit.

Lisa `@Mapping` annotatsioonid:

```java
@Mapping(source = "", target = "")
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);
```

> **IntelliJ vihje:** Kliki `target = ""` jutumärkide vahele → vajuta **Ctrl+Space**.
> IntelliJ näitab kõiki DTO välju — nii saad luua ettevalmistatud `@Mapping` malli.

Täida kõik väljad. Seotud objekti välja poole saab minna **punktiga** (nt `seotudObjekt.alamObjekt.väli`). Mis ei sobi — kasuta `ignore = true`:

```java
@Mapping(source = "seotudObjekt.id", target = "seotudObjektiId")
@Mapping(source = "seotudObjekt.alamObjekt.nimi", target = "alamObjektiNimi")
@Mapping(source = "tavaveerg", target = "tavaveerg")
@Mapping(ignore = true, target = "listVäliMisTäidetakseHiljem")
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);
```

> **Mõtle:** Küsimuse DTO-s on väljad `questionId`, `title`, `description` — need ei ole `TestQuestion`-i enda väljad. Kust `TestQuestion` entity kaudu nendeni jõuad? Ja `questionTypeName`?

### Ülejäänud andmete kogumine

Nüüd on vaja veel kolme asja. Iga päringu jaoks kehtib sama muster nagu Samm 2-s: kontrolli, kas JPA pakub valmismeetodit; kas tulemus on `Optional`; **pane tulemus kohe muutujasse**.

**a) Testi küsimused** — tabel `test_question`, filtreeritud testi järgi, **sorteeritud `position` järgi**.
- Kas `TestQuestion`-il on repository?
- Millise väärtuse järgi filtreerid — kas sul on see Samm 2 tulemusest käes?

**b) Iga küsimuse vastusevariandid** — tabel `question_answer`, filtreeritud küsimuse järgi.
- Mõtle: kas päring tuleb teha iga küsimuse kohta eraldi (tsüklis), või saab kõik korraga kätte ja siis jagada? Algajana on tsükkel täiesti okei — **make it work** enne kui **make it fast**.
- `question_answer` tabelis on `status` veerg — kas ka mitteaktiivsed variandid tuleks näidata? Vaata `3_import.sql`-ist, mis väärtusi seal kasutatakse.

**c) Kasutaja varasemad valikud** — see on kõige keerulisem osa. Ahel on:

```
user_test  →  result (võib puududa!)  →  test_question_result (iga vastatud küsimuse kohta)
                                           →  test_question_answer (iga valitud variandi kohta)
```

- Esmalt: kas sellel `user_test`-il on `result` rida? See võib puududa → päring peaks tagastama `Optional`.
- Kui puudub → kõigi küsimuste `selectedQuestionAnswerIds` = tühi list, rohkem pole midagi pärida.
- Kui olemas → leia selle tulemuse alt valitud vastused ja jaga need küsimuste vahel.

> **Mõtle:** `test_question_answer` real on `question_answer_id` ja läbi `test_question_result`-i ka `question_id`. Kas saaksid ühe päringuga kätte kõik selle `result`-i alla kuuluvad valitud variandid ja siis Java's küsimuse järgi grupeerida?

> **Oluline:** Valitud vastuste list peab olema **tühi list**, mitte `null` — frontend ootab `[]`.

### Service meetodi lõpetamine

Üldkuju:

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    EntiteetTüüp entiteet = getValidEntiteetBy(parameetriNimi);
    TagastatavDtoTüüp tagastatavDto = mapperMuutuja.toDtoKlassiNimi(entiteet);
    // pärin alamobjektid, mapin need, täidan tagastatavDto listi
    // iga alamobjekti kohta täidan tema enda listid
    return tagastatavDto;
}
```

> **IntelliJ vihje:** Kui tagastustüüp on `void`, aga lisad `return ...;` lause,
> vajuta **Alt+Enter** punase joone peal → IntelliJ parandab tagastustüübi automaatselt!

---

## Samm 5 — Repository (täiendavad päringud)

### Mida teha?

Samm 4 käigus vajad mitut uut repositoryt ja päringut (`TestQuestion`, `QuestionAnswer`, `Result`, `QuestionAnswer` jaoks). Loo need JPA Buddy abil.

### Uue meetodi loomine JPA Buddy abil

Mine repository interface'i faili. Kasuta **JPA Buddy** funktsionaalsust:

1. Ava JPA Buddy paneel (paremklõps repository klassis → JPA Buddy)
2. Valikutes **Method** ja **Query** vali → **Query**
3. Vali meetodi tüüp:
    - **Find instance** — üksiku rea leidmiseks (nt `result` määramise järgi)
    - **Find collection** — mitme rea leidmiseks (nt testi küsimused)
4. Määra **Wrap type**:
    - Üksiku rea puhul, mis võib puududa — `Optional<EntiteetKlass>`
    - Mitme rea puhul — `List<EntiteetKlass>`
5. Lisa **query conditionid** — milliseid veerge filtreeritakse
6. **Advanced** sektsioonis: vali alati **Named parameters**
7. Mitme reaga tulemuse puhul mõtle läbi **Order By Attributes** — testi küsimuste puhul on see kohustuslik!

Peale meetodi loomist:
- Kontrolli parameetrite nimed — ebamäärane `id` asenda konkreetsemaga (nt `testId`)
- Tee vastav muudatus ka `@Query` annotatsiooni nimetud parameetris
- Nimeta meetod projekti reegli järgi (vt `backend/CLAUDE.md` → "Repositooriumi meetodi nimetamine") — nimi peab ütlema, **mida** ta tagastab

### Optional käsitlemine

Kui repository meetod tagastab `Optional<...>` (siin: `result` rida, mis võib puududa), otsusta kohe, kuidas puudumist käsitleda:
- **`orElseThrow(...)`** — kui väärtus on kohustuslik (siin: `user_test` rida — vt `getValid...By`)
- **`isPresent()` / `orElse(...)` / muu `Optional` API** — kui puudumine on lubatud (siin: `result` — test on alles alustamata)

Ära lase `Optional`-il "lihtsalt seista" — otsusta teadlikult, mida puudumise korral tehakse.

---

## Samm 6 — tagasi RestController'isse

### Mida teha?

Service meetod tagastab nüüd täidetud response DTO. Naase kontrolleri meetodisse.

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    teenuseMuutuja.meetodiNimi(parameetriNimi);  // <- enne: tulemus kasutamata
}
```

> **IntelliJ vihje:** Lisa `return` lause. IntelliJ kurdab, et `void` ei saa midagi tagastada — vajuta **Alt+Enter** → "Change return type".

Tulemus:

```java
public TagastatavTüüp meetodiNimi(SisendTüüp parameetriNimi) {
    return teenuseMuutuja.meetodiNimi(parameetriNimi);
}
```

---

## Samm 7 — kood ilusaks (refactor)

### Make it work → Make it beautiful

Kui kood töötab, on aeg vaadata, kas saab koodi puhtamaks muuta. See service meetod tuleb tõenäoliselt pikk — ideaalne koht helper meetodite jaoks (nt "täida küsimuste vastusevariandid", "täida valitud vastused").

Vaata ka CLAUDE.md meetodi nimetamise reeglit: meetod, mis sisaldab tingimuslikku loogikat ja **muteerib DTO-d**, kasutab `handle`-prefiksit ja saab DTO parameetrina sisse.

**Extract Method IntelliJ'ga:**

Märgi service meetodis koodilõik, mida soovid eraldada helper meetodiks → paremklõps → Refactor → Extract Method (**Ctrl+Alt+M**).

> **Tähelepanu:** IntelliJ kasutab ekstraktimisel kogu objekti parameetrina.
> Vaata üle, kas helper meetod vajab tegelikult kogu objekti või ainult üht välja — ja tee vajadusel korrektuur.

Enne:
```java
kontrolliMidagiHelper(dtoObjekt);

private void kontrolliMidagiHelper(DtoTüüp dto) {
    boolean onProbleem = repositoorium.kontrollimeetod(dto.getMingiVäli());
    if (onProbleem) {
        throw new MingiException(...);
    }
}
```

Pärast (parem — anna edasi ainult vajalik):
```java
kontrolliMidagiHelper(dtoObjekt.getMingiVäli());

private void kontrolliMidagiHelper(VäljaTüüp väljaNimi) {
    boolean onProbleem = repositoorium.kontrollimeetod(väljaNimi);
    if (onProbleem) {
        throw new MingiException(...);
    }
}
```

### Meetodite järjekord

Kontrolli meetodite järjekorda vastavalt Java konventsioonile:
1. `public` meetodid enne
2. `private` meetodid pärast
3. Järjesta ka väljakutsumise hierarhia järgi — peameetod üleval, helper meetodid all

---

## Kokkuvõte ja kontrollnimekiri

Enne kui pead koodi valmis, kontrolli läbi:

- [ ] RestController klass on olemas vajaliku `@RestController`, `@RequiredArgsConstructor` annotatsiooniga ja tee on kujul `/api/user-tests/{userTestId}/attempt`
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponses` annotatsioonid (200 ja 404)
- [ ] Service klass on olemas vajaliku `@Service`, `@RequiredArgsConstructor` annotatsiooniga
- [ ] `user_test` rida leitakse `getValid...By` meetodi kaudu ja puudumisel visatakse `PrimaryKeyNotFoundException` (`userTestId`)
- [ ] Kõik uued repository interface'id laiendavad `JpaRepository`-t ja asuvad entiteediga samas paketis
- [ ] Repository meetoditel on `@Query` annotatsioon Named parameters stiilis ja nimed ütlevad, mida nad tagastavad
- [ ] Küsimused on sorteeritud `position` järgi
- [ ] Mapper interface on olemas `@Mapper(componentModel = "spring")` annotatsiooniga
- [ ] Kõik `@Mapping` annotatsioonid on täidetud — iga target-väli on kas `source`-iga või `ignore = true`-ga
- [ ] Vastusevariantide DTO-s **ei ole** `correct_choice` infot
- [ ] Kui `result` puudub, on `selectedQuestionAnswerIds` tühi list (`[]`), mitte `null`
- [ ] Meetodite järjekord: `public` enne, `private` pärast — järjesta ka väljakutsumise hierarhia järgi
- [ ] Kood kompileerub ja Swagger UI kaudu on endpoint nähtav

---

> **Järgmine samm:** Testi endpointi Swagger UI kaudu (`http://localhost:8080/swagger-ui/index.html`):
> 1. olemasoleva `userTestId`-ga, millel **pole** `result` rida → kõik valikud tühjad
> 2. olemasoleva `userTestId`-ga, millel **on** salvestatud vastuseid → valikud täidetud
> 3. olematu `userTestId`-ga → 404 ja õige veateade
>
> Vaata `3_import.sql`-ist, millised testandmed sobivad — või lisa need ise.
