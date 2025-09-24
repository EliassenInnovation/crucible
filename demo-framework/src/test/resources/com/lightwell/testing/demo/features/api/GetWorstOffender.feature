@getWorstOffender @demodashboard
Feature: Get Worst Offender

  Background: Prepare to call the api
    Given I am using "Common" objects
    And I call the Get Worst Offender api

  @smoke
  Scenario: Smoke test the Get Worst Offender json api
    And Api call should be OK

  Scenario Outline: Check the response for the "<expected key>" key
    Then The response should contain the key "<expected key>"
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