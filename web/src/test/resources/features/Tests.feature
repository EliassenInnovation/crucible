@mock
Feature: Tests

  @browser=firefox @downloadPath=C:\\temp @adhoc @pageObject=Mock
  Scenario: Test Download
    And I navigate to the page

  @persisted @pageObject=Mock
  Scenario: test persisted part 1
    And I persist "testValue" as "testkey"

  @persisted @pageObject=Mock
  Scenario: test persisted part 1
    And I retrieve "testkey" from pesristed storage