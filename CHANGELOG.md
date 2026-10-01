TP-21 TestCreateView fixes: a new question row can be removed with × right away, even when no
question is selected yet. Empty question rows are ignored in validation and not sent to backend,
so "Lisa testile vähemalt 1 küsimus" is shown only when no question is selected at all.
Removed "Tühista" button and made "Loo test" button wider.

AI question generation: ADMIN/HALDUR can create test questions with AI via the chat widget
(floating button in the bottom right corner). POST /api/ai-questions/generate creates a preview
(up to 5 questions at once, each with up to 5 answer options) and POST /api/ai-questions saves
a confirmed question to ai_question / ai_question_answer tables with status 'P'.
Uses Spring AI + Google GenAI (needs GOOGLE_GENAI_API_KEY). The hourly limit is temporarily removed.

TP-29 GET /api/question-bank?competenceId={id} -> brings all questions for QuestionBankView, regardless of
question status (both 'A' and 'I'), with competence name, question type name and active answer options
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