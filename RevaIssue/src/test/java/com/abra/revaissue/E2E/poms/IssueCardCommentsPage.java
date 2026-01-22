package com.abra.revaissue.E2E.poms;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class IssueCardCommentsPage extends ParentPOM {

    private final By commentInputLocator = By.tagName("textarea");
    private final By sendButtonLocator = By.cssSelector(".new-comment > button");
    private final By commentRowLocator = By.cssSelector(".comment-item");
    private int previousCommentCount;

    public IssueCardCommentsPage(WebDriver driver) {
        super(driver);
    }

    public void saveCurrentCommentCount() {
        previousCommentCount = driver.findElements(commentRowLocator).size();
    }

    /**
     * Enter text into the comment input box.
     * After this, the submit button should become enabled.
     */
    public void enterComment(String comment) {
        WebElement input = waitForElement(commentInputLocator);
        moveToElement(input);
        input.clear();
        input.sendKeys(comment);
    }

    /**
     * Submit the comment by clicking the button.
     * Waits for the button to become enabled/clickable after typing.
     */
    public void submitComment() {
        WebElement button = waitForElement(sendButtonLocator);
        moveToElement(button);
        button.click();
    }

    /** Press Enter in the comment input to submit */
    public void pressEnterToSubmit() {
        WebElement input = waitForElement(commentInputLocator);
        moveToElement(input);
        input.sendKeys(Keys.ENTER);
    }

    /** Get the newest (last) comment */
    public String getLastComment() {
        List<WebElement> updatedComments = getUpdatedComments();
        WebElement lastComment = updatedComments.get(updatedComments.size() - 1);
        WebElement lastCommentMessage = lastComment.findElement(By.cssSelector(".comment-message"));
        moveToElement(lastCommentMessage);

        return lastCommentMessage.getText();
    }

    /** Get the author of the newest comment */
    public String getLastCommentAuthor() {
        List<WebElement> updatedComments = getUpdatedComments();
        WebElement lastComment = updatedComments.get(updatedComments.size() - 1);
        moveToElement(lastComment);

        return lastComment.findElement(By.className("username")).getText();
    }

    /** Get the second newest (second last) comment */
    public String getSecondLastComment() {
        List<WebElement> updatedComments = getUpdatedComments();
        if (updatedComments.size() < 2)
            return null;
        WebElement secondLastComment = updatedComments.get(updatedComments.size() - 2);
        WebElement secondLastCommentMessage = secondLastComment.findElement(By.cssSelector(".comment-message"));
        moveToElement(secondLastCommentMessage);

        return secondLastCommentMessage.getText();
    }

    /** Get the author of the second newest comment */
    public String getSecondLastCommentAuthor() {
        List<WebElement> updatedComments = getUpdatedComments();
        if (updatedComments.size() < 2)
            return null;
        WebElement secondLastComment = updatedComments.get(updatedComments.size() - 2);
        moveToElement(secondLastComment);

        return secondLastComment.findElement(By.className("username")).getText();
    }

    /** Scroll to the last comment */
    public void scrollToLastComment() {
        List<WebElement> comments = driver.findElements(commentRowLocator);
        if (!comments.isEmpty()) {
            moveToElement(comments.get(comments.size() - 1));
        }
    }

    public String getEmptyLastComment() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> {
            List<WebElement> webComments = driver.findElements(commentRowLocator);
            return webComments.size() == previousCommentCount;
        });

        List<WebElement> updatedComments = driver.findElements(commentRowLocator);
        WebElement lastComment = updatedComments.get(updatedComments.size() - 1);
        WebElement lastCommentMessage = lastComment.findElement(By.cssSelector(".comment-message"));
        moveToElement(lastCommentMessage);

        return lastCommentMessage.getText();
    }

    public List<WebElement> getUpdatedComments() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> {
            List<WebElement> webComments = driver.findElements(commentRowLocator);
            return webComments.size() > previousCommentCount;
        });

        return driver.findElements(commentRowLocator);
    }

}
