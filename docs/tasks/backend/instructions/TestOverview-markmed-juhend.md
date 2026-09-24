# Juhend: GET /api/tests

**Taski fail:** `docs/balsamic/notes/TestOverview-markmed.md`
**Kontroller:** `TestController.java`
**Implementeerimise voog:** RestController → Service → Repository → Service → Mapper → RestController

---

## Sissejuhatus

See endpoint tagastab frontendi vaatele `TestOverview.vue` kõikide testide nimekirja, et iga test saaks kuvada eraldi kaardina (nimi, staatuse silt, kirjeldus). Päring läbib kõik kolm kihti: kontroller võtab päringu vastu, service küsib repositooriumist andmed ja mapper teisendab entiteedid response DTO-deks. Selle harjutuse käigus õpid, kuidas tagastada **listi** DTO-sid ja kuidas MapStruct üksiku objekti ning listi mappimist omavahel seob.

---

## Samm 1 — RestController

### Mida teha?

Kontrolleri klass `TestController` on **juba olemas** kaustas `controller/test/` ja sinna on juba lisatud ka esimene tühi meetod koos service väljakutsega. Nüüd tuleb see meetod endpointiks vormistada: lisada mappingannotatsioon ja Swaggeri annotatsioonid.

Kontrolli esmalt, kas vastav kontrolleri klass juba eksisteerib:
- Kaust: `backend/src/main/java/ee/testiplatvorm/controller/`
- Kui **puudub** → loo uus klass IntelliJ'ga (File → New → Java Class)
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

> **Mõtle:** Vaata, kuidas on tee kirja pandud olemasolevas `LoginController`-is. Kas kasutad klassitasemel `@RequestMapping`-ut või kirjutad kogu tee mappingannotatsiooni sisse? Ole projektis järjepidev.

### Meetodi loomine

Alusta meetodist **ilma mappingannotatsioonideta** — see aitab kõigepealt loogika paika saada:

```java
public void meetodiNimi(SisendTüüp parameetriNimi) {
    // tühi meetod esialgu
}
```

> **Mõtle:** Mis on selle meetodi hea nimi? Nimi peaks kirjeldama, mida meetod teeb.
> Vaata HTTP meetodit ja API teed taskifailist — need annavad vihje.
> Kas sellel endpointil on üldse sisendparameetreid? Vaata API teed hoolikalt.

Seejärel lisa:
1. **Mappingannotatsioon** — `@GetMapping`
2. **Parameetrite annotatsioonid** — `@PathVariable` või `@RequestParam` (kui neid on vaja)
3. **Swagger annotatsioonid** — `@Operation` ja `@ApiResponses`

```java
@GetMapping("/mingi/rada")
@Operation(summary = "Lühikokkuvõte")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK")})
public TagastatavTüüp meetodiNimi() {
    return teenuseMuutuja.meetodiNimi();
}
```

> **Veaolukorrad:** Taskifaili järgi veateateid pole — kui ühtegi testi pole, tagastatakse lihtsalt tühi list `[]`. Mõtle, milliseid `@ApiResponse` ridu on siis tegelikult vaja.

### Service klassi ettevalmistus

Enne kui kontrollerist service meetodit välja kutsud, kontrolli, kas service klass juba eksisteerib:
- Kaust: `backend/src/main/java/ee/testiplatvorm/service/`
- Kui **puudub** → loo uus klass IntelliJ'ga (File → New → Java Class)

```java
@Service
@RequiredArgsConstructor
public class TeenusKlass {
    // ...
}
```

> **Tähelepanu:** Võrdle olemasoleva service klassi annotatsioone `LoginService`-iga. Kas `final` väljad (repository, mapper) saavad Spring'i poolt sisse süstitud, kui klassil on vale Lombok'i konstruktori annotatsioon?

Kui service klass on olemas (või just loodud), lisa service muutuja kontrolleri klassi:

```java
private final TeenusKlass teenuseMuutuja;
```

Kutsu service meetodit välja (esialgne tühi väljakutse):

```java
public void meetodiNimi() {
    teenuseMuutuja.meetodiNimi();
}
```

> **IntelliJ vihje:** Kui `teenuseMuutuja.meetodiNimi(...)` on punasega alla joonitud,
> vajuta **Alt+Enter** punasel joonel → vali **"Create method in TeenusKlass"**.
> IntelliJ loob automaatselt vastava meetodi service klassi!

---

## Samm 2 — Service ja esimene repository päring

### Mida teha?

Nüüd liigume service klassi meetodisse. Sisendit sellel meetodil pole — eesmärk on küsida andmebaasist **kõik** testid, sõltumata nende staatusest.

Ava service klass ja mine äsja loodud meetodisse.

### Repository ühenduse loomine

Mõtle: **millisest tabelist** on vaja andmeid pärida?
Vaata `docs/database/2_create.sql` failist tabelit `test` ja sellele vastavat entiteeti kaustas `persistence/`.

Alusta kirjutama repositooriumi muutuja nime service meetodis:

```java
public void meetodiNimi() {
    entiteetRep  // <- kirjuta algus siia
}
```

> **IntelliJ vihje:** Kirjuta muutuja nime algus (nt `entiteetRep`) ja IntelliJ pakub
> automaatselt vastavat repositooriumi. Vajuta **Tab** → repositoorium lisatakse klassiväljana!

Tulemus võiks välja näha nii:

```java
@Service
@RequiredArgsConstructor
public class TeenusKlass {

    private final EntiteetRepository entiteetRepository;

    public void meetodiNimi() {
        entiteetRepository
    }
}
```

> **Kui repositooriumi interface pole olemas:** IntelliJ pakub punase pirniga (Alt+Enter)
> võimaluse luua uus interface. Vali **JpaRepository** ja kontrolli, et fail läheks
> õigesse paketti (sama pakett kui entiteet).

**Küsi endalt:** Kas JPA pakub valmis meetodit, mis tagastab tabeli **kõik** read? Kas taskis on mingit filtreerimistingimust?

> **Rusikareegel:** Kui päringusse läheb sisendina muu väärtus kui tabeli `id`,
> on tõenäoliselt vaja **uut meetodit** teha (vt "Uue meetodi loomine JPA Buddy abil" allpool).

Kui repository meetod midagi tagastab (entity, `Optional<Entiteet>`, list), **pane tulemus kohe muutujasse** — vt "Meetodi palve":

> *"Kui sa kutsud välja mingi meetodi, mis tagastab midagi, ja sa soovid selle infoga midagi edasi teha, siis pane see kohe muutujasse."*

---

## Samm 3 — DTO klass

### Mida teha?

Kui entiteetide list on käes, on aeg luua väljundi DTO klass. Taskifail nimetab selle `TestSummaryDto`. Kõik vajalikud väljad (`testId`, `testName`, `testShortDescription`, `testStatus`) tulevad **ühest** entiteedist, seega sobib JPA Buddy generaator hästi.

Mõtle: kas vastav DTO klass on juba olemas?
- Vaata kaustast: `backend/src/main/java/ee/testiplatvorm/controller/test/dto/`

**Kui DTO puudub** → kasuta JPA Buddy abi:

1. Paremklõps entity klassil → New → DTO
2. Kontrolli valikud:
    - **Package** → controller alampakett (nt `controller.ressurss.dto`)
    - **DTO class name** → nimi taskifailist
    - **MapStruct Interface** → vali olemasolev mapper või loo uus plussmärgiga
    - **Mutable** → jäta märgituks
3. Vali väljad — ainult need, mida taskifaili vastuse näidis nõuab
4. Peale loomist kontrolli DTO klass üle ja nimeta väljad ümber nii, et need klapiksid taskifaili JSON-i näidisega

> **Mõtle:** Vaata olemasolevat `LoginResponse` DTO-d — millise struktuuri ja annotatsioonidega see on tehtud? Kus asub projektis mapper interface (vt `UserMapper`)?

---

## Samm 4 — Mapper

### Mida teha?

Entiteetide list tuleb nüüd teisendada DTO-de listiks. Kõik DTO väljad tulevad ühest entiteedist, nii et täiendavaid päringuid pole vaja.

### Mapper

Ava mapper interface (nt `EntiteetMapper.java`).

Vaikimisi tekib JPA Buddy poolt kolm meetodit — eemalda mittevajalikud, jäta vaid need, mida tegelikult vajad.

Nimeta meetod ümber konventsiooni järgi:

```java
// Ühele DTO-le
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);

// Lista DTO listiks
List<TagastatavDtoTüüp> toDtoKlassiNimid(List<EntiteetTüüp> entiteedid);
```

> **Tähtis:** `@Mapping` annotatsioonid käivad alati **üksiku objekti** meetodile (ainsuses).
> List-meetod (mitmuses) jääb **ilma annotatsioonideta** — MapStruct genereerib selle ise ja kutsub iga elemendi kohta üksiku objekti meetodit. Seega on vaja **mõlemat** meetodit.

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

Täida kõik väljad. Mis ei sobi — kasuta `ignore = true`:

```java
@Mapping(source = "seotudObjekt.id", target = "seotudObjektiId")
@Mapping(source = "tavaveerg", target = "samaNimiDtos")
@Mapping(ignore = true, target = "väljaJuideiTahaSaata")
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);
```

> **Konventsioon:** Iga DTO target-väli peab olema kirjas — kas `source`-iga (ka siis, kui nimed kattuvad) või `ignore = true`-ga. Ükski väli ei jää "vaikimisi automaatseks".

### Service meetodi lõpetamine

Kutsu mapper meetod välja service meetodis:

```java
public void meetodiNimi() {
    List<EntiteetTüüp> entiteedid = entiteetRepository.meetodiNimi();
    List<TagastatavDtoTüüp> dtod = mapperMuutuja.toDtoKlassiNimid(entiteedid);
    return dtod;
}
```

> **IntelliJ vihje:** Nüüd on meetodi tagastustüüp `void`, aga `return` lause on sees.
> Vajuta **Alt+Enter** punase joone peal → IntelliJ parandab tagastustüübi automaatselt!

---

## Samm 5 — Repository (täiendavad päringud, kui vaja)

### Mida teha?

Selle taski puhul tagastatakse kõik testid ilma filtrita, seega eraldi päringut ei pruugi vaja minna. Kui aga otsustad, et soovid testid kindlas järjekorras (nt nime järgi) või kasutada projekti tava lühikese nimega meetodit, loo meetod JPA Buddy abil.

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
5. Lisa **query conditionid** — milliseid veerge filtreeritakse
6. **Advanced** sektsioonis: vali alati **Named parameters**
7. Mitme reaga tulemuse puhul mõtle läbi **Order By Attributes**

Peale meetodi loomist:
- Kontrolli parameetrite nimed — ebamäärane `id` asenda konkreetsemaga
- Tee vastav muudatus ka `@Query` annotatsiooni nimetud parameetris
- Eemalda ebavajalikud `@Param()` annotatsioonid meetodist, kui Named parameters on kasutusel

---

## Samm 6 — tagasi RestController'isse

### Mida teha?

Service meetod tagastab nüüd DTO-de listi. Naase kontrolleri meetodisse ja too tulemus välja.

Täienda kontrolleri meetodit — lisa `return` lause:

```java
public void meetodiNimi() {
    teenuseMuutuja.meetodiNimi();  // <- enne: tulemus kasutamata
}
```

> **IntelliJ vihje:** Lisa `return` lause.
> IntelliJ kurdab, et `void` ei saa midagi tagastada — vajuta **Alt+Enter** → "Change return type".

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

Eemalda ka üleliigsed tühjad read ja kasutamata importid (**Ctrl+Alt+O**).

### Meetodite järjekord

Kontrolli meetodite järjekorda vastavalt Java konventsioonile:
1. `public` meetodid enne
2. `private` meetodid pärast
3. Järjesta ka väljakutsumise hierarhia järgi — peameetod üleval, helper meetodid all

---

## Kokkuvõte ja kontrollnimekiri

Enne kui pead koodi valmis, kontrolli läbi:

- [ ] RestController klass on olemas vajaliku `@RestController`, `@RequiredArgsConstructor` annotatsiooniga (ja tee on kirjas projektiga järjepidevalt)
- [ ] Kontrolleri meetodil on `@GetMapping`, `@Operation` ja `@ApiResponses` annotatsioonid
- [ ] Service klass on olemas vajaliku `@Service`, `@RequiredArgsConstructor` annotatsiooniga
- [ ] Repository interface on olemas ja laiendab `JpaRepository`-t
- [ ] Kui lõid oma repository meetodi, on sellel `@Query` annotatsioon Named parameters stiilis
- [ ] Mapper interface on olemas `componentModel = spring` seadistusega
- [ ] Mapperis on nii üksiku objekti meetod (`@Mapping`-annotatsioonidega) kui ka list-meetod (ilma annotatsioonideta, nimi mitmuses)
- [ ] Kõik `@Mapping` annotatsioonid on täidetud — ükski DTO väli ei ole kaardistamata
- [ ] DTO väljade nimed vastavad taskifaili JSON-i näidisele
- [ ] Meetodite järjekord: `public` enne, `private` pärast — järjesta ka väljakutsumise hierarhia järgi
- [ ] Kood kompileerub ja Swagger UI kaudu on endpoint nähtav

---

> **Järgmine samm:** Testi endpointi Swagger UI kaudu (`http://localhost:8080/swagger-ui/index.html`)
> ja kontrolli, et vastus vastab taskifailist leitud näidisandmetele.
