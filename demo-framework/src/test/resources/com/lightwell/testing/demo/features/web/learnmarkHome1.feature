@sampleWeb @LearnmarkHome1
Feature: Learnmark Home 1

  Background: Navigate to the the Learnmark home page
    Given I am using the "Learnmark" view
    And I create the browser chosen at the command line
    And I navigate to the page

  Scenario: I click the nucleus tab and check the page
    And I check that "nucleus tab" is visible
    And I click on "nucleus tab"
    Then I check that "nucleus text" is visible
    