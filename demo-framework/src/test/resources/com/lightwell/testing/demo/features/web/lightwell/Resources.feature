@resources
Feature: Services and Solutions

  Background: Navigate to the page
    Given I am using the "Resources" view
    And I create the browser chosen at the command line
    And I navigate to the page

  Scenario: Check that resource button is enabled
    Then I check that "resource button" is not disabled

  Scenario: Check that clicking the resource button takes me to the correct page
    And I click on "resource button"
    Then I check that I am on the "resources page"

  Scenario: check that resource drop down menu is enabled
    Then I check that "resource drop down menu" is not disabled

  Scenario: Check that blog link is enabled
    Then I check that "blog link" is not disabled

  Scenario: Check that clicking the blog link takes me to the correct page
    And I click on "blog link"
    Then I check that I am on the "blog link url part"

  Scenario: Check that resource library link is enabled
    Then I check that "resource library link" is not disabled

  Scenario: Check that clicking the resource library link takes me to the correct page
    And I click on "resource library link"
    Then I check that I am on the "resource library url part"

  Scenario: Check that success stories link is enabled
    Then I check that "success stories link" is not disabled

  Scenario: Check that clicking the success stories link takes me to the correct page
    And I click on "success stories link"
    Then I check that I am on the "success stories url part"

  Scenario: Check that webinar & events link is enabled
    Then I check that "webinar & events link" is not disabled

  Scenario: Check that clicking the webinar & events link takes me to the correct page
    And I click on "webinar & events link"
    Then I check that I am on the "webinar & events url part"

  Scenario: Check that lightwell brochures link is enabled
    Then I check that "lightwell brochures link" is not disabled

  Scenario: Check that clicking the lightwell brochures link takes me to the correct page
    And I click on "lightwell brochures link"
    Then I check that I am on the "lightwell brochures url part"