@getTotals
Feature: Get Totals

  Background: prepare to call the api
    Given I am using "Common" objects
    And I call the Get Totals Api

  @smoke
  Scenario: Smoke test the Get Totals API
    And Api call should be OK

  Scenario Outline: check the response for the "<expected key>" key
    Then The response should contain the key "<expected key>"
    Examples:
      | expected key      |
      | successes         |
      | fails             |
      | others            |
      | disabled          |