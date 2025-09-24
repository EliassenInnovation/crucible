@AboutUs

Feature: About Us

  Background: Navigate to About Us page
    Given I am using the "AboutUs" view
    And I create the browser chosen at the command line
    And I navigate to the page
    And I add necessary cookies

    Scenario: Validate connect with us page navigation and header content
      And I click on "connect with us button"
      Then The "header" text contains "CONTACT"


  Scenario: Validate why lightwell? page navigation and header content
    And I scroll until "why lightwell button" is visible then click on it
    Then The "header" text contains "WHY LIGHTWELL?"


    Scenario Outline: Validate Get to know us section links
      And I scroll until "<element>" is visible then click on it
      Then I check that "<title>" is visible
      Examples:
      |element               |title                      |
      |our brand             |our brand title            |
      |leadership team       |leadership team title      |
      |mission & core values |mission & core values title|
      |culture               |culture title              |
      |community             |community title            |
      |careers               |careers title              |