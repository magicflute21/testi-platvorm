# Juhend: POST /api/tests

**Taski fail:** `docs/balsamic/notes/TestCreateView-markmed.md` (sektsioon "API märkmed — POST /api/tests")
**Kontroller:** `TestController.java`
**Implementeerimise voog:** RestController → Service → Mapper → Repository → Service → RestController

---

## Sissejuhatus

See endpoint salvestab vaate "Testi koostamine" vormi andmed. Frontend saadab ühe JSON-objekti, milles on testi andmed ja valitud küsimuste list. Backend kontrollib, kas kasutajal on õigus testi luua, salvestab testi `test` tabelisse ja iga küsimuse `test_question` tabelisse õige järjekorranumbriga (`position`).

Erinevalt senistest GET-endpointidest **kirjutab** see andmebaasi, ja seda kahte tabelisse korraga. Selle harjutuse käigus õpid:
- `@RequestBody` ja sisendi DTO (sh DTO sees olev list teistest DTO-dest)
- sisendi valideerimist (`@Valid`, `@NotBlank`, `@NotNull`)
- mapperit suunas **DTO → entiteet**
- seoseid (`@ManyToOne`) ja süsteemseid välju (staatus, kuupäevad, looja), mida DTO-s pole
- `@Transactional`-i: kas kõik salvestub, või mitte midagi

**Andmebaas:** `test`, `test_question` (kirjutamine); `user`, `role`, `competence_level`, `question` (lugemine).

---

## Samm 1 — RestController

### Mida teha?

`TestController` on juba olemas (seal on `GET /api/tests` ja `GET /api/tests/{testId}/start-info`). Lisa sinna uus meetod.

- Kaust: `backend/src/main/java/ee/testiplatvorm/controller/test/`
- Projekti tava: `@GetMapping`/`@PostMapping` sisaldab kogu teed, klassitasemel `@RequestMapping`-ut pole

### Meetodi loomine

Alusta meetodist **ilma mappingannotatsioonideta**:

```java
public void meetodiNimi(SisendDtoTüüp sisendDto) {
    // tühi meetod esialgu
}
```

> **Mõtle:** Mis on selle meetodi hea nimi? See **loob** uue testi. Vaata ka, kuidas on nimetatud teised meetodid samas kontrolleris.

Seejärel lisa:
1. **Mappingannotatsioon** — POST-päringu jaoks
2. **Parameetri annotatsioon** — andmed tulevad päringu **kehas** (body), mitte URL-is
3. **`@Valid`** parameetri ette — et DTO väljadel olevad `@NotBlank`/`@NotNull` reeglid käivituksid (vt Samm 2)
4. **Swagger annotatsioonid** — `@Operation` ja `@ApiResponses`. Veaolukordi on mitu (400, 403, 404) — kirjelda need kõik

> **Mõtle enne tagastustüübi valimist:** Vaata märkmetest `Response (200)`. Kas loomise järel tuleb midagi tagastada?

```java
@PostMapping("/mingi/rada")
@Operation(summary = "Lühikokkuvõte")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "4xx", description = "Mis viga",
                content = @Content(schema = @Schema(implementation = ApiError.class)))})
public void meetodiNimi(@Valid @RequestBody SisendDtoTüüp sisendDto) {
    teenuseMuutuja.meetodiNimi(sisendDto);
}
```

> **IntelliJ vihje:** `TestService` on olemas ja kontrolleris juba väljana. Kui service meetod on punane, **Alt+Enter** → **Create method in TestService**.

---

## Samm 2 — Sisendi DTO (RequestBody)

### Mida teha?

Kontrolleri parameetri tüüpi veel pole. Märkmete järgi on selle nimi `TestCreateRequestDto`.

- Pakett: `controller/test/dto/`
- **Väljanimed kopeeri märkmete JSON-ist** — need on leping frontendiga (frontend saadab juba täpselt neid nimesid)
- Lomboki annotatsioonid samad, mis teistel DTO-del (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`)

### `questions` väli

JSON-is on `questions` **list objektidest**, mitte numbritest:

```json
"questions": [ { "questionId": 1 }, { "questionId": 4 } ]
```

> **Mõtle:** Mis tüüpi peab see väli Java-s olema, et Jackson oskaks iga `{ ... }` objekti Java objektiks teha? Kas vajad selleks **teist**, väikest DTO klassi?

### Valideerimine

Märkmed: *"Kõik väljad on kohustuslikud, v.a timerMin"*. Veateade `"testName: must not be blank"` vihjab, milline annotatsioon tekstiväljadele sobib.

- Tekstiväljad (`String`) → mis annotatsioon keelab ka tühja stringi `""`?
- Muud väljad (`Integer`, `Boolean`) → mis annotatsioon keelab `null`-i?
- `questions` → kas tühi list `[]` peaks läbi minema?

> **Hea teada:** `RestExceptionHandler.handleMethodArgumentNotValid` on juba olemas — see teeb valideerimisvigadest automaatselt `400 INCORRECT_INPUT` vastuse täpselt märkmetes toodud kujul. Sa ei pea selleks midagi lisama.

---

## Samm 3 — Service: kasutaja ja õiguste kontroll

### Mida teha?

Enne kui midagi salvestame, tuleb kontrollida, **kes** testi loob. Märkmed:

> userId järgi leitakse andmebaasist kasutaja roll — testi saab luua ainult ADMIN või MANAGER rolliga kasutaja.

1. Leia kasutaja `userId` järgi. Kui teda pole → `PrimaryKeyNotFoundException` (vt `TestService.findTestStartInfo` — seal on sama muster).
2. Kontrolli kasutaja rolli nime. Kui see pole lubatud → `ForbiddenException`.

> **Tähelepanu — andmetes on vastuolu!** Märkmetes on rollid `ADMIN` ja `MANAGER`, aga `3_import.sql` `role` tabelis on `ADMIN`, `HALDUR`, `KASUTAJA`. Vaata andmebaasist järele, mis nimi seal päriselt on, ja räägi rühmaga, kumb õigeks jääb.

### Veateade

`ForbiddenException` vajab teadet ja veakoodi. Vaata `LoginService`-i: seal tuleb teade `Error` enumist (`INCORRECT_CREDENTIALS`). Lisa `Error` enumisse uus väärtus märkmete teatega `"Sul puudub õigus testi luua"` ja veakoodiga `NO_PERMISSION`.

> **Rusikareegel:** Kui päringusse läheb sisendina muu väärtus kui tabeli `id`, on vaja **uut meetodit**. Kasutaja leidmiseks id järgi piisab `findById()`-st.

Kui `findById()` tagastab `Optional`-i, otsusta kohe, mida puudumise korral teha — ära lase `Optional`-il "lihtsalt seista".

---

## Samm 4 — Mapper (DTO → entiteet)

### Mida teha?

Nüüd teisenda sisendi DTO `Test` entiteediks. `TestMapper` on juba olemas (`persistence/test/TestMapper.java`) — lisa sinna uus meetod.

Suund on seekord **vastupidi** kui GET-endpointides:

```java
EntiteetTüüp toEntiteetKlassiNimi(SisendDtoTüüp sisendDto);
```

> **IntelliJ vihje:** Kliki `target = ""` jutumärkide vahele → **Ctrl+Space**. Nüüd näitab IntelliJ **entiteedi** välju, sest target on entiteet.

```java
@Mapping(ignore = true, target = "id")
@Mapping(source = "dtoVäli", target = "entiteediVäli")
@Mapping(ignore = true, target = "väliMidaDtoEiAnna")
EntiteetTüüp toEntiteetKlassiNimi(SisendDtoTüüp sisendDto);
```

> **Mõtle iga `Test` välja juures:** kas see tuleb otse DTO-st, või tuleb see mujalt?
> - `id` — genereerib andmebaas
> - `competence`, `competenceLevel`, `createdBy` — need on **objektid** (`@ManyToOne`), DTO-s on aga ainult id-d
> - `status`, `createdAt`, `updatedAt` — neid DTO-s üldse pole
>
> Kõik sellised → `ignore = true`. Nende väärtused määrad service kihis.

> **Tüübid:** DTO-s on `passPercent` tõenäoliselt `Integer`, entiteedis `BigDecimal`. MapStruct oskab selle ise teisendada.

**Kontroll:** iga `Test` target-väli peab olema kaardistatud kas `source`-iga või `ignore = true`-ga.

---

## Samm 5 — Service: puuduvad väljad ja testi salvestamine

### Mida teha?

Mapperist tulnud entiteedil on veel tühjad kõik väljad, mis said `ignore = true`. Täida need service'is enne salvestamist.

- **`competenceLevel`** — leia `competenceLevelId` järgi. Kui ei leia → `PrimaryKeyNotFoundException` (märkmetes on see veaolukord kirjas).
- **`competence`** — mõtle: kas see tuleb `competenceId` järgi eraldi päringuga, või on see leitud kompetentsi taseme küljes juba olemas?
- **`createdBy`** — kasutaja, kelle leidsid Samm 3-s.
- **`status`** — `Status` enum, mitte kõvakodeeritud `"A"`.
- **`createdAt`, `updatedAt`** — mis hetk see on? Vaata entiteedist, mis tüüpi need väljad on.

Seejärel salvesta:

```java
entiteetRepository.save(entiteet);
```

> **Mõtle:** `save()` salvestab testi ja annab sellele `id`. Kas vajad salvestatud testi järgmises sammus? (Vihje: `test_question` tabel viitab testile.)

---

## Samm 6 — Service: küsimuste salvestamine (`test_question`)

### Mida teha?

Iga `questions` listi elemendi kohta tuleb `test_question` tabelisse lisada üks rida. Märkmed:

> iga küsimus test_question tabelisse; position tuleb questions listi järjekorrast (1, 2, 3 ...)

Iga rea jaoks on vaja:
- **`test`** — Samm 5-s salvestatud test
- **`question`** — leia `questionId` järgi (kui ei leia → `PrimaryKeyNotFoundException`)
- **`position`** — järjekorranumber, alates **1**-st
- **`addedBy`** — sama kasutaja
- **`createdAt`, `updatedAt`**

> **Mõtle:** Millist tsüklit kasutad, et teaksid iga küsimuse **järjekorranumbrit**? Kas `for-each` annab selle kätte, või on vaja tavalist `for`-tsüklit indeksiga? Kui indeks algab 0-st, aga `position` 1-st — mida teha?

> **Repositoorium:** `TestQuestionRepository` on olemas. Kas `save()` piisab, või on mugavam koguda kõik read listi ja salvestada korraga (`saveAll()`)?

Soovitus: tõsta ühe `TestQuestion` loomine **eraldi private meetodisse** — nii jääb peameetod loetavaks.

### `@Transactional`

Kujuta ette: test on salvestatud, aga kolmas küsimus on vale id-ga ja tuleb `PrimaryKeyNotFoundException`. Mis seisu jääb andmebaas?

> **Mõtle:** Kuidas tagada, et kas **kõik** salvestub, või **mitte midagi**? Spring pakub selleks service meetodile ühe annotatsiooni.

---

## Samm 7 — tagasi RestController'isse

### Mida teha?

Märkmed ütlevad `Response (200): NONE`. Kontrolli, et kontrolleri meetodi tagastustüüp klapib Samm 1 alguses tehtud otsusega — kui midagi tagastada pole vaja, jääb meetod `void`-iks ja Spring vastab ise `200 OK`.

---

## Samm 8 — kood ilusaks (refactor)

### Make it work → Make it beautiful

Kui kood töötab, vaata, kas saab koodi puhtamaks muuta.

**Extract Method IntelliJ'ga:**

Märgi service meetodis koodilõik, mida soovid eraldada helper meetodiks → paremklõps → Refactor → Extract Method.

> **Tähelepanu:** IntelliJ kasutab ekstraktimisel kogu objekti parameetrina.
> Vaata üle, kas helper meetod vajab tegelikult kogu objekti või ainult üht välja — ja tee vajadusel korrektuur.

Head kandidaadid selles taskis:
- rolli kontroll
- puuduvate väljade täitmine testil
- ühe `TestQuestion` loomine

### Meetodite järjekord

1. `public` meetodid enne
2. `private` meetodid pärast
3. Järjesta väljakutsumise hierarhia järgi — peameetod üleval, helper meetodid all

Lisaks: **Ctrl+Alt+O** eemaldab kasutamata impordid.

---

## Kokkuvõte ja kontrollnimekiri

- [ ] `TestController`-is on uus meetod `@PostMapping`, `@Valid @RequestBody` ja õige tagastustüübiga
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponses` (200, 400, 403, 404)
- [ ] `TestCreateRequestDto` väljad klapivad märkmete JSON-iga; `questions` on list DTO-dest
- [ ] Kohustuslikel väljadel on valideerimisannotatsioonid (`timerMin` on lubatud `null`)
- [ ] Kasutaja leitakse id järgi; puudumisel `PrimaryKeyNotFoundException`
- [ ] Vale rolli korral `ForbiddenException` teatega `Error` enumist (`NO_PERMISSION`)
- [ ] Mapper: suund DTO → entiteet, `id` ja kõik seosed/süsteemsed väljad `ignore = true`, kõik target-väljad kaardistatud
- [ ] `status` tuleb `Status` enumist, mitte `"A"`
- [ ] Iga küsimus salvestatakse `test_question`-isse, `position` algab 1-st
- [ ] Service meetodil on `@Transactional`
- [ ] Meetodite järjekord: `public` enne, `private` pärast
- [ ] Kood kompileerub ja endpoint on Swaggeris nähtav

---

> **Järgmine samm:** Testi endpointi Swagger UI kaudu (`http://localhost:8080/swagger-ui/index.html`) märkmetes toodud näidis-JSON-iga.
> Kontrolli andmebaasist, et `test` tabelisse tekkis uus rida ja `test_question` tabelisse iga küsimuse kohta rida õige `position`-iga.
> Proovi ka veaolukordi: tühi `testName`, olematu `questionId`, KASUTAJA rolliga `userId`.
