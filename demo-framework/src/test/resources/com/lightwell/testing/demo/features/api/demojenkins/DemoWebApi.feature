@demoWebApi
Feature: Demo Web Api

  Background: prepare to call the api
    Given I am using "Common" objects
    And I call the Demo Web Api api

  @smoke @demojenkins
  Scenario: Smoke test the Demo Web Api json api
    And Api call should be OK

  Scenario Outline: check the response for the "<expected key>" key
    Then The response should contain the key "<expected key>"
    Examples:
      | expected key          |
      | _class                |
      | actions               |
      | description           |
      | displayName           |
      | displayNameOrNull     |
      | fullDisplayName       |
      | fullName              |
      | name                  |
      | url                   |
      | buildable             |
      | builds                |
      | color                 |
      | firstBuild            |
      | healthReport          |
      | inQueue               |
      | keepDependencies      |
      | lastBuild             |
      | lastCompletedBuild    |
      | lastFailedBuild       |
      | lastStableBuild       |
      | lastSuccessfulBuild   |
      | lastUnstableBuild     |
      | lastUnsuccessfulBuild |
      | nextBuildNumber       |
      | property              |
      | queueItem             |
      | concurrentBuild       |
      | disabled              |
      | downstreamProjects    |
      | labelExpression       |
      | scm                   |
      | upstreamProjects      |
