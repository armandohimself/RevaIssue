package com.abra.revaissue.E2E.steps;

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

  @Then("I should see the undefined snippet")
  public void i_should_see_the_undefined_snippet() {
      // Write code here that turns the phrase above into concrete actions
      //throw new io.cucumber.java.PendingException();
  }
  
}
