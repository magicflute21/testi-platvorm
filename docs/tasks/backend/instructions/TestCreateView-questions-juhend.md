# Juhend: GET /api/questions?competenceLevelId={competenceLevelId}

**Taski fail:** `docs/balsamic/notes/TestCreateView-markmed.md` (sektsioon "API märkmed — GET /api/questions")
**Kontroller:** `QuestionController.java`
**Implementeerimise voog:** RestController → Service → Repository → Service → Mapper → RestController

---

## Sissejuhatus

See endpoint täidab vaate "Testi koostamine" rippmenüü **"Vali küsimus"**. Kui kasutaja on valinud kompetentsi taseme, küsib frontend backendilt selle taseme aktiivseid küsimusi. Vastuses on iga küsimuse kohta `questionId` ja `questionTitle`.

Ülesehitus on sama, mis tasemete dropdownil (`GET /api/competence-levels`). Sisendiks on `@RequestParam`, filtreeritakse seotud objekti id ja staatuse järgi ning tulemus mapitakse listiks. Seekord on sisendiks **kompetentsi taseme** id (`competence_level.id`), mitte kompetentsi id.

> **Eripära:** `Question` entiteet asub praegu otse `persistence/` kaustas, mitte oma alampaketis (nagu `persistence/competence/Competence.java`). Samm 2 juures tõstame selle õigesse kohta.

---

## Samm 1 — RestController

### Mida teha?

API tee on `/api/questions` — see on omaette ressurss. Kontrollerit veel pole, see tuleb luua.

- Kaust: `backend/src/main/java/ee/testiplatvorm/controller/`
- Mõtle, mis nimega alampakett sobib (vt `competence/`, `competencelevel/`)

Vajalikud klassiannotatsioonid:

```java
@RestController
@RequiredArgsConstructor
public class KontrolleriKlass {
    // ...
}
```

### Meetodi loomine

Alusta tühjast meetodist ja lisa seejärel:
1. `@GetMapping` kogu teega (projekti tava: ilma klassitasemel `@RequestMapping`-uta)
2. `@RequestParam` — mõtle, **mis nimega** parameeter URL-is on
3. `@Operation` ja `@ApiResponse` — summary kirjeldagu täpselt, milliseid küsimusi tagastatakse

> **Mõtle:** Vaata `CompetenceLevelController` meetodit — see teeb väga sarnast asja.

### Service klassi ettevalmistus

Service klassi veel pole. Lisa kontrollerisse väli ja kutsu meetod välja — **Alt+Enter** punasel klassinimel → **Create class**, pakett `ee.testiplatvorm.service`.

---

## Samm 2 — Service ja repository päring

### Entiteedi paika tõstmine

Enne repositooriumi loomist tõsta `Question.java` oma alampaketti:

1. Paremklõps `Question.java` → **Refactor → Move Class** (**F6**)
2. Sihtpakett: `ee.testiplatvorm.persistence.<sobiv nimi>`
3. IntelliJ uuendab ise kõik importid teistes entiteetides

> **Ära tõsta faili käsitsi** — muidu jäävad teiste klasside importid katki.

### Repository

Mõtle: **millisest tabelist** päring algab ja **millise veeru järgi** filtreeritakse?

- Repositooriumi pole veel — loo see entiteedi kõrvale samasse paketti, laiendades `JpaRepository`-t
- Kas `findById()` sobib? Sisendiks on kompetentsi taseme id, mitte küsimuse id

> **Rusikareegel:** Kui päringusse läheb sisendina muu väärtus kui tabeli `id`, on vaja **uut meetodit**.

> **Staatus:** kasuta `Status` enumit nagu `CompetenceLevelService`-is.

Pane repository tulemus kohe muutujasse.

---

## Samm 3 — DTO klass

Vastuse kuju taskist: `QuestionResponseDto` väljadega `questionId` ja `questionTitle`.

- Pakett: kontrolleri alampaketi `dto/` kaust
- **Väljanimed kopeeri märkmete JSON-ist** — need on leping frontendiga
- Lomboki annotatsioonid samad, mis `CompetenceLevelResponseDto`-l
- Väljad `private`

---

## Samm 4 — Mapper

Mapper tuleb entiteedi kõrvale (`persistence/.../QuestionMapper.java`) sama `@Mapper(...)` annotatsiooniga nagu teistel.

Vaja on kahte meetodit:

```java
// Üksik objekt — @Mapping annotatsioonid käivad siia
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);

// List — mitmuses, ilma annotatsioonideta
List<TagastatavDtoTüüp> toDtoKlassiNimid(List<EntiteetTüüp> entiteedid);
```

> **Suund:** entiteet → DTO. `source` = entiteedi väli, `target` = DTO väli.

> **IntelliJ vihje:** kliki `target = ""` jutumärkide vahele → **Ctrl+Space**.

Iga target-väli peab olema kaardistatud (`source` või `ignore = true`).

### Service meetodi lõpetamine

Kutsu list-mapper välja ja tagasta tulemus. **Alt+Enter** parandab `void` tagastustüübi.

---

## Samm 5 — Repository päring (JPA Buddy)

1. Repositooriumis paremklõps → **JPA Buddy** → **Query**
2. **Find collection**, wrap type `List<...>`
3. **Tingimused:** kaks — üks seotud kompetentsi taseme kohta, teine staatuse kohta
4. **Advanced** → **Named parameters**
5. **Order by** — mis järjekord oleks kasutajale dropdownis loogiline?

Pärast loomist:
- Meetodi nimi lühikeseks ja subjektiga (vt `findCompetenceLevelsBy`)
- Parameetri nimi täpseks (mitte `id`) — muuda ka `@Query` sees
- `@Param(...)` annotatsioonid võib eemaldada

> **Levinud viga:** vale seotud objekti id. `Question`-il on nii `competence` kui `competenceLevel` — kumba järgi filtreerida?

---

## Samm 6 — tagasi RestController'isse

Lisa `return` ja paranda tagastustüüp **Alt+Enter**-iga. Muutuja nimi tüübi järgi (`List<XDto> xDtos`).

---

## Samm 7 — kood ilusaks

- **Ctrl+Alt+O** — kasutamata impordid
- Tühjad read annotatsioonide ja meetodi vahel ära
- `public` meetodid enne `private` omi

---

## Kokkuvõte ja kontrollnimekiri

- [ ] `Question.java` asub oma alampaketis `persistence/...`
- [ ] Kontroller: `@RestController`, `@RequiredArgsConstructor`, õige alampakett
- [ ] `@GetMapping("/api/questions")` + `@RequestParam` õige nimega
- [ ] `@Operation` summary kirjeldab täpselt, `@ApiResponse` olemas
- [ ] Service: `@Service`, `@RequiredArgsConstructor`
- [ ] Repository `@Query` filtreerib **kompetentsi taseme** ja aktiivse staatuse järgi, sorteerib mõistlikult
- [ ] `Status` enum, mitte kõvakodeeritud `"A"`
- [ ] `QuestionResponseDto` väljad klapivad märkmete JSON-iga
- [ ] Mapper: suund entiteet → DTO, kõik target-väljad kaardistatud, list-meetod mitmuses
- [ ] Kood kompileerub, endpoint Swaggeris nähtav

---

> **Testi Swaggeris** (`http://localhost:8080/swagger-ui/index.html`):
> - `competenceLevelId=1` → "Mis on sulund (closure)?"
> - `competenceLevelId=2` → "Millised järgnevatest on JS primitiivtüübid?"
> - `competenceLevelId=3` → "Kas SQL-i võtmesõnad on tõstutundlikud?"
