@capabilities
Feature: Capabilities

  Background: Prepare to run scenarios
    Given I am using the "Capabilities" view
    And I create the browser chosen at the command line
    And I navigate to the page
    And I add necessary cookies

    Scenario: check that the Lets Talk button goes to the correct page
      And I click on "lets talk button"
      Then I check that I am on the "contact page"