@servicesAndSolutionsDropDown
Feature: Services and Solutions Drop Down

  Background: Navigate to the page
    Given I am using the "LightwellServicesAndSolutionsDropDown" view
    And I create the browser chosen at the command line
    And I navigate to the page

    Scenario Outline: Check that <element> takes me to the correct page
      And I hover over "services and solutions drop down"
      And I click on "<element>"
      Then I check that I am on the "<link part>"
      Examples:
      |element                                  | link part                                     |
      |capabilities link                        | capabilities link part                        |
      |integration and apis link                | integration and apis link part                |
      |B2B integration and edi link             | B2B integration and edi link part             |
      |application and software development link| application and software development link part|


