# Juhend: POST /api/tests/{testId}/complete

**Taski fail:** `docs/balsamic/notes/POST-tests-testId-complete-markmed.md`
**Kontroller:** `TestAttemptController.java`
**Implementeerimise voog:** RestController → Service → Repository → Service → (Mapper/entiteedi koostamine) → Repository → Service → RestController

---

## Sissejuhatus

See endpoint lõpetab testi: frontend saadab korraga kõik kasutaja vastused, backend kontrollib iga küsimuse õigsust, arvutab punktid ning salvestab ühe `result` rea. Lisaks märgitakse kasutaja testimääramine (`user_test`) lõpetatuks.

Tegu on lihtsustatud (demo) versiooniga — `test_question_result` ja `test_question_answer` ridu **ei looda**.

Seotud tabelid: `user_test`, `test`, `test_question`, `question`, `question_answer`, `result`.

Selle harjutuse käigus õpid:
- võtma vastu `@RequestBody`-na **listi** DTO-sid
- kasutama olemasolevaid repository päringuid uues kontekstis
- arvutama service kihis äriloogikat (punktisumma, protsent, staatus)
- looma ja salvestama uut entiteeti ning uuendama olemasolevat
- kasutama `@Transactional`-i, kui üks toiming muudab mitut tabelit

**Veaolukorrad (taskist):**

| Olukord | HTTP | errorCode | message |
|---|---|---|---|
| Sisselogitud kasutajal pole selle testi kehtivat määramist | 403 | `NO_TEST_ASSIGNMENT_FOR_THIS_USER` | `Kasutajale ei ole vastavat testi määratud` |

**Vastus:** `200 OK` ilma sisuta.

---

## Samm 1 — RestController

### Mida teha?

`TestAttemptController` on juba olemas (seal on GET `/attempt` endpoint). Lisa sinna uus meetod, mis võtab vastu `testId` tee parameetrina ja vastuste listi päringu kehana.

Alusta meetodist ilma annotatsioonideta, seejärel lisa:
1. **Mappingannotatsioon** — `@PostMapping` (klassi `@RequestMapping` sisaldab juba `/api/tests/{testId}` osa)
2. **Parameetrite annotatsioonid** — `@PathVariable` ja `@RequestBody`
3. **Swagger annotatsioonid** — `@Operation` ja `@ApiResponses` (ära unusta 403 veaolukorda!)

```java
@PostMapping("/mingi-rada")
@Operation(summary = "Lühikokkuvõte")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "403", description = "Kirjeldus, mida viga sisaldab")})
public void meetodiNimi(@PathVariable Integer rajaParameeter, @RequestBody List<SisendDtoTüüp> sisendid) {
    teenuseMuutuja.meetodiNimi(...);
}
```

> **Mõtle:** Vaata, kuidas olemasolev GET meetod samas kontrolleris sisselogitud kasutaja `userId` kätte saab. Kas uus meetod peaks tegema sama moodi (kontrolleris) või service'is? Hoia ühtset stiili.

### Sisendi DTO

Päringu keha on list objektidest, millel on küsimuse id ja valitud vastusevariantide id-de list. DTO klass peab olema `controller/.../dto/` paketis.

> **Mõtle:** Kuidas saab Jackson (JSON → Java teisendaja) privaatsetele väljadele väärtused panna? Vaata teisi DTO-sid projektis — millised Lomboki annotatsioonid neil on?

---

## Samm 2 — Service ja määramise kontroll

### Mida teha?

Service meetodis on esimene ülesanne leida sisselogitud kasutaja kehtiv `user_test` rida `userId` ja `testId` järgi. Kui seda ei leita → 403.

> **Vihje:** Sama kontroll on juba olemas GET `/attempt` teenuses. Kasuta sama repository meetodit ja sama `Optional` käsitlust.

---

## Samm 3 — Repository: vajalikud andmed kokku

### Mida teha?

Tulemuse arvutamiseks on vaja teada:
1. **Kõiki testi küsimusi** (et arvutada `max_score` ja `total_questions`)
2. **Iga küsimuse õigeid vastusevariante** (et kontrollida, kas kasutaja vastus on õige)

> **Küsi endalt:** Kas mõni neist päringutest on repositoryde seas juba olemas? Vaata `TestQuestionRepository` ja `QuestionAnswerRepository` faile enne, kui uue meetodi lood.

Kui uut päringut on vaja, kasuta **JPA Buddy** abi:
1. Paremklõps repository failis → JPA Buddy → **Query**
2. Vali tüüp (**Find instance** / **Find collection**)
3. Lisa query conditionid
4. **Advanced** → **Named parameters**

> **Meetodi palve:** Kui kutsud välja meetodi, mis tagastab midagi, ja tahad selle infoga midagi edasi teha, pane see kohe muutujasse.

---

## Samm 4 — Service: õigsuse kontroll ja punktide arvutamine

### Mida teha?

Nüüd tuleb äriloogika. Taskist:
- Küsimus on **õige**, kui kasutaja valitud `answerIds` ühtivad **täpselt** küsimuse õigete variantidega (`correct_choice = true`)
- `score_total` = õigete küsimuste `question.score` summa
- `max_score` = kõigi testi küsimuste `score` summa
- `status` = `'P'`, kui `score_total / max_score * 100 >= test.pass_percent`, muidu `'F'`

> **Mõtle läbi (paberil enne koodi!):**
> - Kas iteratsiooni aluseks on parem võtta **testi küsimused** või **kasutaja saadetud vastused**? Mis juhtub, kui kasutaja jättis mõne küsimuse vastamata või saatis küsimuse, mis sellesse testi ei kuulu?
> - Mida tähendab "täpselt ühtivad"? Kas järjekord on oluline? Mis siis, kui kasutaja valis kõik õiged **ja** ühe vale?
> - `pass_percent` on `BigDecimal`. Kuidas võrrelda seda täisarvulisest jagamisest saadud protsendiga? Mis juhtub Java täisarvulise jagamisega (`7 / 10`)?
> - Kas `max_score` võib olla `0`?

Vajad veel kahte välja, mis on `result` tabelis `NOT NULL`: `total_questions` ja `questions_answered`. Mõtle, kust need väärtused tulevad.

> **Vihje:** Suure loogika saad jagada väiksemateks privaatseteks helper-meetoditeks (nt "kas see küsimus on õigesti vastatud?"). Aga alusta nii, et töötaks — ilusaks teeme Samm 7-s.

---

## Samm 5 — Result entiteedi koostamine ja salvestamine

### Mida teha?

Loo uus `Result` objekt ja täida kõik selle väljad (v.a `id`, mille genereerib andmebaas). Seejärel salvesta see.

> **Mõtle:** Kas siin on mõtet kasutada mapperit? Sisendiks pole üht DTO-d, mille väljad kattuks entiteediga — enamik väärtusi on arvutatud. Kui mapperi teed, pea meeles: iga target-väli peab olema kas `source`-iga või `ignore = true`-ga kaardistatud.

Kasuta `ResultRepository` baasmeetodit salvestamiseks.

> **Rusikareegel:** Lihtsa loomise puhul katab `save()` enamasti ära.

Taski järgi `started_at = completed_at = now`. Vaata entiteedist, mis tüüpi need väljad on, ja vali sobiv "praegune aeg" meetod.

---

## Samm 6 — user_test staatuse uuendamine

### Mida teha?

Taski järgi tuleb `user_test.status` seada väärtusele `'C'`. Vaata `Status` enum'it — kas sobiv konstant on olemas?

> **Mõtle:** See endpoint muudab nüüd **kahte** tabelit (`result` lisamine + `user_test` muutmine). Mis juhtub, kui esimene õnnestub ja teine ebaõnnestub? Millist Springi annotatsiooni kasutatakse, et need kaks muudatust toimuksid "kõik või mitte midagi" põhimõttel?

---

## Samm 7 — kood ilusaks (refactor)

### Make it work → Make it beautiful

Kui kood töötab, vaata, kas saab puhtamaks muuta.

**Extract Method IntelliJ'ga:** märgi koodilõik → paremklõps → Refactor → Extract Method.

> **Tähelepanu:** IntelliJ kasutab ekstraktimisel sageli kogu objekti parameetrina. Vaata üle, kas helper meetod vajab tegelikult kogu objekti või ainult üht välja.

Enne:
```java
kontrolliMidagiHelper(dtoObjekt);

private void kontrolliMidagiHelper(DtoTüüp dto) {
    boolean onProbleem = repositoorium.kontrollimeetod(dto.getMingiVäli());
    ...
}
```

Pärast (parem — anna edasi ainult vajalik):
```java
kontrolliMidagiHelper(dtoObjekt.getMingiVäli());

private void kontrolliMidagiHelper(VäljaTüüp väljaNimi) {
    boolean onProbleem = repositoorium.kontrollimeetod(väljaNimi);
    ...
}
```

Kontrolli ka repository meetodite nimesid — kas nimi vastab sellele, mida meetod tegelikult tagastab (nt "Ids" nimes, aga tagastab entiteete)?

### Meetodite järjekord

1. `public` meetodid enne
2. `private` meetodid pärast
3. Järjesta väljakutsumise hierarhia järgi — peameetod üleval, helperid all

---

## Kokkuvõte ja kontrollnimekiri

- [ ] Kontrolleri meetodil on `@PostMapping`, `@PathVariable`, `@RequestBody`
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponses` (sh 403)
- [ ] Sisendi DTO-l on vajalikud Lomboki annotatsioonid, et Jackson saaks JSON-i sisse lugeda
- [ ] `userId` saadakse samal viisil nagu teistes selle kontrolleri meetodites
- [ ] Määramise puudumisel visatakse `ForbiddenException` õige errorCode'iga
- [ ] Õigsust kontrollitakse backendis, "täpse ühtivuse" põhimõttel
- [ ] Protsendi arvutus ei kaota täpsust täisarvulise jagamise tõttu
- [ ] `Result` entiteedi kõik `NOT NULL` väljad on täidetud
- [ ] `user_test.status` muudetakse `'C'`-ks
- [ ] Service meetod on `@Transactional`
- [ ] Repository meetoditel on `@Query` Named parameters stiilis
- [ ] Meetodite järjekord: `public` enne, `private` pärast
- [ ] Kood kompileerub ja endpoint on Swagger UI-s nähtav

---

> **Järgmine samm:** Testi endpointi Swagger UI kaudu (`http://localhost:8080/swagger-ui/index.html`) — logi esmalt sisse, saada vastused ja kontrolli `psql`-iga, et `result` rida tekkis ja `user_test.status` muutus.
