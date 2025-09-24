@homepage
Feature: Homepage

  Background: Prepare to run scenarios
    Given I am using the "Homepage" view
    And I create the browser chosen at the command line
    And I navigate to the page

  # Retail card
  Scenario: check that retail card is enabled
    Then I check that "retail card" is not disabled

  Scenario: retail card takes me to the correct page
    And I click on "retail card"
    Then I check that I am on the "retail url part"

  # Consumer goods card
  Scenario: check that consumer goods card is enabled
    Then I check that "consumer goods card" is not disabled

  Scenario: consumer goods card takes me to the correct page
    And I click on "consumer goods card"
    Then I check that I am on the "consumer goods url part"

  # Food & beverage card
  Scenario: check that food & beverage card is enabled
    Then I check that "food & beverage card" is not disabled

  Scenario: food & beverage card takes me to the correct page
    And I click on "food & beverage card"
    Then I check that I am on the "food & beverage url part"

  # Logistics & transportation card
  Scenario: check that logistics & transportation card is enabled
    Then I check that "logistics & transportation card" is not disabled

  Scenario: logistics & transportation card takes me to the correct page
    And I click on "logistics & transportation card"
    Then I check that I am on the "logistics & transportation url part"

  # Manufacturing card
  Scenario: check that manufacturing card is enabled
    Then I check that "manufacturing card" is not disabled

  Scenario: manufacturing card takes me to the correct page
    And I click on "manufacturing card"
    Then I check that I am on the "manufacturing url part"

  # Financial services & Insurance card
  Scenario: check that financial services & insurance card is enabled
    Then I check that "financial services & insurance card" is not disabled

  Scenario: financial services & insurance card takes me to the correct page
    And I click on "financial services & insurance card"
    Then I check that I am on the "financial services & insurance url part"

  # Healthcare card
  Scenario: check that healthcare card is enabled
    Then I check that "healthcare card" is not disabled

  Scenario: healthcare card takes me to the correct page
    And I click on "healthcare card"
    Then I check that I am on the "healthcare url part"

  # Pharma & life sciences card
  Scenario: check that Pharma & life sciences card is enabled
    Then I check that "Pharma & life sciences card" is not disabled

  Scenario: Pharma & life sciences card takes me to the correct page
    And I click on "Pharma & life sciences card"
    Then I check that I am on the "Pharma & life sciences url part"

  # Energy & utilities card
  Scenario: check that Energy & utilities card is enabled
    Then I check that "Energy & utilities card" is not disabled

  Scenario: Energy & utilities card takes me to the correct page
    And I click on "Energy & utilities card"
    Then I check that I am on the "Energy & utilities url part"

  # Industry Solutions Button
  Scenario: check that See Our Industry Solutions button is enabled
    Then I check that "see our industry solutions button" is not disabled

  Scenario: see our industry solutions button takes me to the correct page
    And I click on "see our industry solutions button"
    Then I check that I am on the "your industry url part"
