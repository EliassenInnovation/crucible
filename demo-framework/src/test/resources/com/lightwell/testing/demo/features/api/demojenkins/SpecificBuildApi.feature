@specificBuildApi
Feature: Specific Build Api

  Background: prepare to call the api
    Given I am using "Common" objects
    And I ping the Specific Build Api api
    And I call the Specific Build Api api

  @smoke
  Scenario: Smoke test the Specific Build Api json api
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
      | keepLog           |
      | number            |
      | queueId           |
      | result            |
      | timestamp         |
      | url               |
      | builtOn           |
      | changeSet         |
      | culprits          |