# Juhend: GET /api/users ja DELETE /api/users/{userId}

**Taski fail:** `UsersView-markmed.md`
**Kontroller:** `UserController.java` (uus)
**Implementeerimise voog:**
- Osa 1 — GET: RestController → Service → Repository → Service → Mapper → RestController
- Osa 2 — DELETE: RestController → Service → Repository → Service → RestController

---

## Sissejuhatus

Selles taskis ehitad kasutajate haldusvaate (`UsersView.vue`) jaoks kaks backend teenust: kõigi kasutajate nimekirja pärimise ja kasutaja "kustutamise". Nimekirja andmed tulevad mitmest tabelist korraga (`user`, `role`, `profile`, `group_member` + `group`) ning kustutamine on **soft delete** — rida jääb andmebaasi alles, muutub ainult staatus.

Õpid, kuidas koostada vastust, mille väljad ei mahu ühe entiteedi sisse, kuidas käsitleda "üks-mitmele" seost (kasutajal võib olla mitu gruppi) ja kuidas ehitada kustutamise teenus, mis tegelikult midagi ei kustuta.

Enne alustamist loe taskifail uuesti läbi — eriti `Response (200)` näidis, `API teenuse lisainfo` ja `Veateated`.

---

# Osa 1 — GET /api/users

## Samm 1 — RestController

### Mida teha?

Projektis on praegu ainult `LoginController` — kasutajate jaoks kontrollerit veel pole, see tuleb luua. Vaata `LoginController`-it eeskujuks: kuidas on seal lahendatud klassi annotatsioonid ja kas `@RequestMapping` on kasutusel või on tee kirjas otse meetodi juures. Järgi sama stiili.

Kontrolli esmalt, kas vastav kontrolleri klass juba eksisteerib:
- Kaust: `backend/src/main/java/ee/testiplatvorm/controller/`
- Kui **puudub** → loo uus alampakett ressursi jaoks ja klass IntelliJ'ga (File → New → Java Class)
- Kui **on olemas** → ava see klass ja lisa sinna uus meetod

Vajalikud klassiannotatsioonid (kui lood uue kontrolleri):

```java
@RestController
@RequiredArgsConstructor
public class KontrolleriKlass {
    // ...
}
```

### Meetodi loomine

Alusta meetodist **ilma mappingannotatsioonideta** — see aitab kõigepealt loogika paika saada:

```java
public void meetodiNimi() {
    // tühi meetod esialgu
}
```

> **Mõtle:** Mis on selle meetodi hea nimi? Nimi peaks kirjeldama, mida meetod teeb.
> Kas sellel päringul on üldse sisendparameetreid? Vaata taskifailist API rida.

Seejärel lisa:
1. **Mappingannotatsioon** — `@GetMapping`
2. **Swagger annotatsioonid** — `@Operation` ja `@ApiResponses` (springdoc on projektis olemas; kui `LoginController` neid ei kasuta, arutage tiimiga, kas lisate)

### Service klassi ettevalmistus

Kontrolli, kas service klass juba eksisteerib:
- Kaust: `backend/src/main/java/ee/testiplatvorm/service/`
- `LoginService` on olemas, aga see on sisselogimise jaoks. Mõtle: kas kasutajate haldus kuulub sinna või eraldi klassi?

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

---

## Samm 2 — Service ja esimene repository päring

### Mida teha?

Nüüd liigud service meetodisse. Sisendit pole — tuleb tagastada **kõik** kasutajad, sõltumata staatusest (ka ootel ja kustutatud).

Mõtle: **millisest tabelist** on vaja põhiandmeid pärida? Millised teised tabelid annavad lisaväljad (nimi, roll, grupid)? Vaata `docs/database/2_create.sql` ja taski `Response (200)` näidist kõrvuti.

```java
public void meetodiNimi() {
    entiteetRep  // <- kirjuta algus siia
}
```

> **IntelliJ vihje:** Kirjuta muutuja nime algus ja IntelliJ pakub vastavat repositooriumi.
> Vajuta **Tab** → repositoorium lisatakse klassiväljana!

**Küsi endalt:** Kas `JpaRepository` baasmeetod (`findAll()`) annab kõik vajalikud väljad, või on osa infot teistes tabelites, millele entiteedist otse viidet pole?

> **Vihje:** Ava `User.java` entiteet. Millistele seotud entiteetidele on seal väli olemas? Millistele mitte? Kus asub viide `user`-ile tabelites `profile` ja `group_member`?

> **Rusikareegel:** Kui baasmeetod ei kata kõike, on tõenäoliselt vaja **uut meetodit** (vt Samm 5).

Kui repository meetod midagi tagastab, **pane tulemus kohe muutujasse**.

---

## Samm 3 — DTO klass (loo kohe, mitte alles lõpus)

### Mida teha?

Vastus koosneb andmetest mitmest tabelist, seega loo DTO kohe ja täida seda samm-sammult.

Kontrolli, kas vastav DTO klass on juba olemas:
- Kaust: `backend/src/main/java/ee/testiplatvorm/controller/.../dto/`
- Vaata ka `persistence/user/` kausta — seal võib olla mõni vana, kasutamata DTO. Kas see sobib või tekitab pigem segadust?

> **Mitme allikaga DTO:** Selle taski väljad ei mahu ühe entiteedi struktuuri sisse. Loo DTO taskifaili `Response (200)` näidise järgi — väljanimed peavad JSON-iga **täpselt** kokku klappima.

> **Mõtle:** Üks väli on JSON-is **massiiv**. Mis Java tüüp sobib? Mis peaks olema selle algväärtus, kui kasutajal pole ühtegi gruppi (vaata taskifailist, mida frontend ootab)?

> **Nimetamine:** Vaata, kuidas on nimetatud olemasolevad DTO-d (`LoginRequest`, `LoginResponse`) ja järgi sama stiili.

---

## Samm 4 — Mapper ja ülejäänud andmete kogumine

### Mida teha?

Nüüd tuleb andmed DTO-sse saada. Selles taskis on kaks mõistlikku teed — vali teadlikult:

1. **Mapper** — pärid entiteedid ja MapStruct kaardistab need DTO-ks.
2. **JPQL konstruktori-avaldis** — repository päring loob DTO otse (`select new ...`). backend/CLAUDE.md lubab seda ("Vajadusel kasutab repositoorium konstruktori avaldist otse DTOsse").

> **Mõtle:** Kumb sobib paremini, kui väljad tulevad neljast tabelist ja `User` entiteedil pole viidet `Profile`-le? Mis juhtub mõlema lähenemisega massiivi-väljaga?

### Kui valid mapperi

> **rAIn-i kontrollpunkt:** Niipea kui mapper meetodi **signatuur** on olemas, aga `@Mapping` annotatsioone veel pole — kasuta kohe Ctrl+Space "tühja malli" nippi (vt allpool), enne kui hakkad väljade sisu üle arutlema.

> **rAIn-i kontrollpunkt #2:** Kui `@Mapping`-read on täidetud, kontrolli **iga target-välja** ükshaaval: kas igaühel on kas `source = "..."` või `ignore = true`?

Ava mapper interface (`persistence/user/` kaustas on juba üks olemas — vaata, mis seal on).

```java
// Ühele DTO-le
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);

// Lista DTO listiks
List<TagastatavDtoTüüp> toDtoKlassiNimid(List<EntiteetTüüp> entiteedid);
```

> **Levinud segadus:** `@Mapping` annotatsioonid käivad alati **üksiku objekti** meetodile. List-meetod jääb annotatsioonideta — MapStruct genereerib selle ise, kutsudes üksiku objekti meetodit iga elemendi kohta.

```java
@Mapping(source = "", target = "")
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);
```

> **IntelliJ vihje:** Kliki `target = ""` jutumärkide vahele → vajuta **Ctrl+Space**.
> IntelliJ näitab kõiki DTO välju — nii saad ettevalmistatud malli.

Täida kõik väljad. Mis ei sobi — kasuta `ignore = true`:

```java
@Mapping(source = "seotudObjekt.nimi", target = "seotudObjektiNimi")
@Mapping(source = "tavaveerg", target = "tavaveerg")
@Mapping(ignore = true, target = "väliMidaTäidadHiljem")
TagastatavDtoTüüp toDtoKlassiNimi(EntiteetTüüp entiteet);
```

### Kui valid konstruktori-avaldise

> **Mõtle:** `select new` kutsub DTO konstruktorit. Kas Lomboki `@AllArgsConstructor` sobib, kui üks väli on kollektsioon, mida JPQL ei oska täita? Milline konstruktor sul tegelikult vaja on?

### Grupid (mõlema tee puhul)

Kasutajal võib olla **mitu gruppi** (vaata `3_import.sql` — üks kasutaja on kahes grupis).

> **Mõtle:** Kui joinid `group_member` tabeli samasse päringusse, mitu rida tuleb sellele kasutajale? Kas see klapib taski ootusega (iga kasutaja nimekirjas üks kord)?

> **Vihje:** Grupid võib pärida **eraldi päringuga** ja lisada DTO-dele service kihis. Kas `GroupMember` entiteedile on repository olemas?

> Kui kirjutad service'isse meetodi, mis muudab olemasolevaid DTO-sid — backend/CLAUDE.md ütleb, millise prefiksiga see meetod nimetada (`get` vs `handle`).

### Service meetodi lõpetamine

```java
public void meetodiNimi() {
    List<DtoTüüp> dtoList = repositoorium.meetodiNimi();
    // täienda dto-sid teistest allikatest pärit andmetega
    return dtoList;
}
```

> **IntelliJ vihje:** Meetodi tagastustüüp on `void`, aga `return` on sees.
> Vajuta **Alt+Enter** → IntelliJ parandab tagastustüübi automaatselt!

---

## Samm 5 — Repository (täiendavad päringud)

### Mida teha?

Selles taskis on vaja vähemalt üht uut päringut (kasutajate andmed koos seotud tabelitega) ja tõenäoliselt teist (grupid).

### Uue meetodi loomine JPA Buddy abil

1. Ava JPA Buddy paneel (paremklõps repository klassis → JPA Buddy)
2. Valikutes **Method** ja **Query** vali → **Query**
3. Vali meetodi tüüp — siin **Find collection**
4. **Wrap type** → `List<...>`
5. Lisa vajadusel query conditionid
6. **Advanced** sektsioonis: vali alati **Named parameters**
7. Mõtle läbi **Order By** — mis järjekorras peaks nimekiri tulema?

> **Tähtis — ära kaota ridu:** Ootel kasutajal (`userId` 4) **pole** `profile` rida. Halduril (`userId` 2) pole ühtegi grupiliikmesust. Mis tüüpi join tagab, et nad ikkagi tulemusse jõuavad?

> **Seos ilma entiteedi väljata:** Kui `User`-il pole välja `Profile`-le, saab JPQL-is joinida ka `on`-tingimusega: `left join TeineEntiteet t on t.seos = e`.

> **Nimetamine:** backend/CLAUDE.md — repository meetodi nimi peab mainima, mida ta tagastab (nt `findAllXxx()`), mitte `findBy...` pikk JPA-stiilis nimi.

> **Lazy seosed:** Kui pärid `GroupMember` read ja loed igaühe juurest grupi nime, kas see tekitab iga rea kohta eraldi päringu (N+1)? Vaata P6Spy logi konsoolis! Mis aitab — `join fetch`?

---

## Samm 6 — tagasi RestController'isse

### Mida teha?

Service meetod tagastab nüüd listi — täienda kontrolleri meetodit.

```java
public TagastatavTüüp meetodiNimi() {
    return teenuseMuutuja.meetodiNimi();
}
```

> **IntelliJ vihje:** Lisa `return` ja vajuta **Alt+Enter** → "Change return type".

Käivita rakendus ja testi Swaggeris. Kontrolli `3_import.sql` andmete vastu:
- Kas kõik 4 kasutajat on nimekirjas **täpselt üks kord**?
- Kas kahe grupiga kasutajal on massiivis 2 nime?
- Kas grupita kasutajal on tühi massiiv (mitte `null`)?
- Kas ootel kasutajal on nimeväljad `null`?

---

# Osa 2 — DELETE /api/users/{userId}

## Samm 1 — RestController

### Mida teha?

Lisa samasse kontrollerisse uus meetod. Mappingannotatsioon on `@DeleteMapping`, parameeter tuleb path'ist.

> **Mõtle:** Kas taskifaili järgi tagastatakse midagi? Vaata `Response (200)` rida.

> **Tähelepanu:** Path variable nimi peab klappima taskifaili API reaga (`{userId}`, mitte `{id}`).

## Samm 2 — Service

Lisa kontrollerist service meetodi väljakutse ja loo meetod **Alt+Enter**-iga samasse service klassi.

## Samm 3 — Repository

### Mida teha?

Taskifaili järgi on see **soft delete** — rida andmebaasist ei kustutata!

> **Mõtle:** Kas `deleteById()` sobib? Mis peab tegelikult juhtuma? (Vaata `API teenuse lisainfo`.)

Selleks pead kasutaja kõigepealt leidma. Kas `findById()` piisab?

> **Veaolukord:** Taskifaili järgi — kui kasutajat ei leita, milline HTTP staatus, `errorCode` ja `message` tuleb? Vaata kaustast `infrastructure/exception/`, milline erind toodab just sellise `message` ja `errorCode` ise.

> **Konventsioon:** backend/CLAUDE.md ütleb, kuidas `findById()` + `orElseThrow` meetod nimetada ja kuhu see kuulub (`getValid<Entiteet>By(...)`). Kas see peaks olema `public` või `private`?

## Samm 4 — tagasi Service'i

### Mida teha?

Nüüd, kui kasutaja on käes, muuda tema andmeid ja salvesta.

> **Mõtle:**
> - Milline väli muutub ja mis väärtusele? Kas `Status` enumis on see väärtus juba olemas? (Ära kirjuta koodi maagilist stringi!)
> - Kas mõni teine väli peaks muutmisel samuti uuenema? Vaata `user` tabeli veerge.
> - Mis juhtub, kui kasutaja on **juba** kustutatud? Taskifaili järgi — kas see on viga?

## Samm 5 — tagasi RestController'isse

Kuna midagi ei tagastata, jääb kontrolleri meetod `void`-iks — piisab service väljakutsest.

Testi Swaggeris:
- Kustuta olemasolev kasutaja → 200, siis GET nimekirjas on tema staatus muutunud.
- Kustuta sama kasutaja uuesti → mis tuleb?
- Kustuta olematu `userId` (nt 123) → kas vastus klapib taskifaili `Veateated` plokiga täpselt?

---

## Viimane samm — kood ilusaks (refactor)

### Make it work → Make it beautiful

Kui kood töötab, vaata üle, kas saab koodi puhtamaks muuta.

**Extract Method IntelliJ'ga:**

Märgi service meetodis koodilõik, mida soovid eraldada helper meetodiks → paremklõps → Refactor → Extract Method.

> **Tähelepanu:** IntelliJ kasutab ekstraktimisel kogu objekti parameetrina.
> Vaata üle, kas helper meetod vajab tegelikult kogu objekti või ainult üht välja.

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

1. `public` meetodid enne
2. `private` meetodid pärast
3. Järjesta ka väljakutsumise hierarhia järgi — peameetod üleval, helper meetodid all

---

## Kokkuvõte ja kontrollnimekiri

- [ ] Kontroller on õiges alampaketis (`controller/<ressurss>/`), annotatsioonid samas stiilis nagu `LoginController`
- [ ] Kontrolleri meetoditel on `@Operation` ja `@ApiResponses` (kui tiim otsustas neid kasutada)
- [ ] Service klass on olemas `@Service`, `@RequiredArgsConstructor` annotatsiooniga
- [ ] DTO väljanimed klapivad taskifaili JSON-iga täpselt; massiiviväli pole kunagi `null`
- [ ] Repository meetoditel on `@Query` annotatsioon Named parameters stiilis ja nimi ütleb, mida tagastab
- [ ] Mapper (kui kasutad): kõik `@Mapping` target-väljad on käsitletud (`source` või `ignore = true`)
- [ ] GET: iga kasutaja on nimekirjas üks kord, ka profiilita ja grupita kasutajad
- [ ] DELETE: rida jääb andmebaasi, staatus tuleb `Status` enumist, olematu `userId` → 404 `PRIMARY_KEY_NOT_FOUND`
- [ ] `getValid...By` meetod on `public` ja asub õiges service klassis
- [ ] Meetodite järjekord: `public` enne, `private` pärast, väljakutsumise hierarhia järgi
- [ ] Kood kompileerub ja mõlemad endpointid on Swagger UI-s nähtavad

---

> **Järgmine samm:** Testi endpointe Swagger UI kaudu (`http://localhost:8080/swagger-ui/index.html`)
> ja kontrolli, et vastused klapivad taskifaili näidistega.
