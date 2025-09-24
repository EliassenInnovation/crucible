@espn @espnAssociatedLinksApi
Feature: ESPN Associated Links Api

  Background: Prepare to call the API
    Given I am using "espn.ESPN" objects

  Scenario: The api call should return success
    And I call ESPN Associated Links API
    Then the API response code should be success