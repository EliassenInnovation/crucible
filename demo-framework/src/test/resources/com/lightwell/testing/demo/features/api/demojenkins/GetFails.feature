@GetFails
Feature: GetFails

  Background: prepare to call the api
    Given I am using "Common" objects
    And I call the get fails api

  @smoke
  Scenario: Smoke test the Get Fails json api
    And Api call should be OK

  Scenario Outline: check the response for the "<expected key>" key
    Then The response array objects should contain the key "<expected key>"
    Examples:
      | expected key   |
      | buildName      |
      | duration       |
      | result         |
      | link           |
      | runDate        |
      | parent         |
      | passedTests    |
      | totalTests     |
      | failedTests    |
      | buildNumber    |
      | actualDuration |
      | tags           |
