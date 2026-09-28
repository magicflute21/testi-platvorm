TP-20  PreviewCard component creation for three separate views. Used currently only on route: http://localhost:8081/tests
created CompetenceView.vue to test if the card element shows up. Added Status.js file to translate database status values into words.


TP-22 upgrade navbar styling and add accountIcon to the right. 
Route:  http://localhost:8081/tests/1/start <- load data from backend.
Store userId in backend session. getUserId method.


TP-22 Connect TestAttemptView with the actual test data. User test assignment id is yet hardcoded. Route: http://localhost:8081/tests/1/attempt 
TP-22 GET "/user-tests/{userTestId}/attempt" - gets all the necessary info to start taking a test.

TP - 18 GET /api/tests endpoint to bring necessary data from database for /tests view.

TP- 8 LoginView backend + success and error response handling.

TP-8 Login frontend ja backend service WIP
POST /api/login endpoint 