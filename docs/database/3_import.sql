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
    (2, 'MANAGER'),
    (3, 'USER');

-- ------------------------------------------------------------
-- "user"  (4th row = pending invite, not yet completed registration)
-- ------------------------------------------------------------
INSERT INTO "user" (id, email, password_hash, role_id, status, created_at, updated_at) VALUES
    (1, 'admin@example.com',    'DUMMY_HASH_NOT_REAL', 1, 'A', now() - interval '90 days', now()),
    (2, 'manager@example.com',  'DUMMY_HASH_NOT_REAL', 2, 'A', now() - interval '60 days', now()),
    (3, 'user@example.com',     'DUMMY_HASH_NOT_REAL', 3, 'A', now() - interval '30 days', now()),
    (4, 'newhire@example.com',  NULL,                  3, 'P', now() - interval '2 days',  now() - interval '2 days');

-- ------------------------------------------------------------
-- level
-- ------------------------------------------------------------
INSERT INTO "level" (id, "level", name, description) VALUES
    (1, 1, 'Beginner',     'Basic understanding of the topic'),
    (2, 2, 'Intermediate', 'Practical, working knowledge'),
    (3, 3, 'Advanced',     'Expert-level mastery');

-- ------------------------------------------------------------
-- competence
-- ------------------------------------------------------------
INSERT INTO competence (id, name, short_description, description, updated_at, doc_url, doc_filename, status, created_by, created_at) VALUES
    (1, 'JavaScript', 'JS language fundamentals', 'Core JavaScript syntax, closures, async patterns, and the DOM.', now(), 'https://docs.example.com/js', 'javascript_guide.pdf', 'A', 1, now() - interval '90 days'),
    (2, 'SQL',        'Relational database querying', 'Writing and optimizing SQL queries across joins, aggregation, and indexing.', now(), 'https://docs.example.com/sql', 'sql_guide.pdf', 'A', 1, now() - interval '85 days'),
    (3, 'Communication', 'Professional communication skills', 'Written and verbal communication in a team/business context.', now(), 'https://docs.example.com/comms', 'comms_guide.pdf', 'A', 1, now() - interval '80 days');

-- ------------------------------------------------------------
-- competence_level  (competence x level junction)
-- ------------------------------------------------------------
INSERT INTO competence_level (id, competence_id, level_id, status) VALUES
    (1, 1, 1, 'A'),  -- JavaScript / Beginner
    (2, 1, 2, 'A'),  -- JavaScript / Intermediate
    (3, 2, 1, 'A');  -- SQL / Beginner

-- ------------------------------------------------------------
-- competence_file
-- ------------------------------------------------------------
INSERT INTO competence_file (id, competence_id, file_name, file_type, file_data) VALUES
    (1, 1, 'javascript_guide.pdf', 'application/pdf', decode('ZHVtbXlmaWxlZGF0YQ==', 'base64')),
    (2, 2, 'sql_guide.pdf',        'application/pdf', decode('ZHVtbXlmaWxlZGF0YQ==', 'base64'));

-- ------------------------------------------------------------
-- "group"
-- ------------------------------------------------------------
INSERT INTO "group" (id, name, status, created_at, updated_at) VALUES
    (1, 'Frontend Team', 'A', now() - interval '90 days', now()),
    (2, 'Backend Team',  'A', now() - interval '90 days', now()),
    (3, 'QA Team',       'A', now() - interval '90 days', now());

-- ------------------------------------------------------------
-- group_manager
-- ------------------------------------------------------------
INSERT INTO group_manager (id, group_id, user_id, status) VALUES
    (1, 1, 2, 'A'),  -- manager manages Frontend
    (2, 2, 2, 'A'),  -- manager manages Backend
    (3, 3, 1, 'A');  -- admin manages QA

-- ------------------------------------------------------------
-- group_member
-- ------------------------------------------------------------
INSERT INTO group_member (id, group_id, user_id, added_by, created_at, updated_at) VALUES
    (1, 1, 3, 2, now() - interval '29 days', now() - interval '29 days'),
    (2, 2, 3, 2, now() - interval '20 days', now() - interval '20 days'),
    (3, 1, 1, 2, now() - interval '89 days', now() - interval '89 days');

-- ------------------------------------------------------------
-- invitation
-- ------------------------------------------------------------
INSERT INTO invitation (id, invited_by, user_id, token, created_at, expires_at, status) VALUES
    (1, 1, 4, 'inv_tok_abc123pending', now() - interval '2 days', now() + interval '5 days', 'P'),
    (2, 1, 2, 'inv_tok_xyz987completed', now() - interval '61 days', now() - interval '54 days', 'C');

-- ------------------------------------------------------------
-- profile  (only for users who completed registration: 1, 2, 3)
-- ------------------------------------------------------------
INSERT INTO profile (id, user_id, first_name, last_name, phone_number, created_at, updated_at) VALUES
    (1, 1, 'Anna',  'Admin',    '+37255512345', now() - interval '90 days', now()),
    (2, 2, 'Marko', 'Manager',  '+37255598765', now() - interval '60 days', now()),
    (3, 3, 'Kati',  'Kasutaja', NULL,           now() - interval '30 days', now());

-- ------------------------------------------------------------
-- question_type
-- ------------------------------------------------------------
INSERT INTO question_type (id, name) VALUES
    (1, 'SINGLE_CHOICE'),
    (2, 'MULTIPLE_CHOICE'),
    (3, 'TRUE_FALSE');

-- ------------------------------------------------------------
-- question
-- ------------------------------------------------------------
INSERT INTO question (id, competence_id, competence_level_id, title, description, question_type_id, score, status, created_at, created_by, updated_at) VALUES
    (1, 1, 1, 'What is a closure?',                             'Select the best definition of a closure in JavaScript.', 1, 10, 'A', now() - interval '80 days', 1, now()),
    (2, 1, 2, 'Which of the following are JS primitive types?', 'Select all that are JavaScript primitive types.',        2, 10, 'A', now() - interval '75 days', 1, now()),
    (3, 2, 3, 'Is SQL case-sensitive for keywords?',             'True or false.',                                          3,  5, 'A', now() - interval '70 days', 1, now());

-- ------------------------------------------------------------
-- question_answer
-- ------------------------------------------------------------
INSERT INTO question_answer (id, question_id, answer_text, correct_choice, correct_position, paired_answer_id, status) VALUES
    (1, 1, 'A function that remembers its lexical scope', true,  NULL, NULL, 'A'),
    (2, 1, 'A loop that never ends',                       false, NULL, NULL, 'A'),
    (3, 1, 'A CSS property',                               false, NULL, NULL, 'A'),
    (4, 2, 'string',                                       true,  NULL, NULL, 'A'),
    (5, 2, 'number',                                       true,  NULL, NULL, 'A'),
    (6, 2, 'array',                                         false, NULL, NULL, 'A'),
    (7, 2, 'object',                                        false, NULL, NULL, 'A'),
    (8, 3, 'True',                                          false, NULL, NULL, 'A'),
    (9, 3, 'False',                                         true,  NULL, NULL, 'A');

-- ------------------------------------------------------------
-- test
-- ------------------------------------------------------------
INSERT INTO test (id, competence_id, competence_level_id, name, description, is_timed, timer_min, pass_percent, round_score_up, status, created_by, created_at, updated_at) VALUES
    (1, 1, 1, 'JavaScript Basics Test',       'Entry-level JavaScript knowledge check.',  true,  30,   60.00, true,  'A', 1, now() - interval '75 days', now()),
    (2, 1, 2, 'JavaScript Intermediate Test', 'Deeper JavaScript concepts.',              true,  45,   70.00, false, 'A', 1, now() - interval '70 days', now()),
    (3, 2, 3, 'SQL Fundamentals Test',        'Basic SQL knowledge check.',               false, NULL, 65.00, true,  'A', 1, now() - interval '65 days', now());

-- ------------------------------------------------------------
-- test_question
-- ------------------------------------------------------------
INSERT INTO test_question (id, test_id, question_id, position, added_by, created_at, updated_at) VALUES
    (1, 1, 1, 1, 1, now() - interval '75 days', now()),
    (2, 1, 2, 2, 1, now() - interval '75 days', now()),
    (3, 2, 2, 1, 1, now() - interval '70 days', now()),
    (4, 3, 3, 1, 1, now() - interval '65 days', now());

-- ------------------------------------------------------------
-- user_test (assignments) — 2 currently open, 3 completed
-- ------------------------------------------------------------
INSERT INTO user_test (id, test_id, user_id, opens_at, closes_at, status, group_id, assigned_by, created_at) VALUES
    (1, 1, 3, now(),                     now() + interval '7 days',  'O', 1, 2, now()),
    (2, 3, 3, now() - interval '3 days', now() + interval '4 days',  'O', 1, 2, now() - interval '3 days'),
    (3, 1, 1, now() - interval '10 days',now() - interval '3 days',  'C', 2, 2, now() - interval '10 days'),
    (4, 2, 2, now() - interval '15 days',now() - interval '8 days',  'C', 2, 1, now() - interval '15 days'),
    (5, 3, 1, now() - interval '20 days',now() - interval '13 days', 'C', 1, 2, now() - interval '20 days');

-- ------------------------------------------------------------
-- result  (only for the 3 completed user_test rows: 3, 4, 5)
-- ------------------------------------------------------------
INSERT INTO result (id, user_test_id, status, max_score, score_total, completed_at, started_at, total_questions, questions_answered) VALUES
    (1, 3, 'P', 20, 20, now() - interval '3 days',  now() - interval '3 days' - interval '25 min', 2, 2),
    (2, 4, 'P', 10, 10, now() - interval '8 days',  now() - interval '8 days' - interval '15 min', 1, 1),
    (3, 5, 'F', 5,  0,  now() - interval '13 days', now() - interval '13 days' - interval '5 min',  1, 1);

-- ------------------------------------------------------------
-- test_question_result  (per-question breakdown of each result)
-- ------------------------------------------------------------
INSERT INTO test_question_result (id, result_id, question_id, correct_answer, status, sequence_number) VALUES
    (1, 1, 1, true,  'OK', 1),
    (2, 1, 2, true,  'OK', 2),
    (3, 2, 2, true,  'OK', 1),
    (4, 3, 3, false, 'OK', 1);

-- ------------------------------------------------------------
-- test_question_answer  (which specific answer(s) the user picked)
-- ------------------------------------------------------------
INSERT INTO test_question_answer (id, test_question_result_id, question_answer_id, correct_choice_user_answer, correct_position_user_answer, paired_answer_id_user_answer) VALUES
    (1, 1, 1, true,  NULL, NULL),  -- correctly picked closure definition
    (2, 2, 4, true,  NULL, NULL),  -- picked "string" for primitive types
    (3, 2, 5, true,  NULL, NULL),  -- picked "number" for primitive types
    (4, 3, 4, true,  NULL, NULL),  -- manager's run of test 2, same correct picks
    (5, 3, 5, true,  NULL, NULL),
    (6, 4, 8, false, NULL, NULL);  -- picked "True" — wrong, SQL keywords aren't case-sensitive

-- ------------------------------------------------------------
-- ai_question  (AI-suggested draft questions awaiting review)
-- ------------------------------------------------------------
INSERT INTO ai_question (id, title, description, competence_id, competence_level_id, question_type_id, status, created_at, created_by, updated_at, score, feedback, is_good) VALUES
    (1, 'What does "this" refer to in an arrow function?', 'Select the best answer.', 1, 2, 1, 'P', now() - interval '1 day', 1, now() - interval '1 day', NULL, NULL, NULL),
    (2, 'Explain the SQL JOIN types',                       'Short answer / concept check.', 2, 3, 1, 'A', now() - interval '5 days', 1, now() - interval '4 days', 8, 'Good clarity, minor wording tweak needed', true),
    (3, 'JavaScript is dynamically typed - true or false?', 'True/false check.', 1, 1, 3, 'R', now() - interval '6 days', 1, now() - interval '5 days', 3, 'Too trivial, redundant with existing question', false);

-- ------------------------------------------------------------
-- ai_question_answer
-- ------------------------------------------------------------
INSERT INTO ai_question_answer (id, ai_question_id, is_correct, correct_position, status, answer_text, ai_question_answer_id, score, feedback) VALUES
    (1, 1, true,  NULL, 'P', 'The "this" value from the enclosing lexical scope', NULL, 5, 4),
    (2, 1, false, NULL, 'P', 'The global window object always',                    NULL, 2, 2),
    (3, 2, true,  NULL, 'A', 'INNER, LEFT, RIGHT, and FULL OUTER joins',           NULL, 5, 5),
    (4, 3, true,  NULL, 'R', 'True',                                              NULL, 3, 3),
    (5, 3, false, NULL, 'R', 'False',                                             NULL, 1, 1);

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
