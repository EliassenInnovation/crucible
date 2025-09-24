@homepage
Feature: Homepage

  Background: Prepare to run scenarios
    Given I am using the "Homepage" view
    And I create the browser chosen at the command line
    And I navigate to the page

  # "I want to" dropdown
  Scenario: check that i want to dropdown is enabled
    Then I check that "i want to dropdown" is not disabled

  Scenario: check if converted to display block
    And I want to convert "i want to dropdown" to display block
    #And I click on the "goal box 1" that is in "i want to dropdown"

  #Scenario: check that i want to dropdown expands when hovering
    #And I hover over "i want to dropdown"
    #Then I check that "list of goal items" is not disabled