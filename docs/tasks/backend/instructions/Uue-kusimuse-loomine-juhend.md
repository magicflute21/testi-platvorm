# Juhend: POST /api/questions

**Taski fail:** `Uue-kusimuse-loomine.md`
**Kontroller:** `QuestionController.java` (juba olemas)
**Implementeerimise voog:** RestController → Service → Mapper → Repository → Service → RestController

---

## Sissejuhatus

See endpoint salvestab uue küsimuse koos vastusevariantidega kahte tabelisse: `question` ja `question_answer`. Enne salvestamist tuleb teha mitu kontrolli: kasutaja roll, kompetentsi taseme ja küsimuse tüübi olemasolu ning õigete vastuste arv küsimuse tüübi järgi. Selle harjutusega õpid sisendi DTO-d valideerima, sisendi DTO-d entiteediks mappima (ka pesastatud listi), andmeid ühes transaktsioonis salvestama ja uue veakoodi lisama.

> **Tee enne:** Taski `GET /api/question-types` võiks olla enne seda tehtud. Siis tead juba, millised küsimuse tüübid on olemas ja mis on nende id-d.

---

## Samm 1 — RestController

### Mida teha?

`QuestionController` on juba olemas ja selles on `GET /api/questions`. Uus meetod läheb samasse klassi, sest ressurss on sama (`/api/questions`), ainult HTTP meetod on teine.

Alusta meetodist ilma annotatsioonideta:

```java
public void meetodiNimi(SisendDtoTüüp sisendDtoMuutuja) {
    // tühi meetod esialgu
}
```

> **Mõtle:** Mis on hea nimi meetodile, mis **loob** midagi? Vaata `TestController`-i POST meetodit.

Seejärel lisa:
1. **Mappingannotatsioon**: `@PostMapping`
2. **Parameetri annotatsioonid**: `@RequestBody` ja `@Valid`. Ilma `@Valid`-ita ei käivitu DTO väljade valideerimine ning `400 INCORRECT_INPUT` ei tööta.
3. **Swagger annotatsioonid**: `@Operation` ja `@ApiResponses`. Taskifaili järgi on vaja vastuseid 200, 400, 401, 403 ja 404. Veavastuste juures kasuta `content = @Content(schema = @Schema(implementation = VeaKlass.class))`.

> **Mõtle enne tagastustüübi valimist:** Vaata taskifaili **Väljund** sektsiooni. Kas loomise järel tuleb midagi tagastada?
> See otsus mõjutab kogu voogu. Pea seda meeles, kui jõuad Samm 5 juurde.

### Sisendi DTO

Taskifailis on sisendiks `QuestionCreateRequestDto`, mille sees on vastusevariantide list.

- Kas DTO on juba olemas? Vaata `controller/question/dto/`.
- Mõtle, mitu DTO klassi on vaja. Vastusevariandil on oma väljad (`answerText`, `isCorrect`), nii et see vajab tõenäoliselt oma klassi.
- Lisa igale väljale sobiv valideerimisannotatsioon. Vaata taskifaili tabelist veergu "Kohustuslik" ja võrdle `TestCreateRequestDto`-ga:
    - Millal kasutada `@NotNull`, millal `@NotBlank`, millal `@NotEmpty`?
    - Kuidas saada valideerimine käima ka **listi sees olevatel** objektidel?
    - Kas tasub lisada ka pikkuse piirang (`@Size`), et vältida andmebaasi viga liiga pika teksti korral?

### Service meetodi väljakutse

Kontroller kasutab juba `QuestionService`-it, seega pole uut service muutujat vaja. Kutsu välja uus service meetod:

```java
public void meetodiNimi(SisendDtoTüüp sisendDtoMuutuja) {
    teenuseMuutuja.meetodiNimi(sisendDtoMuutuja);
}
```

> **IntelliJ vihje:** Vajuta punasel joonel **Alt+Enter** ja vali **"Create method in QuestionService"**.

---

## Samm 2 — Service (kontrollid enne salvestamist)

### Mida teha?

Ava `QuestionService` ja uus meetod. Enne mappimist ja salvestamist on vaja vastata neljale küsimusele:

1. **Kes on kasutaja ja kas tal on õigus?** `userId` tuleb `CurrentUserService`-ist, mitte request body'st. Rolli järgi otsustad, kas visata `ForbiddenException`.
    > **Vihje:** Projektis on juba koht, kus tehakse **täpselt sama** rollikontrolli sama veakoodiga. Otsi `NO_PERMISSION_TO_CREATE_QUESTIONS` kasutusi (**Ctrl+Shift+F**). Mõtle, kas saad seda loogikat taaskasutada ilma kopeerimata. Kui jah, siis kuidas?
2. **Kas kompetentsi tase on olemas?** Otsi seda id järgi. Kui rida puudub, on vaja `PrimaryKeyNotFoundException`-it.
3. **Kas küsimuse tüüp on olemas?** Sama muster.
4. **Kas õigete vastuste arv sobib tüübiga?** Vaata taskifaili tabelit "Õigete vastuste reeglid".

Iga repository/service väljakutse puhul, mis midagi tagastab, **pane tulemus kohe muutujasse** (meetodi palve).

> **Optional:** `findById()` tagastab `Optional`-i. Siin on väärtus kohustuslik, nii et mõtle, milline `Optional` meetod sobib. Vaata backend/CLAUDE.md jaotist "Entiteedi otsing ID järgi" (`getValid<Entiteet>By` muster).

### Uus veakood

Taski järgi on `INVALID_CORRECT_ANSWER_COUNT` uus veakood. Ava `Error` enum ja lisa see sinna, eeskujuks olemasolevad read. Viska see `BadRequestException`-ina.

> **Mõtle:** Õigete vastuste reegel erineb kolme tüübi puhul. Kuidas teha nii, et peameetod jääks loetav? Kas see loogika sobiks omaette meetodiks, mis saab sisse küsimuse tüübi ja vastuste listi? Mis andmeid see meetod tegelikult vajab?

---

## Samm 3 — Mapper (sisendi teisendamine)

### Mida teha?

Sisendi DTO tuleb teisendada `Question` entiteediks. Vastusevariandid tuleb teisendada `QuestionAnswer` entiteetideks.

Mapperid on selles projektis paketis `persistence/<entiteet>/`:
- `persistence/question/QuestionMapper.java` on olemas
- `persistence/questionanswer/QuestionAnswerMapper.java` on olemas

Lisa uued meetodid olemasolevatesse mapperitesse. Nimeta need konventsiooni järgi:

```java
EntiteetTüüp toEntiteetKlassiNimi(SisendDtoTüüp sisendDto);

// vastusevariantide listi jaoks: üksik + list
AlamEntiteetTüüp toAlamEntiteetKlassiNimi(AlamDtoTüüp alamDto);
List<AlamEntiteetTüüp> toAlamEntiteetKlassiNimid(List<AlamDtoTüüp> alamDtod);
```

> **NB!** `@Mapping` annotatsioonid käivad ainult **üksiku objekti** meetodile. List-meetod jääb ilma annotatsioonideta.

Lisa `@Mapping` annotatsioonid:

```java
@Mapping(source = "", target = "")
EntiteetTüüp toEntiteetKlassiNimi(SisendDtoTüüp sisendDto);
```

> **IntelliJ vihje:** Kliki `target = ""` jutumärkide vahele ja vajuta **Ctrl+Space**. IntelliJ näitab kõiki entiteedi välju. Tee iga välja jaoks oma rida.

Täida iga target-väli kas `source`-iga või `ignore = true`-ga:

```java
@Mapping(ignore = true, target = "id")
@Mapping(source = "dtoVäli", target = "entiteediVäli")
@Mapping(ignore = true, target = "väliMisDtostPuudub")
EntiteetTüüp toEntiteetKlassiNimi(SisendDtoTüüp sisendDto);
```

> **Mõtle:**
> - `id` on loomise puhul alati `ignore = true`.
> - Millised `Question` väljad on **seosed** (teised entiteedid)? DTO-s on nende asemel ainult id-d. Kas mapper saab id-st terve entiteedi teha või tuleb see service-is ise määrata?
> - Millised väljad saavad väärtuse alles service-is (staatus, ajad, looja)?
> - `QuestionAnswer` puhul: millised väljad jäävad taski järgi `NULL`-iks? Kuidas saab vastus teada, millise küsimuse juurde ta kuulub?
> - Kas mõni DTO välja nimi erineb entiteedi välja nimest? (Vaata `isCorrect` ja entiteedi vastavat välja.)

---

## Samm 4 — Repository

### Mida teha?

Siin on kaks salvestamist: küsimus ja vastusevariandid. Mõlemal entiteedil on repository juba olemas (`QuestionRepository`, `QuestionAnswerRepository`).

Mõtle:
- Kas `JpaRepository` `save()` piisab? Kas on ka meetod, mis salvestab terve listi korraga?
- Mis **järjekorras** tuleb salvestada? Vastusel on vaja küsimuse viidet. Kas küsimusel peab `id` enne vastuste salvestamist olemas olema?

> **Rusikareegel:** Lihtsa loomise puhul piisab enamasti baasmeetoditest. Uut meetodit on vaja ainult siis, kui enne salvestamist tuleb midagi spetsiifilist pärida.

---

## Samm 5 — tagasi Service'i

### Mida teha?

Pane service meetod kokku: kontrollid → mappimine → puuduvate väljade täitmine → salvestamine → tagastus.

```java
public void meetodiNimi(SisendDtoTüüp sisendDto) {
    // kontrollid (Samm 2)
    EntiteetTüüp entiteet = mapperMuutuja.toEntiteetKlassiNimi(sisendDto);
    // täida väljad, mida mapper ignoreeris
    entiteetRepository.save(entiteet);
    // vastusevariandid
}
```

> **Tuleta meelde Samm 1 otsust:** Taski järgi tagastatakse loodud küsimuse id. Kust saad selle kätte pärast salvestamist?

> **Transaktsioon:** Taski vastuvõtu kriteerium ütleb, et vea korral ei tohi andmebaasi jääda poolikut küsimust. Mis juhtub, kui küsimus salvestub, aga vastuste salvestamine ebaõnnestub? Millise annotatsiooniga saab seda vältida? Vaata `TestService.createTest`.

> **Ajatempel:** `created_at` ja `updated_at` peaksid olema sama hetk. Mõtle, kuidas seda tagada.

---

## Samm 6 — tagasi RestController'isse

Lisa kontrolleri meetodisse tulemuse muutuja ja `return`:

```java
public void meetodiNimi(SisendDtoTüüp sisendDto) {
    teenuseMuutuja.meetodiNimi(sisendDto);  // <- enne: tulemus kasutamata
}
```

> **IntelliJ vihje:** Vajuta **Alt+Enter** ja vali "Change return type".

Kontrolli, et tagastustüüp klapib Samm 1 otsusega ja taskifaili **Väljund** sektsiooniga.

---

## Samm 7 — kood ilusaks (refactor)

### Make it work → Make it beautiful

Selle taski service meetod läheb pikaks, nii et refactor on siin eriti kasulik.

**Extract Method IntelliJ-ga:** märgi koodilõik, tee paremklõps ja vali Refactor → Extract Method. Head kandidaadid:
- õigete vastuste arvu kontroll
- vastusevariantide salvestamine
- küsimuse väljade täitmine enne salvestamist

> **Tähelepanu:** IntelliJ annab ekstraktimisel parameetriks sageli kogu objekti.
> Vaata üle, kas helper vajab kogu objekti või ainult üht välja.

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

Pärast (parem, sest edasi antakse ainult vajalik):
```java
kontrolliMidagiHelper(dtoObjekt.getMingiVäli());

private void kontrolliMidagiHelper(VäljaTüüp väljaNimi) {
    boolean onProbleem = repositoorium.kontrollimeetod(väljaNimi);
    if (onProbleem) {
        throw new MingiException(...);
    }
}
```

Samuti kontrolli, kas `getValid<Entiteet>By` meetodid on backend/CLAUDE.md järgi õige service klassi all.

### Meetodite järjekord

1. `public` meetodid ees
2. `private` meetodid järel
3. Järjesta väljakutsumise hierarhia järgi: peameetod üleval, helperid all (vt ka skill `/skill-jarjesta-meetodid`)

---

## Kokkuvõte ja kontrollnimekiri

- [ ] `QuestionController`-is on uus `@PostMapping` meetod, mille lõplik tee on `/api/questions`
- [ ] Parameetril on `@RequestBody` ja `@Valid`
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponses` (200, 400, 401, 403, 404)
- [ ] Sisendi DTO-del on valideerimisannotatsioonid ja listi elemente valideeritakse samuti
- [ ] `userId` tuleb `CurrentUserService`-ist, mitte request body'st
- [ ] Rollikontroll viskab `403 NO_PERMISSION_TO_CREATE_QUESTIONS`
- [ ] `Error` enumis on uus `INVALID_CORRECT_ANSWER_COUNT` ja see visatakse `BadRequestException`-ina
- [ ] Õigete vastuste arvu reegel on kontrollitud kõigi 3 tüübi jaoks
- [ ] Olematu `competenceLevelId` / `questionTypeId` annab `404 PRIMARY_KEY_NOT_FOUND`
- [ ] `competence` võetakse kompetentsi taseme küljest
- [ ] `status = 'A'`, `created_by`, `created_at` ja `updated_at` on määratud
- [ ] Iga mapperi target-väli on kaardistatud (`source` või `ignore = true`)
- [ ] List-mapperi meetod on mitmuses ja ilma `@Mapping` annotatsioonideta
- [ ] Service meetodil on `@Transactional`
- [ ] Endpoint tagastab loodud küsimuse id
- [ ] Meetodite järjekord: `public` ees, `private` järel
- [ ] Endpointi jaoks on automaattestid (edukas loomine + veaolukorrad)

---

> **Järgmine samm:** Testi endpointi Swagger UI kaudu (`http://localhost:8080/swagger-ui/index.html`).
> Logi enne sisse ADMIN või HALDUR kasutajana. Proovi edukat loomist ja iga veaolukorda taskifaili tabelist.
> Kontrolli `psql`-iga, et `question` ja `question_answer` tabelisse tekkisid õiged read.
