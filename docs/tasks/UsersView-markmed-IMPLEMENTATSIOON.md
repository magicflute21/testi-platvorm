# Kasutajate nimekiri ja kasutaja kustutamine (UsersView) — implementatsiooni plaan

**Seotud task:** `UsersView-markmed.md`

Plaan katab kaks backend teenust:
- `GET /api/users` — kõigi kasutajate nimekiri (`List<UserResponse>`), iga kasutaja juures kõigi tema gruppide nimed (`groupNames`)
- `DELETE /api/users/{userId}` — kasutaja soft delete (`user.status = 'I'`)

## Hetkeseis (mis on juba olemas)

Baaspakett: `../../backend/src/main/java/ee/testiplatvorm`

- `persistence/user/User.java` — `user` tabeli entiteet (`id`, `email`, `passwordHash`, `role` (ManyToOne → `Role`), `status`, `createdAt`, `updatedAt`).
- `persistence/user/UserRepository.java` — `JpaRepository<User, Integer>`, sisaldab ainult login'i päringut `findUserBy(email, passwordHash, status)`. `findById()`/`save()` on JpaRepository kaudu olemas.
- `persistence/user/UserMapper.java` — MapStruct mapper, sisaldab ainult `toLoginResponse(User)`.
- `persistence/user/UserDto.java` — IntelliJ genereeritud `@Value` DTO (`id`, `roleName`), **mitte kuskil kasutusel**.
- `persistence/Profile.java` — `profile` tabeli entiteet (`user` ManyToOne, `firstName`, `lastName`, …). Repository puudub (pole ka vaja).
- `persistence/Role.java` — `role` tabeli entiteet (`id`, `name`).
- `persistence/groupmember/GroupMember.java` — `group_member` entiteet (`group`, `user`, `addedBy`, …). **Repository puudub.**
- `persistence/group/Group.java` — `"group"` tabeli entiteet (`id`, `name`, `status`, …).
- `Status.java` — enum, sisaldab ainult `STATUS_ACTIVE("A")`.
- `Error.java` — enum, sisaldab ainult `INCORRECT_CREDENTIALS`.
- `infrastructure/exception/PrimaryKeyNotFoundException.java` — genereerib ise `message` ("Ei leidnud primary keyd '<field>' väärtusega: <value>") ja `errorCode` (`PRIMARY_KEY_NOT_FOUND`); `RestExceptionHandler` kaardistab selle → HTTP 404.
- `controller/login/LoginController.java` + `service/LoginService.java` — ainus olemasolev valmis teenus, eeskujuks kihtide ülesehitusele (DTO nimetamine `XxxRequest`/`XxxResponse`, `@RequiredArgsConstructor`, `@RestController`).
- `../database/3_import.sql` — staatuste legendis on `user.status = 'I'` (Inactive / soft delete) juba kirjas.

**Puudub:**
- `UserController` — kontroller `/api/users` endpointide jaoks puudub täielikult.
- `UserService` — kasutajate äriloogika teenus puudub (sh `getValidUserBy`).
- `UserResponse` DTO — puudub.
- `GroupMemberRepository` — puudub.
- Repository päring kasutajate nimekirja jaoks (koos profile ja role infoga) puudub.
- `Status` enumis puudub kustutatud staatus `'I'`.
- Teste projektis ei ole (`src/test/java/ee/testiplatvorm/` on tühi).

## Puuduv/muudetav

1. `Status.java` — lisada `STATUS_DELETED("I")`.
2. Uus DTO `controller/user/dto/UserResponse.java` (sh `List<String> groupNames`).
3. `UserRepository.java` — lisada JPQL konstruktori-avaldisega päring `findAllUserResponses()` (ilma gruppideta).
4. Uus `persistence/groupmember/GroupMemberRepository.java` päringuga `findAllGroupMembers()`.
5. Uus teenus `service/UserService.java` — `findAllUsers()`, `handleAddGroupNames(...)`, `deleteUser(...)`, `getValidUserBy(...)`.
6. Uus kontroller `controller/user/UserController.java` — `GET /api/users`, `DELETE /api/users/{userId}`.
7. Testid (service ühiktestid + kontrolleri testid).
8. (Valikuline) kasutamata `persistence/user/UserDto.java` kustutamine.

## Sammud

1. **Lisa kustutatud staatus** — fail: `../../backend/src/main/java/ee/testiplatvorm/Status.java`
   - Lisa enumi konstant `STATUS_DELETED("I")` (`STATUS_ACTIVE` kõrvale).
   - Kasutatakse `UserService.deleteUser()` sees; ära kasuta koodis maagilist stringi `"I"`.

2. **Loo response DTO** — fail: `backend/src/main/java/ee/testiplatvorm/controller/user/dto/UserResponse.java`
   - Nimetamine järgib olemasolevat stiili (`LoginRequest`/`LoginResponse`, ilma `Dto` lõputa).
   - DTO on ainult `user` ressursi oma → kuulub `controller/user/dto/`, mitte `controller/common/dto/`.
   - Lombok `@Data`, `@NoArgsConstructor`. `@AllArgsConstructor` **ei sobi**, sest JPQL `select new` ei oska kollektsiooni (`groupNames`) täita — selle asemel kirjuta käsitsi 6 argumendiga konstruktor (argumentide järjekord peab klappima päringuga), mis initsialiseerib `groupNames` tühja listina.
   ```java
   private Integer userId;
   private String firstName;
   private String lastName;
   private String email;
   private List<String> groupNames = new ArrayList<>();
   private String roleName;
   private String status;

   public UserResponse(Integer userId, String firstName, String lastName, String email, String roleName, String status) {
       this.userId = userId;
       this.firstName = firstName;
       this.lastName = lastName;
       this.email = email;
       this.roleName = roleName;
       this.status = status;
   }
   ```

3. **Lisa kasutajate päring repositooriumi** — fail: `../../backend/src/main/java/ee/testiplatvorm/persistence/user/UserRepository.java`
   - backend/CLAUDE.md: kohandatud päringud on JPQL `@Query`-ga, vajadusel konstruktori-avaldisega otse DTOsse. `Profile` on seotud `User`-iga ainult ühesuunaliselt (`User` entiteedis viidet pole), seega entity join `on`-tingimusega.
   - `profile` peab olema **left join** — ootel kasutajal (`userId` 4) profiili pole, ilma left join'ita jääks ta nimekirjast välja.
   - Gruppe siin **ei join'ita** — kasutaja võib olla mitmes grupis ja join tekitaks topeltridu. Grupid lisatakse service kihis (samm 5).
   - Meetodi nimi mainib tagastatavat subjekti (backend/CLAUDE.md repositooriumi nimetamise reegel).
   ```java
   @Query("""
           select new ee.testiplatvorm.controller.user.dto.UserResponse(
               u.id, p.firstName, p.lastName, u.email, r.name, u.status)
           from User u
           join u.role r
           left join Profile p on p.user = u
           order by u.id""")
   List<UserResponse> findAllUserResponses();
   ```
   - Mapperit (`UserMapper`) selle päringu jaoks vaja ei ole, kuna projektsioon tehakse otse DTOsse.

4. **Loo GroupMemberRepository** — fail: `backend/src/main/java/ee/testiplatvorm/persistence/groupmember/GroupMemberRepository.java`
   - `JpaRepository<GroupMember, Integer>`.
   - `join fetch gm.group` väldib N+1 päringuid (`group` on LAZY). `gm.getUser().getId()` ei tekita lisapäringut (Hibernate proxy teab ID-d).
   ```java
   @Query("select gm from GroupMember gm join fetch gm.group g order by g.name")
   List<GroupMember> findAllGroupMembers();
   ```

5. **Loo UserService** — fail: `backend/src/main/java/ee/testiplatvorm/service/UserService.java`
   - `@Service`, `@RequiredArgsConstructor`, sõltuvused `UserRepository userRepository`, `GroupMemberRepository groupMemberRepository`.
   - Meetodid (kutsumise hierarhia järjekorras):
   ```java
   public List<UserResponse> findAllUsers() {
       List<UserResponse> userResponses = userRepository.findAllUserResponses();
       handleAddGroupNames(userResponses);
       return userResponses;
   }

   public void deleteUser(Integer userId) {
       User user = getValidUserBy(userId);
       user.setStatus(STATUS_DELETED.getCode());
       user.setUpdatedAt(Instant.now());
       userRepository.save(user);
   }

   public User getValidUserBy(Integer userId) {
       return userRepository.findById(userId)
               .orElseThrow(() -> new PrimaryKeyNotFoundException("userId", userId));
   }

   private void handleAddGroupNames(List<UserResponse> userResponses) {
       Map<Integer, UserResponse> userResponsesByUserId = userResponses.stream()
               .collect(Collectors.toMap(UserResponse::getUserId, userResponse -> userResponse));
       List<GroupMember> groupMembers = groupMemberRepository.findAllGroupMembers();
       for (GroupMember groupMember : groupMembers) {
           UserResponse userResponse = userResponsesByUserId.get(groupMember.getUser().getId());
           userResponse.getGroupNames().add(groupMember.getGroup().getName());
       }
   }
   ```
   - `handleAddGroupNames` — `handle`-prefiks, sest meetod muteerib DTO-sid (backend/CLAUDE.md meetodite nimetamise reegel). Kaks päringut kokku sõltumata kasutajate arvust.
   - `deleteUser` **ei kontrolli** eelnevat staatust — juba kustutatud (`'I'`) kasutaja uuesti kustutamine ei ole viga (idempotentne, vastus 200).
   - `getValidUserBy` on `public` ja asub `UserService`-s — backend/CLAUDE.md reegel "Entiteedi otsing ID järgi".
   - Muutujate nimed peegeldavad tüüpi (`User user`, `List<UserResponse> userResponses`, `GroupMember groupMember`).
   - `LoginService` jääb eraldi alles; `UserService`-i sinna ei liideta.

6. **Loo UserController** — fail: `backend/src/main/java/ee/testiplatvorm/controller/user/UserController.java`
   - `@RestController`, `@RequiredArgsConstructor`, sõltuvus `UserService userService` (sama muster nagu `LoginController`).
   ```java
   @GetMapping("/api/users")
   public List<UserResponse> findAllUsers() {
       return userService.findAllUsers();
   }

   @DeleteMapping("/api/users/{userId}")
   public void deleteUser(@PathVariable Integer userId) {
       userService.deleteUser(userId);
   }
   ```
   - Kontroller ei sisalda äriloogikat ega rollikontrolli (vt "Tehtud otsused"), ainult delegeerib service'ile.
   - Swaggeri `@Operation`/`@ApiResponses` annotatsioone `LoginController` ei kasuta — järgi sama stiili.

7. **(Valikuline) Koristus** — fail: `../../backend/src/main/java/ee/testiplatvorm/persistence/user/UserDto.java`
   - Klass pole kuskil kasutusel ja võib `UserResponse`-ga segadust tekitada. Kustuta, kui meeskond on nõus.

8. **Testid** — vt jaotist "Testid" allpool.

## Veakäsitlus

| Teenus | Olukord | Erind | Kus visatakse | HTTP / errorCode |
|---|---|---|---|---|
| `GET /api/users` | — | — | — | Veateateid pole (tühi andmebaas → `[]`, 200) |
| `DELETE /api/users/{userId}` | `userId`-ga kasutajat pole | `PrimaryKeyNotFoundException("userId", userId)` | `UserService.getValidUserBy()` | 404 / `PRIMARY_KEY_NOT_FOUND`, message `"Ei leidnud primary keyd 'userId' väärtusega: 123"` |
| `DELETE /api/users/{userId}` | kasutaja on juba kustutatud (`'I'`) | — | — | Ei ole viga → 200 |

- Uut sõnumit `Error` enumi lisada pole vaja — `PrimaryKeyNotFoundException` koostab `message` ja `errorCode` ise.
- `RestExceptionHandler`-it muuta pole vaja.

## Testid

Testikaust `../../backend/src/test/java/ee/testiplatvorm` on tühi — eeskuju pole, testid tuleb luua nullist.

1. **`service/UserServiceTest.java`** (ühiktest, Mockito, `UserRepository` ja `GroupMemberRepository` mockitud):
   - `findAllUsers` — mitme grupiga kasutaja saab kõik `groupNames`, grupita kasutaja saab tühja listi `[]`.
   - `deleteUser` edukas — `user.status` muutub `"I"`-ks, `updatedAt` uueneb, `save()` kutsutakse.
   - `deleteUser` juba kustutatud kasutajale — erindit ei visata, `status` jääb `"I"`.
   - `deleteUser` olematu `userId` — visatakse `PrimaryKeyNotFoundException`, `errorCode == "PRIMARY_KEY_NOT_FOUND"`, `save()` ei kutsuta.
2. **`controller/user/UserControllerTest.java`** (`@WebMvcTest(UserController.class)`, `UserService` mockitud):
   - `GET /api/users` → 200, JSON massiiv õigete väljanimedega (`userId`, `firstName`, `lastName`, `email`, `groupNames`, `roleName`, `status`), `groupNames` on massiiv.
   - `DELETE /api/users/1` → 200, tühi body.
   - `DELETE /api/users/123` (service viskab erindi) → 404, `errorCode: PRIMARY_KEY_NOT_FOUND`.
3. (Valikuline) integratsioonitest `3_import.sql` andmetega — `userId` 3 tuleb nimekirja **üks kord** ja `groupNames` = `["Backend Team", "Frontend Team"]`; ootel kasutaja (`userId` 4, profile puudub) on nimekirjas `firstName`/`lastName` = `null`. Nõuab töötavat PostgreSQL-i.

## Tehtud otsused

1. **Kasutaja võib kuuluda mitmesse gruppi** — response'is on `groupNames` massiiv (Balsamiq märkmed uuendatud).
2. **Staatus `'I'`** — lisatud `3_import.sql` staatuste legendi (`user.status`: `'I'` = Inactive, soft delete).
3. **Kustutatud kasutaja uuesti kustutamine ei ole viga** — `deleteUser` on idempotentne, vastus 200.
4. **Ligipääsukontroll** — praeguses etapis backend rolli ei kontrolli; Admin/Halduri piirangut teeb ainult frontend (menüülink). Aktsepteeritud.
5. **backend/CLAUDE.md** — uuendatud: `Error` enum (mitte `ErrorResponse`), skeem `testi_platvorm` (mitte `minu_projekt`).

## Avatud küsimused

—
