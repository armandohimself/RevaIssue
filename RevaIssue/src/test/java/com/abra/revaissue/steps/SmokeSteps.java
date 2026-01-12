package com.abra.revaissue.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class SmokeSteps {

  @Given("cucumber is configured")
  public void cucumberIsConfigured() {
    // no-op: wiring check
  }

  @When("the smoke scenario runs")
  public void theSmokeScenarioRuns() {
    // no-op: execution check
  }

  @Then("the scenario should pass")
  public void theScenarioShouldPass() {
    // no-op: assertion not needed for smoke test
  }
}
