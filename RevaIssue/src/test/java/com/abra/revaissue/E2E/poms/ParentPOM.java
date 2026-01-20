package com.abra.revaissue.E2E.poms;

import org.openqa.selenium.WebDriver;

public class ParentPOM {
    protected WebDriver driver;

    public ParentPOM(WebDriver driver) {
        this.driver = driver;
    }
}
