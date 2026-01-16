# Testing Feature Files & Dev Breakdown

> [!NOTE] We will be working with 3 hypothetical files to make features work.
>
> - [`CucumberRunnerTest.java`](#cucumberrunnertestjava)
> - [`adminLogin.feature`](#adminloginfeature)
> - [`AdminLoginSteps.java`](#adminloginstepsjava)

## The Breakdown

Before we get into the steps here is a breakdown of the file/folder structure you will want to keep in mind.

### What Each Folder Is For

```bash
src/test/java/com/abra/revaissue
├── runner                          # Test Launcher for Cucumber | JUnit Platform Suite + Cucumber engine | Hooks to: resources/features + step packages (glue)
│   └── CucumberRunnerTest.java
├── unit                            # Unit tests | JUnit + Mockito (+ AssertJ) | Hooks to: service/util/mapper classes in src/main/java
│   └── ...
├── integrations
│   └── api                         # API integration tests | RestAssured + JUnit (+ Spring Boot running) | Hooks to: controllers → services → repos
│       └── AdminLoginAPITest.java
└── E2E                             # Full user journey tests
    ├── fixtures                    # Reusable test data | Plain Java builders/data, maybe Jackson helpers | Hooks to: E2E/steps, E2E/poms, integrations/api (optional reuse)
    │   ├── Users.java
    │   └── Projects.java
    ├── poms                        # Page Objects: screens/components | Selenium WebDriver (+ waits/helpers) | Hooks to: E2E/steps
    │   ├── ParentPOM.java
    │   └── LoginPage.java
    └── steps                       # Step definitions that drive Selenium | Cucumber + Selenium (+ assertions) | Hooks to: resources/features + E2E/poms + BaseSeleniumTest
        ├── BaseSeleniumTest.java
        ├── AdminLoginSteps.java
        └── SmokeSteps.java

src/test/resources/features         # Feature files | Gherkin syntax | Hooks to: runner + step definitions
└── adminLogin.feature
```

### What Each File Does

```bash
src/test/java/com/abra/revaissue
├── runner
│   └── CucumberRunnerTest.java         # test launcher for Cucumber
├── unit
│   └── ...                             # JUnit + Mockito tests (service/util/mappers)
├── integrations
│   └── api
│       └── AdminLoginAPITest.java      # RestAssured tests hitting controllers over HTTP
└── E2E
    ├── fixtures
    │   ├── Users.java                  # test data builders (admin creds, etc.)
    │   └── Projects.java               # sample payloads / reusable test data
    ├── poms                             
    │   ├── ParentPOM.java              # shared page utilities (waits, click helpers)
    │   └── LoginPage.java              # page objects (UI screens)
    └── steps
        ├── BaseSeleniumTest.java       # WebDriver setup/teardown
        ├── AdminLoginSteps.java        # step defs (UI or shared)
        └── SmokeSteps.java

src/test/resources/features
└── adminLogin.feature
```

---

### `resources/features/`

- **This is:** where the .feature files live (your readable scenarios).
- **HOOKS to:** Cucumber Runner → Step Definitions.
- **NOT:** Java tests, not Spring config, not production code.

---

### `runner/`

- **This is:** where your CucumberRunnerTest.java lives.
- **Hooks to:** feature files + step packages.
- **NOT:** where you write assertions about business logic. It’s the “launcher.”

Now that we understand `where` things go, let's break down `what` each thing is and what each thing `does` and `how` each connects to one another.

---

### `unit/`

- **This is:** fast tests for one class at a time (usually service/util/mapper). (Unit tests)
- **Tools used:** JUnit + Mockito
- **Hooks to:** your service/, util/, and mappers without starting the Spring app.
- **NOT:** controllers, NOT real database calls, NOT real HTTP.

---

### `integrations/api/`

- **This is:** tests that hit your backend over HTTP (Integration tests).
- **Tools used:** RestAssured + JUnit (often with Spring Boot running)
- **Hooks to:** `controller` → `service` → `repository` (more of the real stack).
- **NOT:** a browser test, NOT mocking everything.

Typical target from your main app:

- Controllers: UserController, ProjectController, IssueController, etc.
- Auth endpoints (login/token)
- Role-based access rules (403 vs 200)

### `E2E/ (End-to-End)`

- **This is:** A test that checks a full user journey from start to finish from the `outside: UI → API → database → back to UI`.
- **Tools used:** `Cucumber` (`Gherkin` feature files + step definitions) + `Selenium` + `WebDriver` + Optional helpers: `waits`, `assertions` (`AssertJ/JUnit assertions`), `test data (fixtures)`
- **Hooks to:** `src/test/resources/features/*.feature` → `Step definitions (E2E/steps)` → `Page Objects (E2E/poms)` → `WebDriver` setup (`BaseSeleniumTest`). And indirectly the `Spring Boot` backend because the UI calls your API.
- **NOT:** Unit tests (that’s unit/) or API-only tests (that’s integrations/api/) or the place to “`assert HTTP 200`” as the main goal—E2E is usually “`what the user sees/experiences`”

---

### `E2E/poms/`

- **This is:** `Page Object Models (POMs)` — classes that represent pages/components.
- **Hooks to:** Selenium steps (so steps stay clean).
- **NOT:** test scenarios, NOT backend calls.

> [!TIP] Concrete Examples
>
> **`LoginPage.java`** object that knows how to type username/password and click Login.
>
> **`ParentPOM.java`** shared browser helper methods all pages can use.

---

### `E2E/steps/`

- **This is:** Cucumber step definitions that drive Selenium (browser automation).
- **Hooks to:** `feature files (Gherkin)` + `POMs` + `BaseSeleniumTest`.
- **NOT:** unit tests, NOT API tests.

---

### `E2E/fixtures`

- **This is:** Reusable test data and builders (users, projects, payloads) AKA `Test Data`.
- **Hooks to:** E2E/steps, E2E/poms, integrations/api (optional reuse).
- **NOT:** assertions, NOT WebDriver setup, NOT real HTTP calls by itself

---

### `E2E/steps/BaseSeleniumTest.java`

- **This is:** shared setup/teardown for `WebDriver` (open browser, close browser).
- **Hooks to:** Selenium steps.
- **NOT:** where you define business behavior—just plumbing.

> [!NOTE] Key discrimination:
> Selenium doesn’t directly “test the backend.”
>
> It tests the system from the user side (UI), which indirectly hits the backend if the UI calls your APIs.

## So How Are You (the dev) Suppose To Think About This?

You get to choose three lanes of speed + purpose when writing tests

```bash
FAST (Unit) ──▶ JUnit + Mockito ──▶ test one class (usually service/util)
  - Doesn't need Spring running
  - Doesn't use Cucumber or Selenium

MEDIUM (Integration/API) ──▶ JUnit + RestAssured ──▶ test endpoints + real wiring
  - Usually needs Spring running (or Spring test context)
  - Doesn't use a browser

SLOW (E2E) ──▶ Cucumber + Selenium (+ POM) ──▶ test full user flow
  - Needs UI + backend running
  - Can validate “login works end-to-end”
```

### The “Hook” Truth Table

- Cucumber hooks to feature files + step definitions (API steps or UI steps). ✅
- RestAssured hooks to the backend via HTTP. ✅
- Selenium hooks to the UI (browser), and indirectly to the backend. ✅
- Mockito hooks to unit tests only (fake dependencies). ✅
- Mockito does NOT hook to Selenium or RestAssured flows (different purpose). ❌

### How this maps to your Spring Boot code (what to test where)

Using your main folders:

- controller/ → best covered by integrations/api (RestAssured)
- service/ → best covered by unit (Mockito) and maybe some integration tests
- repository/ → can be integration-style (real DB / test DB)
- util/ and mappers/DTO mapping → unit tests
- exception/GlobalExceptionHandler → integrations/api (verify correct status + message)

### Important Definitions

---

| Jargon | Definition | Simple Analogy | Why It Matters |
| :------ | :----------: | :--------------: | ---------: |
| `Feature File (Gherkin)` | A plain-English checklist of behavior. | A movie script. | |
| `Step Definition (Glue code)` | The Java “translator” that makes script lines actually do things. | An actor + stage crew turning script into action | |
| `Test Launcher` | The file that presses play and runs a set of tests. | The stage manager calling “Places!” and starting the show. | |
| `Runner (JUnit + Cucumber)` | AKA the `test launcher/runner`, the "play" button | The theater projector operator | |
| `RestAssured` | A Java tool to send HTTP requests and check responses. | POSTman, but automated. | |
| `Mockito` | Makes fake versions of dependencies so you can test one class in isolation | testing a car’s radio by plugging it into a bench power supply instead of installing the whole car. | |
| `Selenium` | A tool that controls a real browser like a user would. | The actor on stage actually clicking props, opening doors, moving through the set. | Validates the user experience, not just the backend response. |
| `WebDriver` | The part of Selenium that sends the actual commands to the browser. | The director’s headset telling the actor exactly where to move and what to do. | This is the API your code talks to when it says “click”, “type”, “wait”. |
| `BaseSeleniumTest` | A shared setup class that starts the browser before tests and closes it after. (`Base Test` / `Test Fixture` setup/teardown) | The crew that opens the theater, sets lights, checks microphones, and closes everything after. | Prevents copy/paste and keeps browser setup consistent. |
| `E2E` | A test that checks a full user journey from start to finish. | A dress rehearsal where you run the whole scene with lights, costumes, and cues. | It catches “it works in pieces but breaks when connected” problems. |
| `Fixtures (Test Data)` | Reusable test data and builders (users, projects, payloads). | The prop room: approved props you can pull for any scene. | Stops you from hardcoding strings everywhere and makes tests consistent. |
| `ParentPOM.java` | Shared browser helper methods all pages can use. | The standard stage directions every actor follows (“wait for spotlight”, “enter from stage left”). | Centralizes waits/click helpers so every page doesn’t reinvent the wheel. |
| | | | |

---

### Flow Map (What Connects to What)

---

```bash
(1) adminLogin.feature  ──describes behavior──▶
(2) CucumberRunnerTest (JUnit) ──runs features──▶
(3) AdminLoginSteps.java ──executes steps──▶
(4) RestAssured ──HTTP call──▶  Spring Boot Backend (/controller → /service → /repo)
                         ◀─assert response───
```

This is the "smallest complete chain" when you are first starting out.

## Feature File

### Syntax Breakdown

---

- `Feature`    -> describes what the app should do.
- `Rule`       -> biz rule or constraint that MUST always be true.
- `Scenario`   -> Example of behavior / use case.
- `Given`      -> Setup / starting state (what must already be true) | Analogy: stage is set + actors are in place.
- `When`       -> The action (what the actor does) | Analogy: actor performs an action.
- `Then`       -> The main outcome (what the audience must see) | Analogy: what the audience sees must happen.
- `And / But`  -> extra details in the same “type” of line you’re already in | Analogy: “also notice…” additional audience-visible facts.

## Flow of Commands While Testing

| Step # | Dev Actions | Terminal Command | What It Does |
| :----- | :-----------: | :----------------: | :------------: |
| Step 1 | Create `adminLogin.feature` with 1 scenario & 2-3 steps | `./gradlew cucumberTest --info` | Fails with undefined steps and prints a snippet methods in the terminal you can paste. |
| Step 2 | Paste the snippet into `AdminLoginSteps.java` | `./gradlew cucumberTest --info` | Now it fails inside your step methods (often “Pending” or throws by default). This is good: you’ve moved from “`missing glue`” → “`glue is executing`”. |
| Step 3 | Make the step hit the backend (API lane first), then implement steps using RestAssured to call your login endpoint and capture the token. | `./gradlew test --tests "*AdminLoginAPITest*"` | First failure is usually wrong URL/port/path → fix until you get the expected status. Then assert token exists. |
| Step 4 | Use the token to call a protected endpoint in the same test (or next scenario), send `Authorization: Bearer <token>` to a protected endpoint (like create project). | `./gradlew test --tests "*AdminLoginAPITest*"` | Verify `without a token`, you expect -> `401/403`. `With a token`, you expect -> `200` |
| Step 5 | Optional: E2E UI scene (Selenium). Write UI scenario: “Admin creates project and sees new card + toast.” | `./gradlew cucumberTest --info` | Verify the browser launches, clicks happen, assertions check UI outcomes. |

### Each Command Breakdown

| Command | Test Layer |
| :------- | :----------: |
| `./gradlew test` | Unit lane (JUnit + Mockito) -> Targets all unit tests |
| `./gradlew test --tests "*JunitSmokeTest*"` | Unit lane (JUnit + Mockito) -> Targets one class to tests. |
| `./gradlew test --tests "*APITest*"` | Integration/API lane (RestAssured + JUnit) - If they’re normal JUnit tests under src/test/java, run this command. |
| `./gradlew cucumberTest --info` | Cucumber lane (Feature files + Steps) |
| `./gradlew cucumberTest --info` | E2E lane (Cucumber + Selenium) -> Often also run via the same cucumber task (depends how your Gradle is set up, could be something like `e2eTest`). |
| | |
| | |

## Concrete Example

Background: We need to authenticate a user who uses a JWT token. They land on a login page, enter some valid credentials and are taken to a dashboard where they can take further actions.

> [!IMPORTANT] How should you reuse login in a clean way for other tests because maybe you have other users or role based permissions?
>
> - **`Option 1`:** Background (repeatable setup for each scenario in that feature)
> - **`Option 2`:** A reusable step like Given the admin is authenticated
> - **`Option 3`:** A helper method inside steps (not visible in Gherkin), so the script stays clean

Remember this because we are going to go through one, or maybe all these strategies, who knows.

> [!CAUTION] Senior-dev nuance:
>
> If you find yourself writing “`OR`” inside one scenario, that’s usually a smell.
>
> Prefer `two scenarios` or a `Scenario Outline` or a `Data table(s)`.

### The “Login + JWT” test slice (only the files we need)

Minimal test tree (just this slice)

```bash
src/test/resources/features/
└── adminLogin.feature

src/test/java/com/abra/revaissue/
├── runner/
│   └── CucumberRunnerTest.java
├── E2E/
│   ├── fixtures/
│   │   └── TestUsers.java
│   └── steps/
│       └── AdminLoginSteps.java
└── integrations/api/
    └── AdminLoginAPITest.java
```

## Phase Checkpoints (What “Success” Looks Like At Each Phase)

✅ Phase 1 success
> `./gradlew cucumberTest --info` prints undefined step snippets.

✅ Phase 2 success
> `./gradlew cucumberTest --info` actually enters your step methods (even if it fails on HTTP).

✅ Phase 3 success
> Runner works without “no tests discovered” / “engine not found”.

✅ Phase 4 success
> `./gradlew test --tests "*AdminLoginAPITest*"` passes (or fails with meaningful HTTP body).

---

### Phase 0 - One-time “Preflight”: Know Your API Base URL

User controller:

- POST /api/users/login
- GET /api/users/me

But we need the host+port (example: `http://localhost:8080` or `http://localhost:8081`).

So we’ll make tests read it from an environment variable or Gradle system property:

- env var: REVAISSUE_BASE_URL
- or JVM prop: -DbaseUrl=...

> This is like writing the venue address on the script so the cast goes to the right building.

### Phase 1 - Write the Feature File (script) & Run It To Get Step Snippets

#### 1A) Create: src/test/resources/features/adminLogin.feature

```feature
Feature: Admin Authentication (API)

    # Scenario 1: Login gives a token
    Scenario: Admin can login and receives a token
        Given  the API base url is configured
        When   the admin logs in with username "admin" and password "password"
        Then   the response status should be 200
        And    the response should contain a token

    # Scenario 2: Token works for /me
    Scenario: Admin token allows access to /me
        Given   the API base url is configured
        When    the admin logs in with username "admin" and password "password"
        Then    the response should contain a token
        When    the client calls "/api/users/me" with that token
        Then    the response status should be 200
        And     the current user name should be "admin"
```

Why the THEN/AND combo here is “correct”

- Then = the main audience-visible result (status, token exists)
- And = extra checks in the same “Then lane”

#### 1B) Run to intentionally fail and generate missing step code

```bash
./gradlew cucumberTest --info
```

What you should see (success criteria for this phase)

- The run fails
- BUT prints “Undefined step” snippets you can copy into your step file. You can get these from 1 of 2 ways:
  - You copy what you see in the terminal.
  - You can click the link where it takes you to the Cucumber dashboard and click on the test(s) that failed and see a nice output of your step boilerplate.
- **Theater Analogy:** That means Cucumber runner found your feature file, and is asking for actors to perform the lines.

### Phase 2 — Add Step Definitions (actors) so the script can be performed

#### 2A) Create: src/test/java/com/abra/revaissue/E2E/steps/AdminLoginSteps.java

> [!IMPORTANT] Remember that your `Step Definitions` drive Selenium
>
> That means we are using Cucumber + Selenium (+ assertions)
>
> We will hook into resources/features + E2E/poms + BaseSeleniumTest

```java
    // Admin can login and receives a token
    public void the_api_base_url_is_configured() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("the admin logs in with username {string} and password {string}")
    public void the_admin_logs_in_with_username_and_password(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("the response should contain a token")
    public void the_response_should_contain_a_token() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    // Admin token allows access to /me
    @Given("the API base url is configured")
    public void the_api_base_url_is_configured() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("the admin logs in with username {string} and password {string}")
    public void the_admin_logs_in_with_username_and_password(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("the response should contain a token")
    public void the_response_should_contain_a_token() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("the client calls {string} with that token")
    public void the_client_calls_with_that_token(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("the current user name should be admin")
    public void the_current_user_name_should_be_admin() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
```

This is what the failed test generated for me and you will see there was a lot of duplicate methods. 

```java
package com.abra.revaissue.E2E.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class AdminLoginSteps {
    
    private String baseUrl;
    private Response lastResponse;
    private String token;
    
    @Given("the API base url is configured")
    public void the_api_base_url_is_configured() {
        // Start writing code to make these tests pass, here is my implementation below

        // 1) Prefer DatabaseURL
        String fromProp = System.getProperty("baseUrl");

        // 2) Then environment variable
        String fromEnv = System.getenv("REVAISSUE_BASE_URL");

        // 3) Default API Base URL
        baseUrl = (fromProp != null && !fromProp.isBlank())
            ? fromProp
            : (fromEnv != null && !fromEnv.isBlank())
                ? fromEnv
                : "http://localhost:8081";

        RestAssured.baseURI = baseUrl;
    }
    
    @When("the admin logs in with username {string} and password {string}")
    public void the_admin_logs_in_with_username_and_password(String userName, String password) {
        lastResponse = 
            RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"userName\":\"" + userName + "\",\"password\":\"" + password + "\"}")
            .when() 
                .post("api/users/login")
            .then()
                .extract().response();
    }
    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer expectedStatus) {
        assertNotNull(lastResponse, "No response captured yet. Did a When step run?");
        assertEquals(expectedStatus.intValue(), lastResponse.statusCode(), () ->
            "Expected HTTP " + expectedStatus + " but got " + lastResponse.statusCode()
            + "\nBody: " + lastResponse.asString()
        );
    }

    @Then("the response should contain a token")
    public void the_response_should_contain_a_token() {
        assertNotNull(lastResponse, "No response captured yet. Did login run?");
        token = lastResponse.jsonPath().getString("token");

        assertNotNull(token,  "token was null. Body: " + lastResponse.asString());
        assertFalse(token.isBlank(), "token was blank. Body: " + lastResponse.asString());
    }


    @When("the client calls {string} with that token")
    public void the_client_calls_with_that_token(String path) {
        assertNotNull(token, "No token captured. Did you run the 'token' Then step?");

        String authHeader = "Bearer " + token;

        lastResponse = 
            RestAssured.given()
                .header("Authorization", authHeader) // RequestSpecification
            .when() 
                .get(path)
            .then()
                .extract().response();
    }

    @Then("the current user name should be {string}")
    public void the_current_user_name_should_be_admin(String expectedUserName) {
        assertNotNull(lastResponse, "No response captured yet.");

        String actualUserName = lastResponse.jsonPath().getString("userName");

        // If your JSON uses "userName" (likely) this is correct.

        assertEquals(expectedUserName, actualUserName, () -> 
            "Expected userName=" + expectedUserName + " but got " + actualUserName 
            + "\nBody: " + lastResponse.asString()
        );
    }
}
```













```bash
Scenario: Admin can login and receives a token                           # classpath:features/adminLogin.feature:4
      ✔ Given the API base url is configured                                 # com.abra.revaissue.E2E.steps.AdminLoginSteps.the_api_base_url_is_configured()
      ✘ When the admin logs in with username "admin" and password "password" # com.abra.revaissue.E2E.steps.AdminLoginSteps.the_admin_logs_in_with_username_and_password(java.lang.String,java.lang.String)
            java.net.ConnectException: Connection refused
... lots of errors ...
↷ Then the response should contain a token                             # com.abra.revaissue.E2E.steps.AdminLoginSteps.the_response_should_contain_a_token()
      ↷ When the client calls "/api/users/me" with that token                # com.abra.revaissue.E2E.steps.AdminLoginSteps.the_client_calls_with_that_token(java.lang.String)
      ↷ Then the response status should be 200                               # com.abra.revaissue.E2E.steps.AdminLoginSteps.the_response_status_should_be(java.lang.Integer)
      ↷ And the current user name should be "admin"                          # com.abra.revaissue.E2E.steps.AdminLoginSteps.the_current_user_name_should_be_admin(java.lang.String)

CucumberRunnerTest > Cucumber > Admin Authentication (API) > com.abra.revaissue.runner.CucumberRunnerTest.Admin token allows access to /me FAILED
```










```bash
Scenario Outline: Login outcome depends on credentials
    Given the backend is running
    When the user logs in with "<username>" and "<password>"
    Then the status should be <status>

Examples:
  | username | password  | status |
  | admin    | password  | 200    |
  | admin    | wrong     | 401    |
  ```
