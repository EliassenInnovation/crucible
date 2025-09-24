@ESPN
  Feature: ESPN stuff

  Background: Prepare to run scenarios
    Given I am using the "ESPN" view
    And I create the browser chosen at the command line
    And I navigate to the page

  Scenario: I make it to the Miami Dolphins
    And I add the "hover" class to "nfl navigation link"
    And I set the style on "nfl teams div" to "left: 118px; float: left; right: auto;"
    And I click on "miami dolphins link"
