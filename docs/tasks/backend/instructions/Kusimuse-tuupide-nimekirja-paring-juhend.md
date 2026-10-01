# Juhend: GET /api/question-types

**Taski fail:** `Kusimuse-tuupide-nimekirja-paring.md`
**Kontroller:** uus kontroller küsimuse tüüpide jaoks (nt `QuestionTypeController.java`)
**Implementeerimise voog:** RestController → Service → Repository → Service → Mapper → RestController

---

## Sissejuhatus

See endpoint tagastab kõik küsimuse tüübid. Nendega täidetakse modali "Loo uus küsimus" rippmenüü "Küsimuse tüüp". Päringul pole sisendit ja klassifikaatortabelis pole `status` veergu, nii et siin ei ole filtreerimist ega veaolukordi, mida ise käsitleda. See on hea harjutus puhta GET-voo jaoks: kontroller → service → repository → mapper (list). Samuti vaatame, kas repository baasmeetodist piisab ja kas oma päringut on üldse vaja.

---

## Samm 1 — RestController

### Mida teha?

Küsimuse tüüpide jaoks eraldi kontrollerit veel pole. Kaustas `controller/` on iga ressursi jaoks oma alampakett (nt `controller/question/`, `controller/competencelevel/`). Mõtle, kas küsimuse tüübid on eraldi ressurss (oma URL `/api/question-types`) või kuuluvad olemasoleva kontrolleri alla. Vaata, kuidas on tehtud sarnane klassifikaatori päring `CompetenceLevelController`.

Kontrolli esmalt, kas vastav kontrolleri klass juba eksisteerib:
- Kaust: `backend/src/main/java/ee/testiplatvorm/controller/`
- Kui **puudub**, loo uus alampakett ja klass IntelliJ-ga (File → New → Java Class)
- Kui **on olemas**, ava see klass ja lisa sinna uus meetod

Vajalikud klassiannotatsioonid (kui lood uue kontrolleri):

```java
@RestController
@RequiredArgsConstructor
public class KontrolleriKlass {
    // ...
}
```

> **Projekti eripära:** Osa kontrollereid kasutab klassi peal `@RequestMapping("/api")`, osa kirjutab täistee otse `@GetMapping`-usse. Vaata paari olemasolevat kontrollerit ja vali üks stiil. Oluline on, et lõplik tee oleks `/api/question-types`.

### Meetodi loomine

Alusta meetodist **ilma mappingannotatsioonideta**. Nii saad kõigepealt loogika paika:

```java
public void meetodiNimi() {
    // tühi meetod esialgu
}
```

> **Mõtle:** Mis on selle meetodi hea nimi? Meetod tagastab **kõik** tüübid. Vaata, kuidas on nimetatud `TestController`-i meetod, mis tagastab kõik testid.

Seejärel lisa:
1. **Mappingannotatsioon**: `@GetMapping`
2. **Parameetrite annotatsioonid**: siin pole ühtegi, sest sisendit pole
3. **Swagger annotatsioonid**: `@Operation` ja `@ApiResponse` (ainult `200`, sest kohandatud veateateid pole)

### Service klassi ettevalmistus

Kontrolli, kas küsimuse tüüpide service klass on olemas:
- Kaust: `backend/src/main/java/ee/testiplatvorm/service/`
- Kui **puudub**, loo uus klass

```java
@Service
@RequiredArgsConstructor
public class TeenusKlass {
    // ...
}
```

Lisa service muutuja kontrollerisse:

```java
private final TeenusKlass teenuseMuutuja;
```

Kutsu service meetodit välja:

```java
public void meetodiNimi() {
    teenuseMuutuja.meetodiNimi();
}
```

> **IntelliJ vihje:** Kui `teenuseMuutuja.meetodiNimi()` on punasega alla joonitud,
> vajuta punasel joonel **Alt+Enter** ja vali **"Create method in TeenusKlass"**.

---

## Samm 2 — Service ja repository päring

### Mida teha?

Service meetod ei saa sisendit. Selle ülesanne on tuua tabelist `question_type` kõik read. Entiteet `persistence/QuestionType.java` ja `persistence/QuestionTypeRepository.java` on juba olemas, nii et repository't ei pea looma.

Alusta repository muutuja nime kirjutamist:

```java
public void meetodiNimi() {
    entiteetRep  // <- kirjuta algus siia
}
```

> **IntelliJ vihje:** Kirjuta muutuja nime algus ja vajuta **Tab**. Repository lisatakse klassiväljana.

**Küsi endalt:** Kas `JpaRepository` pakub juba meetodit, mis tagastab **kõik** read? Tabelis pole `status` veergu, seega pole midagi filtreerida.

> **Rusikareegel:** Kui päringus pole ühtegi tingimust, pole tavaliselt vaja uut JPQL meetodit teha.

Kui repository meetod tagastab listi, **pane tulemus kohe muutujasse** (meetodi palve).

---

## Samm 3 — DTO klass

### Mida teha?

Taskifaili järgi on väljund `QuestionTypeResponseDto` kahe väljaga. Nende nimed erinevad entiteedi väljade nimedest (`questionTypeId` vs `id`, `questionTypeName` vs `name`).

Kontrolli, kas DTO on juba olemas:
- Kaust: `backend/src/main/java/ee/testiplatvorm/controller/<sinu-alampakett>/dto/`

**Kui DTO puudub**, kasuta JPA Buddy abi:

1. Tee entity klassil paremklõps ja vali New → DTO
2. Kontrolli valikud:
    - **Package**: kontrolleri alampaketi `dto` kaust
    - **DTO class name**: taskifailis antud nimi
    - **MapStruct Interface**: loo uus plussmärgiga (vt Samm 4, kuhu mapper panna)
    - **Mutable**: jäta märgituks
3. Vali väljad
4. Pärast loomist nimeta väljad ümber nii, et need vastaksid taskifaili "Väljund" JSON-ile

---

## Samm 4 — Mapper

### Mida teha?

Entiteetide list tuleb teisendada DTO-de listiks.

> **Projekti eripära:** Mapperid asuvad selles projektis `persistence/<entiteet>/` paketis, entiteedi kõrval (vt `CompetenceLevelMapper`, `QuestionMapper`). `QuestionType` asub aga otse `persistence/` juurpaketis, ilma oma alampaketita. Mõtle, kuhu mapper sel juhul kõige loogilisemalt sobib.

Kui JPA Buddy lõi mapperisse mitu meetodit, jäta alles ainult need, mida vajad. Nimeta need konventsiooni järgi:

```java
// Ühele DTO-le
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);

// Entiteetide list DTO-de listiks
List<TagastatavDtoTüüp> toDtoKlassiNimid(List<EntiteetTüüp> entiteedid);
```

> **NB!** `@Mapping` annotatsioonid käivad **ainult üksiku objekti** meetodile. List-meetod jääb annotatsioonideta. MapStruct genereerib selle ise ja kutsub iga elemendi jaoks üksiku objekti meetodit.

Lisa `@Mapping` annotatsioonid:

```java
@Mapping(source = "", target = "")
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);
```

> **IntelliJ vihje:** Kliki `target = ""` jutumärkide vahele ja vajuta **Ctrl+Space**.
> IntelliJ näitab kõiki DTO välju. Tee iga välja jaoks oma `@Mapping` rida.

Täida kõik target-väljad. Igal real peab olema kas `source = "..."` või `ignore = true`. Ükski DTO väli ei tohi jääda kaardistamata.

### Service meetodi lõpetamine

```java
public void meetodiNimi() {
    List<EntiteetTüüp> entiteedid = entiteetRepository.meetodiNimi();
    List<TagastatavDtoTüüp> dtod = mapperMuutuja.toDtoKlassiNimid(entiteedid);
    return dtod;
}
```

> **IntelliJ vihje:** Tagastustüüp on veel `void`, aga meetodis on `return`. Vajuta **Alt+Enter** ja IntelliJ parandab tagastustüübi.

---

## Samm 5 — Repository (täiendavad päringud)

Selle taski puhul pole tõenäoliselt täiendavaid päringuid vaja. Kui aga otsustad siiski teha oma meetodi (nt järjestamiseks `id` järgi), kasuta **JPA Buddy** abi:

1. Tee repository interface'is paremklõps ja vali JPA Buddy
2. Vali **Query** → **Find collection**
3. **Wrap type**: `List<EntiteetKlass>`
4. Mõtle läbi **Order By Attributes**
5. Nimeta meetod nii, et nimest oleks näha, mida see tagastab (vt backend/CLAUDE.md "Repositooriumi meetodi nimetamine")

---

## Samm 6 — tagasi RestController'isse

Lisa kontrolleri meetodisse tulemuse muutuja ja `return`:

```java
public void meetodiNimi() {
    teenuseMuutuja.meetodiNimi();  // <- enne: tulemus kasutamata
}
```

> **IntelliJ vihje:** Kui lisad `return`, kurdab IntelliJ, et `void` ei saa midagi tagastada. Vajuta **Alt+Enter** ja vali "Change return type".

Tulemus:

```java
public List<TagastatavTüüp> meetodiNimi() {
    List<TagastatavTüüp> tulemused = teenuseMuutuja.meetodiNimi();
    return tulemused;
}
```

---

## Samm 7 — kood ilusaks (refactor)

### Make it work → Make it beautiful

Kui kood töötab, vaata üle, kas seda saab puhtamaks muuta. See endpoint on lühike, nii et siin keskendu peamiselt nimedele:
- Kas meetodi nimed ütlevad, mida need tagastavad?
- Kas muutuja nimed peegeldavad täistüüpi (nt `EntityDto entityDto`, mitte `dto`)?
- Kas `@Operation` summary kirjeldab täpselt, mida endpoint teeb?

**Extract Method IntelliJ-ga:** märgi koodilõik, tee paremklõps ja vali Refactor → Extract Method. Kontrolli, kas helper meetod vajab kogu objekti või ainult üht välja.

### Meetodite järjekord

1. `public` meetodid ees
2. `private` meetodid järel
3. Järjesta väljakutsumise hierarhia järgi: peameetod üleval, helperid all

---

## Kokkuvõte ja kontrollnimekiri

- [ ] RestController klassil on `@RestController` ja `@RequiredArgsConstructor` (ning vajadusel `@RequestMapping`)
- [ ] Endpointi lõplik tee on `/api/question-types`
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponse` annotatsioonid
- [ ] Service klassil on `@Service` ja `@RequiredArgsConstructor`
- [ ] Repository laiendab `JpaRepository`-t (on juba olemas)
- [ ] Mapper on `@Mapper(... componentModel = SPRING)` annotatsiooniga ja õiges paketis
- [ ] Iga DTO target-väli on mapperis kaardistatud (`source` või `ignore = true`)
- [ ] List-meetod on mitmuses ja ilma `@Mapping` annotatsioonideta
- [ ] Meetodite järjekord: `public` ees, `private` järel
- [ ] Endpoint on Swagger UI-s nähtav ja tagastab 3 tüüpi õigete id-dega
- [ ] Endpointi jaoks on automaattest

---

> **Järgmine samm:** Testi endpointi Swagger UI kaudu (`http://localhost:8080/swagger-ui/index.html`).
> Kontrolli, et vastuses on `SINGLE_CHOICE`, `MULTIPLE_CHOICE` ja `TRUE_FALSE` koos id-dega 1, 2, 3.
