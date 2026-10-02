Dashboard ("Töölaud"): visible only to ADMIN and HALDUR. Shows four clickable number cards: tests assigned to me and
not yet completed (-> "Minu testid"), tests and questions added in the last 7 days (-> tests / question bank) and AI
questions waiting for review (-> AI questions, highlighted when > 0). GET /api/dashboard returns assignedTestCount,
recentTestCount, recentQuestionCount, pendingAiQuestionCount and recentDays (7, RECENT_DAYS in DashboardService);
other roles get HTTP 403 NO_PERMISSION_TO_VIEW_DASHBOARD, not logged in 401. After login a regular user (KASUTAJA)
goes straight to "Minu testid" and is redirected there from staff-only pages (NavigationService.navigateToStartPage).

TestCreateView fields: inputs, selects and labels use the same style as the question create form (QuestionCreateForm):
light grey rounded fields that turn white on focus and small bold labels. Info box content is centered, "Lisa küsimus"
is a dashed button with text and removing a question uses an X icon.

TestCreateView style: the form is now in the same card style as the test detail and test start pages. Competence,
level, pass percent and timer are shown as info boxes with icons (same style as the test detail page), title and short
description fields show a character counter inside the field (255 / 150, input limited with maxlength), all fields
have the same height and text size and there is more space between rows. "Loo test" button is bold.

Test detail view: "Vaata testi" on the tests page opens /tests/{testId} (TestDetailView, ADMIN/HALDUR only). The card
shows competence, title, level, short and long description and info boxes for question count, time limit, pass
percent and max score (singular/plural: "1 küsimus" / "2 küsimust"). Questions themselves are not shown. Inactive tests
get a "Mitteaktiivne" badge. Long titles (over 60 chars) are shown in normal case and a smaller font. An info icon in
the bottom right corner shows the author and creation date in a hover tooltip (shared .app-tooltip style in theme.css).

GET /api/tests/{testId} -> test details with questionCount, maxScore and createdBy (author's name from profile, email
if there is no profile). Only ADMIN and HALDUR: other roles get HTTP 403 NO_PERMISSION_TO_VIEW_TEST, not logged in 401,
unknown test 404 PRIMARY_KEY_NOT_FOUND.

Test cards (PreviewCard): titles longer than two lines are cut with "...".

Test data (3_import.sql): new tests "Vue.js Medior" (title 255 and short description 150 chars, the maximum lengths)
and "Giti algtaseme test". New competence "Vali-IT" (level Spetsialist) with the test "Backendi koodi head tavad":
12 questions in rAIn style about backend coding conventions, 20 min, pass 50%, assigned to the admin user.

TP-21 TestCreateView fixes: a new question row can be removed with × right away, even when no
question is selected yet. Empty question rows are ignored in validation and not sent to backend,
so "Lisa testile vähemalt 1 küsimus" is shown only when no question is selected at all.
Removed "Tühista" button and made "Loo test" button wider.

AI question generation: ADMIN/HALDUR can create test questions with AI via the chat widget
(floating button in the bottom right corner). POST /api/ai-questions/generate creates a preview
(up to 5 questions at once, each with up to 5 answer options) and POST /api/ai-questions saves
a confirmed question to ai_question / ai_question_answer tables with status 'P'.
Uses Spring AI + Google GenAI (needs GOOGLE_GENAI_API_KEY). The hourly limit is temporarily removed.

AI chat fix: the chat kept only the user's first message and the latest one, so when the user changed the
competence, level or type, the AI got conflicting info and repeated the same clarifying question. Now the chat
sends the conversation history (last 10 messages incl. AI clarifying questions and generated question summaries)
in POST /api/ai-questions/generate as previousMessages, and the AI rules say that the latest message has priority
and earlier messages are only used to fill in missing details. The same clarifying question is not repeated.

POST /api/ai-questions/{aiQuestionId}/review -> AI question review with score 1-5, feedback (max 255) and decision
(approved). Fills ai_question score, feedback and is_good. Approved: status 'A' and the question with its answers is
copied to question / question_answer (status 'A', score 10, TRUE_FALSE 5, created_by = AI question author).
Rejected: status 'R'. Already reviewed question returns HTTP 400 AI_QUESTION_ALREADY_REVIEWED. On the "AI küsimused"
page "Vaata üle" in the "···" menu opens a review modal with star rating (StarRating component), feedback and
"Kinnita" / "Lükka tagasi" buttons; reviewed cards show the rating and feedback. AI question scores in 3_import.sql
are now on the 1-5 scale.

GET /api/ai-questions?status={P|A|R}&competenceId={id} -> AI questions with answer options, newest first, both
parameters optional (AiQuestionBankService). The "AI küsimused" page lists them with the same QuestionBankCard,
filtered by status (default "Ootab ülevaatust"), and the tab shows the number of questions waiting for review.
Answer option view moved to QuestionAnswerOption component.

TP-29 Question bank tabs: QuestionBankView and the new AiQuestionBankView (http://localhost:8081/questions/ai) have
tabs "Kõik küsimused" and "AI küsimused" (QuestionBankTabs component). Approve/reject actions on the AI
questions page will be added later. "Küsimuste pank" in the side menu stays active
on both pages.

Test data (3_import.sql): levels renamed to Juunior / Medior / Seenior (were Algaja / Kesktase / Edasijõudnu),
competence "Suhtlemine" renamed to "Kommunikatsioon". Kommunikatsioon has its own levels Algaja / Edasijõudnu /
Spetsialist (level ids 4-6) and got questions for Edasijõudnu (MULTIPLE_CHOICE) and Spetsialist (TRUE_FALSE). Added competences Vue.js, Spring Boot and Git, each with
all 3 levels and 3 questions (Juunior: SINGLE_CHOICE, Medior: MULTIPLE_CHOICE, Seenior: TRUE_FALSE).

PUT /api/questions/{questionId} -> updates question title (max 100), description (max 1000) and status ('A' or 'I').
Answer options can't be changed, so existing test results stay correct. Empty/too long fields or another status
return HTTP 400 INCORRECT_INPUT, unknown questionId returns HTTP 404 PRIMARY_KEY_NOT_FOUND. In QuestionBankView
"Muuda" in the "···" menu opens a modal with title, description and status fields, after saving a success
message is shown and the list is reloaded.

DELETE /api/questions/{questionId} -> "deletes" a question: the question stays in the database, its status is set
to 'I' (Mitteaktiivne) and updated_at is updated, so existing tests and results stay intact. Unknown questionId
returns HTTP 404 PRIMARY_KEY_NOT_FOUND. In QuestionBankView the "···" menu of a question card has "Muuda" and
"Kustuta". "Kustuta" opens a confirmation dialog, after deleting a success message is shown and
the list is reloaded. "Kustuta" is hidden for questions that are already inactive.

TP-29 QuestionBankView http://localhost:8081/questions route view. Questions are shown as collapsible cards
(QuestionBankCard component): header has the title, competence level, competence and status badges, the arrow opens
the card and shows question type, score, description and answer options in two columns, correct answers are
highlighted in green. On a narrow card the badges move under the title and answers are in one column.
"Küsimuste pank" link is added as the last item of the side menu. Competence dropdown filters questions by competence, "Lisa uus küsimus" button navigates to
/questions/new (route not created yet). In Status.js the inactive status is now 'I' (Mitteaktiivne), as in backend.

TP-29 GET /api/question-bank?competenceId={id} -> brings all questions for QuestionBankView, regardless of
question status (both 'A' and 'I'), with competence name, competence level name, score, question type name and active answer options
(answerText, correctChoice). competenceId is optional - without it questions of all competences are returned,
unknown competenceId returns an empty list. Questions are ordered by questionId.


TP-24 http://localhost:8081/tests route view and GET /api/me/my-test endpoint. Using same PreviewCard component,
User must be logged in to see their own tests, Completed and Open tests are both shown with a different status badge,
The order is open status tests that will close the soonest to completed status tests. If the test is open the user will
see only the button 'Soorita test', if it's completed then they will see 'Vaata tulemusi'


TP-21 TestCreateView form for creating a new test. Route: http://localhost:8081/tests/new                                                                                                                                          
Admin/manager fills in test name, short description, description, competence, competence level,                                                                                                                                    
pass percent, score rounding (up/down) and optional timer, then selects questions. Competence                                                                                                                                      
levels are loaded after competence is selected and questions after level is selected. Question                                                                                                                                     
rows can be added/removed and the same question can't be selected twice. Frontend validation                                                                                                                                       
shows an error for each empty field;

TP-21 POST /api/tests -> creates a new test. Only users with role ADMIN or HALDUR can create                                                                                                                                       
a test, others get HTTP 403 with errorCode NO_PERMISSION. Saves the test row (status 'A',                                                                                                                                          
competence taken from the selected competence level) and a test_question row for every selected                                                                                                                                    
question with its position (1, 2, 3...). Runs in one transaction, so if a question is not found                                                                                                                                    
nothing is saved. Returns HTTP 400 INCORRECT_INPUT for missing required fields and HTTP 404                                                                                                                                        
PRIMARY_KEY_NOT_FOUND for unknown userId, competenceLevelId or questionId.

TP-21 GET /api/questions?competenceLevelId={id} -> brings active questions of the selected                                                                                                                                         
competence level for TestCreateView.

TP-23 Update GET /api/tests/{testId}/start-info -> user can't open the /start page without a valid                                                                                                                                                                
(open) test assignment. Returns HTTP 403 with errorCode NO_TEST_ASSIGNMENT_FOR_THIS_USER, so                                                                                                                                                                      
TestAttemptView and TestStartView show the same kind of "Test not found" page.

TP-23 Add POST /api/tests/{testId}/complete -> triggered when user clicks "Finish test" in the UI.                                                                                                                                                                
Selected answerIds per questionId are sent to backend, which checks correctness (unanswered                                                                                                                                                                       
question = wrong), calculates user score and max score (sum of all question scores) and sets                                                                                                                                                                      
status P/F based on test pass_percent (and round_score_up). Saves a result row and sets                                                                                                                                                                           
user_test status to 'C' - after that /start-info and /attempt return 403 for this test.                                                                                                                                                                           
Simplified version: test_question_result / test_question_answer rows are not saved yet.                                                                                                                                                                           
Result is not yet shown in the UI - will be added in next step (separate GET endpoint).

TP-20  PreviewCard component creation for three separate views. Used currently only on route: http://localhost:8081/tests
created CompetenceView.vue to test if the card element shows up. Added Status.js file to translate database status values into words.


TP-22 upgrade navbar styling and add accountIcon to the right. 
Route:  http://localhost:8081/tests/1/start <- load data from backend.
Store userId in backend session. getUserId method.


TP-22 Connect TestAttemptView with the actual test data. User test assignment id is yet hardcoded. Route: http://localhost:8081/tests/1/attempt 

TP-22 GET "/user-tests/{userTestId}/attempt" - gets all the necessary info to start taking a test.

TP - 21 GET /api/competence-levels to bring active competence levels for TestCreateView

TP - 21 GET /api/competences endpoint to bring active competences for TestCreateView

TP - 18 GET /api/tests endpoint to bring necessary data from database for /tests view.

TP- 8 LoginView backend + success and error response handling.

TP-8 Login frontend ja backend service WIP
POST /api/login endpoint 