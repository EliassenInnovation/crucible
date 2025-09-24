@espn @pageObject=espn.ESPN @user
Feature: ESPN

  Background: navigate to ESPN
    And I add the "hover" class to "nfl navigation link"
    And I set the style on "nfl teams div" to "left: 118px; float: left; right: auto;"

    @adhoc
  Scenario: Comparing next games for two different teams
    And I am using the "MiamiDolphins" ESPN view
    And I grab the link for the "New York Jets"
    And I click on "miami dolphins link"
    And I store the text from "next game" as "stored text"
    And I click on "espn logo"
    And I add the "hover" class to "nfl navigation link"
    And I set the style on "nfl teams div" to "left: 118px; float: left; right: auto;"
    And I am using the "CincinnatiBengals" ESPN view
    And I click on "cincinnati bengals link"
    Then My stored "stored text" is not equal to the text from "next game"

  Scenario: Landing on specified team page
    And I navigate to the "New York Jets" team page
    And I remember the text from "next game" as "stored text"
    And I navigate to the "Green Bay Packers" team page
    Then My remembered "stored text" is not equal to the text from "next game"

  Scenario Outline: Comparing text from <first team>'s next game to <second team>'s next game
    And I navigate to the "<first team>" team page
    And I remember the text from "next game" as "stored text"
    And I navigate to the "<second team>" team page
    Then My remembered "stored text" is not equal to the text from "next game"
    Examples:
      | first team       | second team       |
      | New York Jets    | Green Bay Packers |
      | Cleveland Browns | Atlanta Falcons   |