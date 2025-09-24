@newIdeas
Feature: New Ideas

  Background: Prepare to run scenarios
    Given I am using the "NewIdeas" view
    And I create the browser chosen at the command line
    And I navigate to the page

  Scenario Outline: check for visibility of <element>
    Then I check that "<element>" is visible
    Examples:
    |element                      |
    |spark new ideas heading      |
    |browse all resources button  |

  Scenario: check that Browse All Resources button is enabled
    Then I check that "browse all resources button" is not disabled
    
  Scenario: browse all resources button takes me to the correct page
    And I click on "browse all resources button"
    Then I check that I am on the "resource library url part"

