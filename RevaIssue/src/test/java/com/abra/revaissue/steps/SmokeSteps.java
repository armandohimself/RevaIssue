package com.abra.revaissue.steps;

import io.cucumber.java.en.Given;

public class SmokeSteps {
    @Given("I print hello")

    public void hello() {
        System.out.println("hello from cucumber!!!");
    }
}
