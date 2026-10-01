-- ============================================================
-- Dummy seed data for the full schema (22 tables)
-- Order respects FK dependencies. Wrapped in a transaction.
--
-- ⚠️ NOTE: user.password_hash is varchar(30) in this script — too
-- short for a real bcrypt hash (60 chars). Using a short placeholder
-- below. Widen the column (varchar(60) or varchar(255)) before
-- wiring up real authentication.
--
-- Status code legend used below (table-specific, not universal):
--   user.status / competence.status / test.status / group.status /
--   competence_level.status / question.status / question_answer.status
--     'A' = Active, 'P' = Pending
--   invitation.status: 'P' = Pending, 'C' = Completed
--   user_test.status:  'O' = Open/in-progress, 'C' = Completed
--   result.status:     'P' = Pass, 'F' = Fail
--   ai_question.status: 'P' = Pending review, 'A' = Approved, 'R' = Rejected
--   test_question_result.status: 'OK' (generic marker, varchar(2))
--
-- paired_answer_id / ai_question_answer_id self-references are left
-- NULL throughout — they're only used for "matching" type questions,
-- which this minimal dataset doesn't include.
-- ============================================================

BEGIN;

-- ------------------------------------------------------------
-- role
-- ------------------------------------------------------------
INSERT INTO role (id, name) VALUES
    (1, 'ADMIN'),
    (2, 'HALDUR'),
    (3, 'KASUTAJA');

-- ------------------------------------------------------------
-- "user"  (4th row = pending invite, not yet completed registration)
-- ------------------------------------------------------------
INSERT INTO "user" (id, email, password_hash, role_id, status, created_at, updated_at)
VALUES (1, 'admin', '123', 1, 'A', now(), now()),
       (2, 'manager@example.com', 'manager123', 2, 'A', now() - interval '60 days', now()),
       (3, 'user@example.com', 'user123', 3, 'A', now() - interval '30 days', now()),
       (4, 'newhire@example.com', NULL, 3, 'P', now() - interval '2 days', now() - interval '2 days');

-- ------------------------------------------------------------
-- level
-- ------------------------------------------------------------
INSERT INTO "level" (id, "level", name, description)
VALUES (1, 1, 'Juunior', 'Teema põhiteadmised, töötab juhendamisel'),
       (2, 2, 'Medior', 'Praktilised, töös kasutatavad teadmised, töötab iseseisvalt'),
       (3, 3, 'Seenior', 'Eksperditasemel oskused, juhendab teisi ja teeb arhitektuurseid otsuseid'),
       -- Pehmete oskuste (nt Kommunikatsioon) tasemed
       (4, 1, 'Algaja', 'Tunneb põhimõtteid ja rakendab neid lihtsamates olukordades'),
       (5, 2, 'Edasijõudnu', 'Rakendab oskusi iseseisvalt ka keerulisemates olukordades'),
       (6, 3, 'Spetsialist', 'Valdab oskusi eeskujulikult ja oskab neid teistele õpetada');

-- ------------------------------------------------------------
-- competence
-- ------------------------------------------------------------
INSERT INTO competence (id, name, short_description, description, updated_at, doc_url, doc_filename, status, created_by,
                        created_at)
VALUES (1, 'JavaScript', 'JavaScripti keele alused', 'JavaScripti põhisüntaks, sulundid, asünkroonsed mustrid ja DOM.',
        now(), 'https://docs.example.com/js', 'javascripti_juhend.pdf', 'A', 1, now() - interval '90 days'),
       (2, 'SQL', 'Relatsiooniliste andmebaaside päringud',
        'SQL-päringute kirjutamine ja optimeerimine: liitmised, agregeerimine ja indeksid.', now(),
        'https://docs.example.com/sql', 'sql_juhend.pdf', 'A', 1, now() - interval '85 days'),
       (3, 'Kommunikatsioon', 'Professionaalsed kommunikatsioonioskused',
        'Kirjalik ja suuline suhtlus meeskonna- ja ärikontekstis.', now(), 'https://docs.example.com/comms',
        'kommunikatsiooni_juhend.pdf', 'A', 1, now() - interval '80 days'),
       (4, 'Vue.js', 'Vue 3 frontendi arendus',
        'Vue 3 komponendid, reaktiivsus, Options ja Composition API, Vue Router ning komponentidevaheline suhtlus.',
        now(), 'https://docs.example.com/vue', 'vue_juhend.pdf', 'A', 1, now() - interval '40 days'),
       (5, 'Spring Boot', 'Javas REST API-de arendus Spring Bootiga',
        'Spring Booti rakenduse kihid, REST kontrollerid, Spring Data JPA, valideerimine ja veakäsitlus.',
        now(), 'https://docs.example.com/spring-boot', 'spring_boot_juhend.pdf', 'A', 1, now() - interval '40 days'),
       (6, 'Git', 'Versioonihaldus Gitiga',
        'Commitid, harud, ühendamine (merge), rebase, konfliktide lahendamine ja pull requestid meeskonnatöös.',
        now(), 'https://docs.example.com/git', 'git_juhend.pdf', 'A', 1, now() - interval '40 days');

-- ------------------------------------------------------------
-- competence_level  (competence x level junction)
-- ------------------------------------------------------------
INSERT INTO competence_level (id, competence_id, level_id, status)
VALUES (1, 1, 1, 'A'),  -- JavaScript / Juunior
       (2, 1, 2, 'A'),  -- JavaScript / Medior
       (3, 2, 1, 'A'),  -- SQL / Juunior
       (4, 3, 4, 'A'),  -- Kommunikatsioon / Algaja
       (5, 4, 1, 'A'),  -- Vue.js / Juunior
       (6, 4, 2, 'A'),  -- Vue.js / Medior
       (7, 4, 3, 'A'),  -- Vue.js / Seenior
       (8, 5, 1, 'A'),  -- Spring Boot / Juunior
       (9, 5, 2, 'A'),  -- Spring Boot / Medior
       (10, 5, 3, 'A'), -- Spring Boot / Seenior
       (11, 6, 1, 'A'), -- Git / Juunior
       (12, 6, 2, 'A'), -- Git / Medior
       (13, 6, 3, 'A'), -- Git / Seenior
       (14, 3, 5, 'A'), -- Kommunikatsioon / Edasijõudnu
       (15, 3, 6, 'A'); -- Kommunikatsioon / Spetsialist

-- ------------------------------------------------------------
-- competence_file
-- ------------------------------------------------------------
INSERT INTO competence_file (id, competence_id, file_name, file_type, file_data)
VALUES (1, 1, 'javascripti_juhend.pdf', 'application/pdf', decode('ZHVtbXlmaWxlZGF0YQ==', 'base64')),
       (2, 2, 'sql_juhend.pdf', 'application/pdf', decode('ZHVtbXlmaWxlZGF0YQ==', 'base64'));

-- ------------------------------------------------------------
-- "group"
-- ------------------------------------------------------------
INSERT INTO "group" (id, name, status, created_at, updated_at)
VALUES (1, 'Frontendi meeskond', 'A', now() - interval '90 days', now()),
       (2, 'Backendi meeskond', 'A', now() - interval '90 days', now()),
       (3, 'Testijate meeskond', 'A', now() - interval '90 days', now());

-- ------------------------------------------------------------
-- group_manager
-- ------------------------------------------------------------
INSERT INTO group_manager (id, group_id, user_id, status)
VALUES (1, 1, 2, 'A'), -- manager manages Frontend
       (2, 2, 2, 'A'), -- manager manages Backend
       (3, 3, 1, 'A');
-- admin manages QA

-- ------------------------------------------------------------
-- group_member
-- ------------------------------------------------------------
INSERT INTO group_member (id, group_id, user_id, added_by, created_at, updated_at)
VALUES (1, 1, 3, 2, now() - interval '29 days', now() - interval '29 days'),
       (2, 2, 3, 2, now() - interval '20 days', now() - interval '20 days'),
       (3, 1, 1, 2, now() - interval '89 days', now() - interval '89 days');

-- ------------------------------------------------------------
-- invitation
-- ------------------------------------------------------------
INSERT INTO invitation (id, invited_by, user_id, token, created_at, expires_at, status)
VALUES (1, 1, 4, 'inv_tok_abc123pending', now() - interval '2 days', now() + interval '5 days', 'P'),
       (2, 1, 2, 'inv_tok_xyz987completed', now() - interval '61 days', now() - interval '54 days', 'C');

-- ------------------------------------------------------------
-- profile  (only for users who completed registration: 1, 2, 3)
-- ------------------------------------------------------------
INSERT INTO profile (id, user_id, first_name, last_name, phone_number, created_at, updated_at)
VALUES (1, 1, 'Anna', 'Admin', '+37255512345', now() - interval '90 days', now()),
       (2, 2, 'Marko', 'Haldur', '+37255598765', now() - interval '60 days', now()),
       (3, 3, 'Kati', 'Kasutaja', NULL, now() - interval '30 days', now());

-- ------------------------------------------------------------
-- question_type
-- ------------------------------------------------------------
INSERT INTO question_type (id, name)
VALUES (1, 'SINGLE_CHOICE'),
       (2, 'MULTIPLE_CHOICE'),
       (3, 'TRUE_FALSE');

-- ------------------------------------------------------------
-- question
-- ------------------------------------------------------------
INSERT INTO question (id, competence_id, competence_level_id, title, description, question_type_id, score, status,
                      created_at, created_by, updated_at)
VALUES (1, 1, 1, 'Mis on sulund (closure)?', 'Vali JavaScripti sulundi kõige täpsem definitsioon.', 1, 10, 'A',
        now() - interval '80 days', 1, now()),
       (2, 1, 2, 'Millised järgnevatest on JS primitiivtüübid?', 'Vali kõik JavaScripti primitiivtüübid.', 2, 10, 'A',
        now() - interval '75 days', 1, now()),
       (3, 2, 3, 'Kas SQL-i võtmesõnad on tõstutundlikud?', 'Tõene või väär.', 3, 5, 'A', now() - interval '70 days', 1,
        now()),
       (4, 1, 1, 'Mis vahe on let ja const vahel?', 'Vali õige väide.', 1, 10, 'A', now() - interval '60 days', 1,
        now()),
       (5, 1, 1, 'Mida tagastab typeof null?', 'Vali õige vastus.', 1, 10, 'A', now() - interval '60 days', 1, now()),
       (6, 2, 3, 'Milline käsk tagastab tabelist andmeid?', 'Vali õige SQL-i käsk.', 1, 10, 'A',
        now() - interval '55 days', 1, now()),
       (7, 2, 3, 'Kas WHERE-tingimus filtreerib ridu enne GROUP BY-d?', 'Tõene või väär.', 3, 5, 'A',
        now() - interval '55 days', 1, now()),
       (8, 3, 4, 'Mis on aktiivne kuulamine?', 'Vali kõige täpsem kirjeldus.', 1, 10, 'A', now() - interval '50 days', 1,
        now()),
       (9, 3, 4, 'Milline e-kirja pealkiri on kõige selgem?', 'Vali parim näide.', 1, 10, 'A',
        now() - interval '50 days', 1, now()),
       -- Vue.js
       (10, 4, 5, 'Milline direktiiv seob sisendvälja väärtuse kahesuunaliselt andmetega?',
        'Vali Vue direktiiv, mis hoiab sisendvälja ja komponendi andmed omavahel sünkroonis.', 1, 10, 'A',
        now() - interval '30 days', 1, now()),
       (11, 4, 6, 'Millised väited computed omaduste kohta on õiged?',
        'Vali kõik õiged väited Vue computed omaduste kohta.', 2, 10, 'A', now() - interval '30 days', 1, now()),
       (12, 4, 7, 'Kas alamkomponent tohib props väärtust otse muuta?',
        'Tõene või väär: alamkomponent võib vanemalt saadud props väärtust otse üle kirjutada.', 3, 5, 'A',
        now() - interval '30 days', 1, now()),
       -- Spring Boot
       (13, 5, 8, 'Milline annotatsioon märgib klassi REST kontrolleriks?',
        'Vali annotatsioon, mis teeb klassist REST endpointe pakkuva kontrolleri.', 1, 10, 'A',
        now() - interval '30 days', 1, now()),
       (14, 5, 9, 'Millised annotatsioonid seovad HTTP päringu andmed meetodi parameetriga?',
        'Vali kõik annotatsioonid, millega saab päringust andmeid kontrolleri meetodisse.', 2, 10, 'A',
        now() - interval '30 days', 1, now()),
       (15, 5, 10, 'Kas @Transactional meetodis visatud RuntimeException tühistab vaikimisi tehingu?',
        'Tõene või väär: Springi vaikekäitumisel tehakse kontrollimata erindi korral rollback.', 3, 5, 'A',
        now() - interval '30 days', 1, now()),
       -- Git
       (16, 6, 11, 'Milline käsk loob uue haru ja lülitub kohe sellele?',
        'Vali käsk, mis teeb mõlemat korraga.', 1, 10, 'A', now() - interval '30 days', 1, now()),
       (17, 6, 12, 'Millised käsud muudavad kohalikku tööpuud või haru ajalugu?',
        'Vali kõik käsud, mis muudavad kohalikke faile või harusid (mitte ainult ei loe infot).', 2, 10, 'A',
        now() - interval '30 days', 1, now()),
       (18, 6, 13, 'Kas jagatud haru rebase ja force push võib teiste arendajate töö segamini ajada?',
        'Tõene või väär: juba pushitud ja teistega jagatud haru ajaloo ümberkirjutamine võib teistele probleeme tekitada.',
        3, 5, 'A', now() - interval '30 days', 1, now()),
       -- Kommunikatsioon
       (19, 3, 14, 'Millised võtted aitavad konfliktset arutelu rahulikult lahendada?',
        'Vali kõik võtted, mis aitavad meeskonnas tekkinud pingelises arutelus jõuda lahenduseni.', 2, 10, 'A',
        now() - interval '30 days', 1, now()),
       (20, 3, 15, 'Kas tagasiside on kõige tõhusam, kui see keskendub isiku omadustele, mitte käitumisele?',
        'Tõene või väär: hea tagasiside hindab inimest ennast, mitte tema konkreetset tegevust või tulemust.', 3, 5, 'A',
        now() - interval '30 days', 1, now());

-- ------------------------------------------------------------
-- question_answer
-- ------------------------------------------------------------
INSERT INTO question_answer (id, question_id, answer_text, correct_choice, correct_position, paired_answer_id, status)
VALUES (1, 1, 'Funktsioon, mis mäletab oma leksikaalset skoopi', true, NULL, NULL, 'A'),
       (2, 1, 'Tsükkel, mis ei lõppe kunagi', false, NULL, NULL, 'A'),
       (3, 1, 'CSS-i omadus', false, NULL, NULL, 'A'),
       (4, 2, 'string', true, NULL, NULL, 'A'),
       (5, 2, 'number', true, NULL, NULL, 'A'),
       (6, 2, 'massiiv', false, NULL, NULL, 'A'),
       (7, 2, 'objekt', false, NULL, NULL, 'A'),
       (8, 3, 'Tõene', false, NULL, NULL, 'A'),
       (9, 3, 'Väär', true, NULL, NULL, 'A'),
       (10, 4, 'const muutujale ei saa uut väärtust omistada', true, NULL, NULL, 'A'),
       (11, 4, 'let muutuja on alati globaalne', false, NULL, NULL, 'A'),
       (12, 4, 'Nende vahel ei ole vahet', false, NULL, NULL, 'A'),
       (13, 5, '"object"', true, NULL, NULL, 'A'),
       (14, 5, '"null"', false, NULL, NULL, 'A'),
       (15, 5, '"undefined"', false, NULL, NULL, 'A'),
       (16, 6, 'SELECT', true, NULL, NULL, 'A'),
       (17, 6, 'INSERT', false, NULL, NULL, 'A'),
       (18, 6, 'UPDATE', false, NULL, NULL, 'A'),
       (19, 7, 'Tõene', true, NULL, NULL, 'A'),
       (20, 7, 'Väär', false, NULL, NULL, 'A'),
       (21, 8, 'Kõnelejale täielik keskendumine ja tagasiside andmine', true, NULL, NULL, 'A'),
       (22, 8, 'Vestluskaaslase katkestamine oma mõtte jagamiseks', false, NULL, NULL, 'A'),
       (23, 8, 'Samal ajal e-kirjadele vastamine', false, NULL, NULL, 'A'),
       (24, 9, 'Kohtumise aja muutus: neljapäev kell 14', true, NULL, NULL, 'A'),
       (25, 9, 'Küsimus', false, NULL, NULL, 'A'),
       (26, 9, 'Tere!', false, NULL, NULL, 'A'),
       -- Vue.js
       (27, 10, 'v-model', true, NULL, NULL, 'A'),
       (28, 10, 'v-bind', false, NULL, NULL, 'A'),
       (29, 10, 'v-if', false, NULL, NULL, 'A'),
       (30, 10, 'v-for', false, NULL, NULL, 'A'),
       (31, 11, 'Computed omaduse väärtus puhverdatakse ja arvutatakse uuesti alles siis, kui sõltuvused muutuvad', true, NULL, NULL, 'A'),
       (32, 11, 'Computed omadust kasutatakse mallis nagu tavalist andmevälja, ilma sulgudeta', true, NULL, NULL, 'A'),
       (33, 11, 'Computed omadus käivitub igal renderdamisel uuesti nagu meetod', false, NULL, NULL, 'A'),
       (34, 11, 'Computed omaduses on hea teha API päringuid', false, NULL, NULL, 'A'),
       (35, 12, 'Tõene', false, NULL, NULL, 'A'),
       (36, 12, 'Väär', true, NULL, NULL, 'A'),
       -- Spring Boot
       (37, 13, '@RestController', true, NULL, NULL, 'A'),
       (38, 13, '@Service', false, NULL, NULL, 'A'),
       (39, 13, '@Repository', false, NULL, NULL, 'A'),
       (40, 13, '@Entity', false, NULL, NULL, 'A'),
       (41, 14, '@PathVariable', true, NULL, NULL, 'A'),
       (42, 14, '@RequestParam', true, NULL, NULL, 'A'),
       (43, 14, '@RequestBody', true, NULL, NULL, 'A'),
       (44, 14, '@Autowired', false, NULL, NULL, 'A'),
       (45, 15, 'Tõene', true, NULL, NULL, 'A'),
       (46, 15, 'Väär', false, NULL, NULL, 'A'),
       -- Git
       (47, 16, 'git checkout -b uus-haru', true, NULL, NULL, 'A'),
       (48, 16, 'git branch uus-haru', false, NULL, NULL, 'A'),
       (49, 16, 'git commit -b uus-haru', false, NULL, NULL, 'A'),
       (50, 16, 'git push uus-haru', false, NULL, NULL, 'A'),
       (51, 17, 'git merge', true, NULL, NULL, 'A'),
       (52, 17, 'git rebase', true, NULL, NULL, 'A'),
       (53, 17, 'git status', false, NULL, NULL, 'A'),
       (54, 17, 'git log', false, NULL, NULL, 'A'),
       (55, 18, 'Tõene', true, NULL, NULL, 'A'),
       (56, 18, 'Väär', false, NULL, NULL, 'A'),
       -- Kommunikatsioon
       (57, 19, 'Kuulata teise poole seisukoht lõpuni ära ja võtta see oma sõnadega kokku', true, NULL, NULL, 'A'),
       (58, 19, 'Rääkida oma vajadustest mina-sõnumitega, näiteks „Mul on raske, kui…"', true, NULL, NULL, 'A'),
       (59, 19, 'Otsida mõlemale poolele sobivaid lahendusi, mitte süüdlast', true, NULL, NULL, 'A'),
       (60, 19, 'Tõsta häält, et oma seisukoht selgelt kõlama jääks', false, NULL, NULL, 'A'),
       (61, 20, 'Tõene', false, NULL, NULL, 'A'),
       (62, 20, 'Väär', true, NULL, NULL, 'A');

-- ------------------------------------------------------------
-- test
-- ------------------------------------------------------------
INSERT INTO test (id, competence_id, competence_level_id, name, short_description, description, is_timed, timer_min, pass_percent,
                  round_score_up, status, created_by, created_at, updated_at)
VALUES (1, 1, 1, 'JavaScripti algtaseme test', 'JavaScripti algteadmiste kontroll.', 'Test algajatele JS-i baasteadmiste kontrollimiseks.', true, 30, 60.00, true, 'A', 1,
        now() - interval '75 days', now()),
       (2, 1, 2, 'JavaScripti kesktaseme test', 'JavaScripti süvitsi minevad teemad.', 'Kesktaseme test JS-i süvitsi minevate teemade kohta.', true, 45, 70.00, false, 'A', 1,
        now() - interval '70 days', now()),
       (3, 2, 3, 'SQL-i aluste test', 'SQL-i põhiteadmiste kontroll.', 'Lühike test SQL-i põhiteadmiste hindamiseks.', false, NULL, 65.00, true, 'A', 1,
        now() - interval '65 days', now());
-- ------------------------------------------------------------
-- test_question
-- ------------------------------------------------------------
INSERT INTO test_question (id, test_id, question_id, position, added_by, created_at, updated_at)
VALUES (1, 1, 1, 1, 1, now() - interval '75 days', now()),
       (2, 1, 2, 2, 1, now() - interval '75 days', now()),
       (3, 2, 2, 1, 1, now() - interval '70 days', now()),
       (4, 3, 3, 1, 1, now() - interval '65 days', now());

-- ------------------------------------------------------------
-- user_test (assignments) — 2 currently open, 3 completed
-- ------------------------------------------------------------
INSERT INTO user_test (id, test_id, user_id, opens_at, closes_at, status, group_id, assigned_by, created_at)
VALUES (1, 1, 3, now(), now() + interval '7 days', 'O', 1, 2, now()),
       (2, 3, 3, now() - interval '3 days', now() + interval '4 days', 'O', 1, 2, now() - interval '3 days'),
       (3, 1, 1, now() - interval '10 days', now() - interval '3 days', 'O', 2, 2, now() - interval '10 days'),
       (4, 2, 2, now() - interval '15 days', now() - interval '8 days', 'C', 2, 1, now() - interval '15 days'),
       (5, 3, 1, now() - interval '20 days', now() - interval '13 days', 'C', 1, 2, now() - interval '20 days');

-- ------------------------------------------------------------
-- result  (only for the 3 completed user_test rows: 3, 4, 5)
-- ------------------------------------------------------------
INSERT INTO result (id, user_test_id, status, max_score, score_total, completed_at, started_at, total_questions,
                    questions_answered)
VALUES (1, 3, 'P', 20, 20, now() - interval '3 days', now() - interval '3 days' - interval '25 min', 2, 2),
       (2, 4, 'P', 10, 10, now() - interval '8 days', now() - interval '8 days' - interval '15 min', 1, 1),
       (3, 5, 'F', 5, 0, now() - interval '13 days', now() - interval '13 days' - interval '5 min', 1, 1);

-- ------------------------------------------------------------
-- test_question_result  (per-question breakdown of each result)
-- ------------------------------------------------------------
INSERT INTO test_question_result (id, result_id, question_id, correct_answer, status, sequence_number)
VALUES (1, 1, 1, true, 'OK', 1),
       (2, 1, 2, true, 'OK', 2),
       (3, 2, 2, true, 'OK', 1),
       (4, 3, 3, false, 'OK', 1);

-- ------------------------------------------------------------
-- test_question_answer  (which specific answer(s) the user picked)
-- ------------------------------------------------------------
INSERT INTO test_question_answer (id, test_question_result_id, question_answer_id, correct_choice_user_answer,
                                  correct_position_user_answer, paired_answer_id_user_answer)
VALUES (1, 1, 1, true, NULL, NULL), -- correctly picked closure definition
       (2, 2, 4, true, NULL, NULL), -- picked "string" for primitive types
       (3, 2, 5, true, NULL, NULL), -- picked "number" for primitive types
       (4, 3, 4, true, NULL, NULL), -- manager's run of test 2, same correct picks
       (5, 3, 5, true, NULL, NULL),
       (6, 4, 8, false, NULL, NULL);
-- picked "Tõene" — wrong, SQL keywords aren't case-sensitive

-- ------------------------------------------------------------
-- ai_question  (AI-suggested draft questions awaiting review)
-- ------------------------------------------------------------
INSERT INTO ai_question (id, title, description, competence_id, competence_level_id, question_type_id, status,
                         created_at, created_by, updated_at, score, feedback, is_good)
VALUES (1, 'Millele viitab "this" noolefunktsioonis?', 'Vali parim vastus.', 1, 2, 1, 'P', now() - interval '1 day', 1,
        now() - interval '1 day', NULL, NULL, NULL),
       (2, 'Selgita SQL-i JOIN-tüüpe', 'Lühivastus / mõiste kontroll.', 2, 3, 1, 'A', now() - interval '5 days', 1,
        now() - interval '4 days', 8, 'Selge küsimus, sõnastust tuleb veidi täpsustada', true),
       (3, 'JavaScript on dünaamiliselt tüübitud – tõene või väär?', 'Tõene/väär kontroll.', 1, 1, 3, 'R',
        now() - interval '6 days', 1, now() - interval '5 days', 3, 'Liiga lihtne, kattub olemasoleva küsimusega',
        false);

-- ------------------------------------------------------------
-- ai_question_answer
-- ------------------------------------------------------------
INSERT INTO ai_question_answer (id, ai_question_id, is_correct, correct_position, status, answer_text,
                                ai_question_answer_id, score, feedback)
VALUES (1, 1, true, NULL, 'P', 'Ümbritseva leksikaalse skoobi "this" väärtus', NULL, 5, 4),
       (2, 1, false, NULL, 'P', 'Alati globaalne window-objekt', NULL, 2, 2),
       (3, 2, true, NULL, 'A', 'INNER, LEFT, RIGHT ja FULL OUTER liitmised', NULL, 5, 5),
       (4, 3, true, NULL, 'R', 'Tõene', NULL, 3, 3),
       (5, 3, false, NULL, 'R', 'Väär', NULL, 1, 1);

-- ------------------------------------------------------------
-- Keep sequences in sync after manual id inserts
-- ------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('role', 'id'), (SELECT MAX(id) FROM role));
SELECT setval(pg_get_serial_sequence('"user"', 'id'), (SELECT MAX(id) FROM "user"));
SELECT setval(pg_get_serial_sequence('"level"', 'id'), (SELECT MAX(id) FROM "level"));
SELECT setval(pg_get_serial_sequence('competence', 'id'), (SELECT MAX(id) FROM competence));
SELECT setval(pg_get_serial_sequence('competence_level', 'id'), (SELECT MAX(id) FROM competence_level));
SELECT setval(pg_get_serial_sequence('competence_file', 'id'), (SELECT MAX(id) FROM competence_file));
SELECT setval(pg_get_serial_sequence('"group"', 'id'), (SELECT MAX(id) FROM "group"));
SELECT setval(pg_get_serial_sequence('group_manager', 'id'), (SELECT MAX(id) FROM group_manager));
SELECT setval(pg_get_serial_sequence('group_member', 'id'), (SELECT MAX(id) FROM group_member));
SELECT setval(pg_get_serial_sequence('invitation', 'id'), (SELECT MAX(id) FROM invitation));
SELECT setval(pg_get_serial_sequence('profile', 'id'), (SELECT MAX(id) FROM profile));
SELECT setval(pg_get_serial_sequence('question_type', 'id'), (SELECT MAX(id) FROM question_type));
SELECT setval(pg_get_serial_sequence('question', 'id'), (SELECT MAX(id) FROM question));
SELECT setval(pg_get_serial_sequence('question_answer', 'id'), (SELECT MAX(id) FROM question_answer));
SELECT setval(pg_get_serial_sequence('test', 'id'), (SELECT MAX(id) FROM test));
SELECT setval(pg_get_serial_sequence('test_question', 'id'), (SELECT MAX(id) FROM test_question));
SELECT setval(pg_get_serial_sequence('user_test', 'id'), (SELECT MAX(id) FROM user_test));
SELECT setval(pg_get_serial_sequence('result', 'id'), (SELECT MAX(id) FROM result));
SELECT setval(pg_get_serial_sequence('test_question_result', 'id'), (SELECT MAX(id) FROM test_question_result));
SELECT setval(pg_get_serial_sequence('test_question_answer', 'id'), (SELECT MAX(id) FROM test_question_answer));
SELECT setval(pg_get_serial_sequence('ai_question', 'id'), (SELECT MAX(id) FROM ai_question));
SELECT setval(pg_get_serial_sequence('ai_question_answer', 'id'), (SELECT MAX(id) FROM ai_question_answer));

COMMIT;
