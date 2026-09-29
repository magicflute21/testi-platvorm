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