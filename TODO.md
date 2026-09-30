# TODO — turvaparandused

## Tõsised

- [ ] **`DELETE /api/users/{userId}` ei kontrolli õigusi ega sisselogimist** (`UserController.deleteUser` → `UserService.deleteUser`)
  - Igaüks, kes teab backendi aadressi, saab suvalise kasutaja deaktiveerida — ka sisse logimata
  - Parandus: võtta kasutaja sessioonist (`currentUserService.getUserId()`) ja lubada ainult `ADMIN` rollile, muidu `ForbiddenException`

- [ ] **Paroolid on andmebaasis lihttekstina** (`user.password_hash`, `UserRepository.findUserBy`)
  - Sisselogimine võrdleb parooli otse andmebaasi väärtusega (nt `admin123`) — andmebaasi lekkimisel lekivad kõik paroolid
  - Parandus: hoida BCrypt räsi ja kontrollida `passwordEncoder.matches()` abil
  - Vaja muuta ka `docs/database/3_import.sql` algandmed ja laiendada `password_hash` veergu (BCrypt räsi on 60 märki)

- [ ] **`POST /tests` usaldab `userId` väärtust päringu kehast** (`TestService.createTest`, `TestCreateRequestDto.userId`)
  - Tavakasutaja saab saata `"userId": 1` ja luua testi admini nimel, sest rollikontroll vaatab võltsitud kasutajat
  - Parandus: võtta `userId` sessioonist `currentUserService.getUserId()` kaudu ja eemaldada väli DTO-st (ja frontendist `TestCreateView.vue`)

## Keskmised

- [ ] **`GET /api/users` on sisselogimiseta avatud** (`UserController.findAllUsers`)
  - Tagastab kõigi kasutajate andmed koos e-mailidega
  - Parandus: nõuda sisselogimist ja sobivat rolli

- [ ] **Enamik endpointe ei kontrolli sisselogimist** (nt `/api/competences`, `/api/competence-levels`, `/api/questions`, `/tests`)
  - `/api/questions` võib avaldada testiküsimused ja õiged vastused
  - Parandus: kontrollida sessiooni kõigis endpointides, mis pole avalikud (vt `CurrentUserService`); kaaluda ühist lahendust (nt interceptor või Spring Security), et kontrolli poleks vaja igasse meetodisse eraldi kirjutada

- [ ] **Rollikontroll on frontendis ainult kosmeetiline**
  - `sessionStorage` väärtusi (`userId`, `roleName`) saab brauseris muuta
  - Parandus: iga õigust nõudev tegevus peab olema kontrollitud ka backendis (vt punktid ülal)

## AI küsimuste loomine (teadlikud kompromissid)

- [ ] **Tunnilimiit (30 küsimust) on backendi mälus** (`AiQuestionLimitService`)
  - Backendi taaskäivitamisel loendur nullitakse; mõjutab ainult AI kulusid, mitte andmete turvalisust
  - Parandus vajadusel: hoida loendurit andmebaasis (nt `ai_question.created_at` põhjal)

- [ ] **Google'ile saadetakse andmeid** (`AiQuestionService` promptid)
  - Kompetentside nimed ja kirjeldused, olemasolevate küsimuste pealkirjad ja kasutaja sisestatud tekst; kasutajate isikuandmeid ei saadeta
  - Kui kompetentside sisu on konfidentsiaalne, tuleb seda arvestada
