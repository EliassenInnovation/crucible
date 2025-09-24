@joeBurrow @espn
Feature: Joe Burrow

  Background: navigate to Joe Burrow's player page
    Given I am using the "players.JoeBurrow" ESPN view
    And I create the browser chosen at the command line
    And I navigate to the page


  Scenario: Confirming the Joe Burrow Page loaded properly
    Then I check that I am on the base page


  Scenario: Confirming each seasons rushing yards add up to the career total yards.
    And I figure out the column number for "Passing Yards" in the "Passing" table
    And I sum all the "Passing Yards" in the "Passing" table
    Then My remembered "Passing Yards" total is equal to the career total

  Scenario Outline: Comparing season <passing stat type> total to career <passing stat type>
    And I figure out the column number for "<passing stat type>" in the "Passing" table
    And I sum all the "<passing stat type>" in the "Passing" table
    Then My remembered "<passing stat type>" total is equal to the career total
    Examples:
      | passing stat type      |
      | Games Played           |
      | Completions            |
      | Passing Attempts       |
      | Completion Percentage  |
      | Passing Yards          |
      | Yards Per Pass Attempt |
      | Passing Touchdowns     |
      | Interceptions          |
      | Longest Pass           |
      | Total Sacks            |
      | Passer Rating          |
      | Adjusted QBR           |

  Scenario Outline: Comparing season <rushing stat type> total to career <rushing stat type>
    And I figure out the column number for "<rushing stat type>" in the "Rushing" table
    And I sum all the "<rushing stat type>" in the "Rushing" table
    Then My remembered "<rushing stat type>" total is equal to the career total
    Examples:
      | rushing stat type      |
      | Games Played           |
      | Rushing Attempts       |
      | Rushing Yards          |
      | Yards Per Rush Attempt |
      | Rushing Touchdowns     |
      | Long Rushing           |
      | Rushing 1st downs      |
      | Rushing Fumbles        |
      | Rushing Fumbles Lost   |


