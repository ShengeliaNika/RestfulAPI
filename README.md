# Pet Store API Test Automation Framework

API test automation framework for the [Swagger Pet Store API](https://petstore.swagger.io/v2), built with RestAssured and Cucumber on top of JUnit 5.

## Stack

- Java 21
- Maven
- RestAssured 5.5.2
- Cucumber 7.22.1 (JUnit 5 Platform Suite engine)
- JUnit 5.12.2
- Jackson Databind (POJO serialization)
- Lombok

## Project structure

```
src/test/java/com/qa/petstore/
  client/            RestAssured HTTP client for the Pet resource
  config/            Configuration loading (base URL)
  context/           Per-scenario state shared between step definitions
  hooks/             Cucumber lifecycle hooks (logging, cleanup)
  models/            Pet, Category and Tag POJOs
  runner/            JUnit 5 Cucumber suite entry point
  stepdefinitions/   Gherkin step implementations
  utils/             Test data generation

src/test/resources/
  features/          Gherkin feature files
  schemas/           JSON Schema definitions used to validate response bodies
  config.properties  Environment configuration
  cucumber.properties
```

## Running the tests

```
mvn test
```

Cucumber reports are generated under `target/cucumber-reports/` (`cucumber.html`, `cucumber.json`) after each run.

For a richer, navigable HTML report (per-scenario/step breakdown, pass/fail trends), run:

```
mvn verify
```

This generates an additional report via the `cucumber-reporting` Maven plugin at `target/cucumber-html-reports/overview-features.html`, built from the `cucumber.json` produced by the run.

## Test coverage

`pet_crud.feature` covers the full CRUD lifecycle of the `/pet` resource against the live Pet Store API:

| Operation | Positive case | Negative case |
|-----------|--------------|----------------|
| Create    | Create pet returns 200 with matching body | Malformed request body returns 400 |
| Read      | Get pet by id returns 200 with matching body; filter by status returns 200 | Get a non-existent pet id returns 404 |
| Update    | Update name/status returns 200 with updated body | Malformed request body returns 400 |
| Delete    | Delete pet returns 200 and a subsequent get returns 404 | Delete a non-existent pet id returns 404 |

Status codes, the `Content-Type` response header, and response body fields are all asserted directly against the live API response. Successful create/read/update/list responses are additionally validated against JSON Schemas (`src/test/resources/schemas/`) to catch structural/type regressions in the API response shape.

## Design notes

- Each scenario runs against a freshly generated pet id (`PetTestDataFactory`) to avoid collisions between test runs and to keep scenarios independent.
- `ScenarioContext` is instantiated per scenario via Cucumber's PicoContainer object factory and constructor-injected into both `Hooks` and `PetStepDefinitions`, avoiding static/shared mutable state.
- The `@After` hook deletes any pet created during a scenario, keeping the shared public API free of test data regardless of scenario outcome.
- `RestAssured.enableLoggingOfRequestAndResponseIfValidationFails` only logs request/response details when an assertion fails, keeping passing runs quiet.
