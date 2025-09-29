@sampleWeb @LightwellHome1
Feature: Lightwell Home 1

  Background: Navigate to the the Lightwell home page
    Given I am using the "Lightwell" view
    And I create the browser chosen at the command line
    And I navigate to the page

  @smoke
  Scenario: I check for the See How button
    Then I check that "see how button" is visible