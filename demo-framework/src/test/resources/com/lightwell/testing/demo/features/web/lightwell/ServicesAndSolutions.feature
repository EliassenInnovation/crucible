Feature: Services and Solutions
  
  Background: Navigate to the page
    Given I am using the "ServicesAndSolutions" view
    And I create the browser chosen at the command line
    And I navigate to the page


  Scenario: Check I am on the correct page
    Then I check that I am on the "services and solutions page"