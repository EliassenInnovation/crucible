@demos @exp-2 @logResponse
Feature: Demos

  Background: Prep to call the api
    Given I am using "Common" objects
    And I do not want to grab headers

  Scenario: Call the auto-complete api and should get OK
    And I call rest country Calling Code with parameter "65"
    Then Api call should be OK

  Scenario:  Call the Avatar Characters API and check that it is ok
    And I call Avatar Characters API
    Then Api call should be OK

  Scenario:  I check that Aang has more allies than enemies
    Then I check that "Aang" has more "allies" than "enemies"

  Scenario:  I call the Good Jokes API and check that it is ok
    And I call Good Jokes API
    Then Api call should be OK

  Scenario: I check that there are 26 programming jokes
    Then I check that there are "26" jokes of type "programming"