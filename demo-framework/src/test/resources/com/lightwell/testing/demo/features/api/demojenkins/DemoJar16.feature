@demoJar16
Feature: Demo Jar 16

  Background: prepare to call the api
    Given I am using "Common" objects
    And I call the demo build 16 json api

  @smoke
  Scenario: Smoke test the Demo Jar 16 json api
    And Api call should be OK

  Scenario Outline: check the response for the "<expected key>" key
    Then The response should contain the key "<expected key>"
    Examples:
      | expected key      |
      | _class            |
      | actions           |
      | artifacts         |
      | building          |
      | description       |
      | displayName       |
      | duration          |
      | estimatedDuration |
      | executor          |
      | fullDisplayName   |
      | id                |