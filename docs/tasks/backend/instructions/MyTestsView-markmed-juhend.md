# Juhend: GET /api/me/user-tests

**Taski fail:** `docs/balsamic/notes/MyTestsView-markmed.md` (sektsioon "API märkmed — GET /api/me/user-tests")
**Kontroller:** `UserTestController.java` (uus, paketis `controller/usertest/`)
**Implementeerimise voog:** RestController → Service → Repository → Service → Mapper → RestController

---

## Sissejuhatus

See endpoint tagastab sisse logitud kasutajale määratud testide nimekirja, mida frontend kuvab vaates `MyTestsView.vue` kaartidena. Päring läbib kõik kolm kihti: kontroller võtab päringu vastu, service küsib sessioonist kasutaja id ja pärib repositooriumist tema `user_test` read, mapper teisendab need `UserTestSummaryDto` listiks.

Selle harjutuse käigus õpid:
- kuidas kasutada juba olemasolevat teenust (`CurrentUserService`), et tuvastada kasutaja **sessioonist**, mitte URL-ist
- kuidas kirjutada JPA Buddy abil päringut, mis filtreerib nii oma tabeli kui ka **seotud tabeli** välja järgi
- kuidas teha MapStruct'is üksiku objekti ja listi mapper meetodi paari

**Taskist tuvastatud info:**

| | |
|---|---|
| HTTP meetod | `GET` |
| API tee | `/api/me/user-tests` (parameetreid pole — userId tuleb sessioonist) |
| RequestBody | puudub |
| ResponseBody | `List<UserTestSummaryDto>` — väljad `userTestId`, `testId`, `testName`, `testShortDescription`, `userTestStatus` |
| Tingimused | `user_test.user_id` = sisse logitud kasutaja **ja** seotud `test.status = 'A'` |
| Tühi tulemus | tühi list `[]` (mitte viga!) |
| Veaolukorrad | eraldi veateateid pole; sisse logimata kasutaja puhul viskab `CurrentUserService` ise `401 Unauthorized` |
| DB tabelid | `user_test`, `test` |

---

## Samm 1 — RestController

### Mida teha?

Selle endpointi jaoks pole veel sobivat kontrollerit. `TestAttemptController` käsitleb ühe testi sooritamist, `TestController` kõiki teste — aga see endpoint räägib **kasutajale määratud testidest** (`user_test` tabel). Loo seega uus kontroller.

Kontrolli esmalt, kas vastav kontrolleri klass juba eksisteerib:
- Kaust: `backend/src/main/java/ee/testiplatvorm/controller/`
- Kui **puudub** → loo uus alampakett (nt `usertest`) ja sinna uus klass IntelliJ'ga (File → New → Java Class)
- Kui **on olemas** → ava see klass ja lisa sinna uus meetod

Vajalikud klassiannotatsioonid (kui lood uue kontrolleri):

```java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KontrolleriKlass {
    // ...
}
```

### Meetodi loomine

Alusta meetodist **ilma mappingannotatsioonideta** — nii saad kõigepealt loogika paika:

```java
public void meetodiNimi() {
    // tühi meetod esialgu
}
```

> **Mõtle:** Mis on selle meetodi hea nimi? Nimi peaks kirjeldama, mida meetod teeb.
> Vaata ka, kuidas on nimetatud sarnased meetodid olemasolevates kontrollerites.

> **Mõtle:** Miks sellel meetodil **pole parameetreid**, kuigi tagastatakse ühe konkreetse kasutaja andmed?
> Loe taski "API teenuse lisainfo" lõik uuesti läbi — mis juhtuks turvalisusega, kui `userId` tuleks URL-ist?

Seejärel lisa:
1. **Mappingannotatsioon** — `@GetMapping`
2. **Swagger annotatsioonid** — `@Operation` ja `@ApiResponses` (mõtle, kas lisaks `200`-le tasub dokumenteerida ka `401`)

### Service klassi ettevalmistus

Enne kui kontrollerist service meetodit välja kutsud, kontrolli, kas sobiv service klass juba eksisteerib:
- Kaust: `backend/src/main/java/ee/testiplatvorm/service/`
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
public void meetodiNimi() {
    teenuseMuutuja.meetodiNimi();
}
```

> **IntelliJ vihje:** Kui `teenuseMuutuja.meetodiNimi()` on punasega alla joonitud,
> vajuta **Alt+Enter** punasel joonel → vali **"Create method in TeenusKlass"**.
> IntelliJ loob automaatselt vastava meetodi service klassi!

---

## Samm 2 — Service ja repository päring

### Mida teha?

Nüüd liigume service meetodisse. Sisse ei tule ühtegi parameetrit — seega esimene küsimus on: **kust service teada saab, kelle testid tagastada?**

### Kasutaja tuvastamine sessioonist

Projektis on juba olemas teenus, mis oskab sessioonist sisse logitud kasutaja id kätte saada. Leia see kaustast `service/` ja vaata, mida selle meetod teeb, kui kasutaja pole sisse logitud.

Lisa see teenus oma service klassi väljana (sama moodi nagu repositoorium allpool) ja kutsu selle meetod välja.

> **Meetodi palve:** Kui sa kutsud välja mingi meetodi, mis tagastab midagi, ja sa soovid selle infoga midagi edasi teha, siis **pane see kohe muutujasse**.

### Repository ühenduse loomine

Mõtle: **millisest tabelist** on vaja andmeid pärida? Vaata taski märkmetest, mille ridu tagastatakse.

Alusta kirjutama repositooriumi muutuja nime service meetodis:

```java
public void meetodiNimi() {
    entiteetRep  // <- kirjuta algus siia
}
```

> **IntelliJ vihje:** Kirjuta muutuja nime algus ja IntelliJ pakub automaatselt vastavat repositooriumi.
> Vajuta **Tab** → repositoorium lisatakse klassiväljana!

**Küsi endalt:** Kas JPA pakub valmis meetodit, mis leiab read **kasutaja id** järgi **ja** kontrollib samal ajal seotud testi staatust?

> **Rusikareegel:** Kui päringusse läheb sisendina muu väärtus kui tabeli `id`,
> on tõenäoliselt vaja **uut meetodit** teha (vt Samm 5 "Uue meetodi loomine JPA Buddy abil").

> **Vihje:** Vaata olemasolevat repository't selle entiteedi kõrval — seal on juba üks päring, mis filtreerib seotud tabeli staatuse järgi. Sealt näed mustrit. Staatuse väärtuseid ei kirjutata projektis "kõvasti" koodi sisse — vaata, kuidas olemasolev service need päringule kaasa annab.

Kui repository meetod on olemas, pane tulemus kohe muutujasse. Mõtle: kas tulemus on üks objekt, `Optional` või list?

---

## Samm 3 — DTO klass

### Mida teha?

Kui entity list on käes, on aeg luua väljundi DTO klass `UserTestSummaryDto`.

Mõtle: kas vastav DTO klass on juba olemas?
- Vaata kaustast: `backend/src/main/java/ee/testiplatvorm/controller/.../dto/`

**Kui DTO puudub** → kasuta JPA Buddy abi:

1. Paremklõps entity klassil → New → DTO
2. Kontrolli valikud:
    - **Package** → sinu uue kontrolleri alampakett + `.dto`
    - **DTO class name** → `UserTestSummaryDto` (taski märkmetes antud)
    - **MapStruct Interface** → vali olemasolev mapper (entiteedi kõrval on see juba olemas!)
    - **Mutable** → jäta märgituks
3. Vali väljad — seotud entiteedi väljade jaoks vali **Flat** struktuur
4. Peale loomist kontrolli DTO klass üle ja **nimeta väljad ümber** nii, et need klapiksid täpselt taski näidis-JSON-iga

> **Mõtle:** Näidis-JSON-is on 5 välja. Mitu neist tuleb otse `user_test` tabelist ja mitu seotud `test` tabelist?

---

## Samm 4 — Mapper

### Mida teha?

Entity list tuleb nüüd teisendada DTO listiks. Kõik vajalikud andmed on ühe entity (ja tema seotud testi) küljes olemas — lisapäringuid pole vaja.

### Mapper

Ava entiteedi kõrval olev mapper interface. Kui JPA Buddy lisas sinna uusi meetodeid, eemalda mittevajalikud ja jäta vaid need, mida tegelikult vajad.

Nimeta meetodid ümber konventsiooni järgi:

```java
// Ühele DTO-le
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);

// Lista DTO listiks
List<TagastatavDtoTüüp> toDtoKlassiNimid(List<EntiteetTüüp> entiteedid);
```

> **Tähelepanu:** `@Mapping` annotatsioonid käivad alati **üksiku objekti** meetodile (ainsuses).
> List-meetod (mitmuses) jääb **ilma annotatsioonideta** — MapStruct genereerib selle ise, kutsudes iga elemendi kohta üksiku objekti meetodit.

Lisa `@Mapping` annotatsioonid:

```java
@Mapping(source = "", target = "")
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);
```

> **IntelliJ vihje:** Kliki `target = ""` jutumärkide vahele → vajuta **Ctrl+Space**.
> IntelliJ näitab, mitu välja DTO-l on — nii saad luua ettevalmistatud `@Mapping` malli.

Näiteks kui DTO-l on 3 välja, tekib selline mall:

```java
@Mapping(source = "", target = "")
@Mapping(source = "", target = "")
@Mapping(source = "", target = "")
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);
```

Täida kõik väljad — **iga** DTO target-väli peab olema kaardistatud (`source`-iga, ka siis kui nimi kattub). Seotud objekti välja poole saad viidata punktiga:

```java
@Mapping(source = "seotudObjekt.id", target = "seotudObjektiId")
@Mapping(source = "tavaveerg", target = "samaNimiDtos")
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);
```

### Service meetodi lõpetamine

Kutsu mapperi **list-meetod** service meetodis välja ja tagasta tulemus:

```java
public void meetodiNimi() {
    // 1. kasutaja id sessioonist
    // 2. repository päring -> entiteetide list
    List<TagastatavDtoTüüp> dtod = mapperMuutuja.toDtoKlassiNimid(entiteedid);
    return dtod;
}
```

> **IntelliJ vihje:** Meetodi tagastustüüp on veel `void`, aga `return` lause on sees.
> Vajuta **Alt+Enter** punase joone peal → IntelliJ parandab tagastustüübi automaatselt!

> **Mõtle:** Mis juhtub, kui kasutajale pole ühtegi testi määratud? Kas pead selle jaoks midagi eraldi kirjutama, või tagastab repository + mapper juba ise tühja listi?

---

## Samm 5 — Repository (uus päring JPA Buddy abil)

### Mida teha?

Kui Samm 2 juures selgus, et sobivat päringut veel pole, loo see nüüd. Päring peab leidma kõik `user_test` read, mis kuuluvad antud kasutajale **ja** mille seotud test on aktiivne.

### Uue meetodi loomine JPA Buddy abil

Mine repository interface'i faili. Kasuta **JPA Buddy** funktsionaalsust:

1. Ava JPA Buddy paneel (paremklõps repository klassis → JPA Buddy)
2. Valikutes **Method** ja **Query** vali → **Query**
3. Vali meetodi tüüp:
    - **Find instance** — üksiku rea leidmiseks
    - **Find collection** — mitme rea leidmiseks
    - **Count** — loendamiseks
    - **Exists** — olemasolu kontrollimiseks
4. Määra **Wrap type**:
    - Üksiku rea puhul — kaaluda `Optional<EntiteetKlass>`
    - Mitme rea puhul — `List<EntiteetKlass>`
5. Lisa **query conditionid** — milliseid veerge filtreeritakse (mõtle: mitu tingimust on taskis?)
6. **Advanced** sektsioonis: vali alati **Named parameters**
7. Mitme reaga tulemuse puhul mõtle läbi **Order By Attributes** — mis järjekorras oleks kasutajal mõistlik oma teste näha?

Peale meetodi loomist:
- Kontrolli parameetrite nimed — ebamäärane `id` või `status` asenda konkreetsemaga (nt millise tabeli staatus see on?)
- Tee vastav muudatus ka `@Query` annotatsiooni nimetud parameetris
- Eemalda ebavajalikud `@Param()` annotatsioonid, kui Named parameters on kasutusel
- Hoia meetodi nimi lühike ja loetav — vaata, kuidas on nimetatud sama faili olemasolev meetod

---

## Samm 6 — tagasi RestController'isse

### Mida teha?

Service meetod on nüüd valmis ja tagastab DTO listi. Naase kontrolleri meetodisse.

Täienda kontrolleri meetodit — lisa `return` lause:

```java
public void meetodiNimi() {
    teenuseMuutuja.meetodiNimi();  // <- enne: tulemus kasutamata
}
```

> **IntelliJ vihje:** Lisa `return` lause. IntelliJ kurdab, et `void` ei saa midagi tagastada —
> vajuta **Alt+Enter** → "Change return type".

Tulemus:

```java
public TagastatavTüüp meetodiNimi() {
    return teenuseMuutuja.meetodiNimi();
}
```

---

## Samm 7 — kood ilusaks (refactor)

### Make it work → Make it beautiful

Kui kood töötab, on aeg vaadata, kas saab koodi puhtamaks muuta.

**Extract Method IntelliJ'ga:**

Märgi service meetodis koodilõik, mida soovid eraldada helper meetodiks → paremklõps → Refactor → Extract Method.

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

- [ ] RestController klass on olemas vajaliku `@RestController`, `@RequestMapping`, `@RequiredArgsConstructor` annotatsiooniga
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponses` annotatsioonid
- [ ] Kontrolleri meetod **ei võta** `userId`-d parameetrina — kasutaja tuleb sessioonist
- [ ] Service klass on olemas vajaliku `@Service`, `@RequiredArgsConstructor` annotatsiooniga
- [ ] Repository interface on olemas ja laiendab `JpaRepository`-t
- [ ] Repository meetoditel on `@Query` annotatsioon Named parameters stiilis
- [ ] Staatuse väärtus tuleb `Status` enumist, mitte kõvakodeeritud stringina
- [ ] Mapper interface on olemas `componentModel = spring` seadistusega
- [ ] Kõik `@Mapping` annotatsioonid on täidetud — ükski DTO väli ei ole kaardistamata
- [ ] List-mapper meetod on mitmuses ja ilma `@Mapping` annotatsioonideta
- [ ] Meetodite järjekord: `public` enne, `private` pärast — järjesta ka väljakutsumise hierarhia järgi
- [ ] Kood kompileerub ja Swagger UI kaudu on endpoint nähtav

---

> **Järgmine samm:** Testi endpointi Swagger UI kaudu (`http://localhost:8080/swagger-ui/index.html`).
> NB! Kuna kasutaja tuleb sessioonist, pead enne sama brauseri seansis sisse logima (`/api/login`) —
> muidu saad vastuseks `401`. Kontrolli ka, et vastus vastab taski näidis-JSON-ile ja et mitteaktiivse testiga read ei tule kaasa.
