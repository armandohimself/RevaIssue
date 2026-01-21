package com.abra.revaissue.E2E.steps;

import org.junit.jupiter.api.Assertions;

import com.abra.revaissue.E2E.poms.IssueCardCommentsPage;
import com.abra.revaissue.E2E.poms.IssuePage;
import com.abra.revaissue.E2E.poms.LoginPage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.abra.revaissue.E2E.steps.BaseSeleniumTest.driver;

public class CommentSteps {

    private LoginPage loginPage;
    private IssuePage issuePage;
    private IssueCardCommentsPage issueCardCommentsPage;

    // Background steps

    @Given("the user is logged in as {string} with password {string}")
    public void youAreLoggedInAs(String username, String password) {
        loginPage = new LoginPage(driver);
        loginPage.navigateTo("http://localhost:4200/login");
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();
    }

    @And("the user opens the issue card")
    public void youAreOnAnIssueCard() {
        issuePage = new IssuePage(driver);
        issuePage.openIssuePage();
        issuePage.openFirstIssueCard();
        issueCardCommentsPage = new IssueCardCommentsPage(driver);
    }

    // When steps

    @When("the user types {string} into the comment input")
    public void theUserTypesIntoTheCommentInput(String comment) {
        issueCardCommentsPage.enterComment(comment);
    }

    @And("the user submits the comment")
    public void theUserSubmitsTheComment() {
        issueCardCommentsPage.saveCurrentCommentCount();
        issueCardCommentsPage.submitComment();
    }

    @And("the user presses the Enter key to submit the comment")
    public void theUserPressesTheEnterKeyToSubmitTheComment() {
        issueCardCommentsPage.saveCurrentCommentCount();
        issueCardCommentsPage.pressEnterToSubmit();
    }

    // Then steps

    @Then("the comment table shows {string}")
    public void theCommentTableShows(String comment) {
        String lastComment = issueCardCommentsPage.getLastComment();
        Assertions.assertEquals(comment, lastComment);
    }

    @And("the comment author is {string}")
    public void theCommentAuthorIs(String author) {
        String lastAuthor = issueCardCommentsPage.getLastCommentAuthor();
        Assertions.assertEquals(author, lastAuthor);
    }

    @Then("the comment table shows {string} before {string}")
    public void theCommentTableShowsBefore(String before, String after) {
        String lastComment = issueCardCommentsPage.getLastComment();
        Assertions.assertEquals(after, lastComment);
        String secondLastComment = issueCardCommentsPage.getSecondLastComment();
        Assertions.assertEquals(before, secondLastComment);
    }

    @And("the comment author for both comments is {string}")
    public void theCommentAuthorForBothCommentsIs(String author) {
        String lastAuthor = issueCardCommentsPage.getLastCommentAuthor();
        Assertions.assertEquals(author, lastAuthor);
        String secondLastAuthor = issueCardCommentsPage.getSecondLastCommentAuthor();
        Assertions.assertEquals(author, secondLastAuthor);
    }

    @Then("the comment table does not show an empty comment")
    public void theCommentTableDoesNotShowAnEmptyComment() {
        String lastComment = issueCardCommentsPage.getEmptyLastComment();
        Assertions.assertNotEquals("", lastComment);
    }
}
