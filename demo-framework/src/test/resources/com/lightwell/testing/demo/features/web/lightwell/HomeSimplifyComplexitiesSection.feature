@homepage
Feature: Homepage - Simplify Complexities Section

  Background: Prepare to run scenarios
    Given I am using the "Homepage" view
    And I create the browser chosen at the command line
    And I navigate to the page
    And I add necessary cookies


  Scenario Outline: check that <element> goes to <destination>
    And I click on "<element>"
    Then I check that I am on the "<destination>"
    Examples:
      | element                             | destination                           |
      | view all of our capabilities button | view all of our capabilities url part |
#      | integration & api management card   | integration & api management url part |

  Scenario Outline: check that <link> in card goes to <destination>
    And I scroll until "<parent element>" is visible
    And I want to convert "<containing element>" to display block
    And I click on the "<link>" that is in "<containing element>"
    Then I check that I am on the "<destination>"
    Examples:
      | link                              | destination                           | containing element                | parent element                           |
      | integration & api management link | integration & api management url part | integration & api management card | integration & api management card parent |

  Scenario: check that Browse All Resources button is enabled
    Then I check that "view all of our capabilities button" is not disabled

#  Scenario: view all of our capabilities button takes me to the correct page
#    And I click on "view all of our capabilities button"
#    Then I check that I am on the "view all of our capabilities url part"

