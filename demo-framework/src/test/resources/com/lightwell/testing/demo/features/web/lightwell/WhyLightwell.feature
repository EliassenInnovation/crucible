@whyLightwell
Feature: WhyLightwell

  Background: Navigate to the page
    Given I am using the "WhyLightwell" view
    And I create the browser chosen at the command line
    And I navigate to the page
    And I add necessary cookies

  Scenario: Check I am on the correct page
    Then I check that I am on the "Why Lightwell page"

  Scenario Outline: check for visibility of <element>
    Then I check that "<element>" is visible
    Examples:
      | element                |
      | why lightwell? heading |
      | let's connect button   |

  Scenario:  check that Let's Connect button is enabled
    Then I check that "let's connect button" is not disabled

  Scenario: The let's connect button takes me to the correct page
    And I click on "let's connect button"
    Then I check that I am on the "let's connect url part"

  Scenario Outline: check for visibility of <element> then click it
    And I check that "get to know us better heading" is visible
    Then I check that "<element>" is visible
    Then I click on "<element>"
    Then I check that I am on the "<url part>"
    Examples:
      | element               | url part                       |
      | leadership team       | leadership team url part       |
      | mission & core values | mission & core values url part |
      | culture               | culture url part               |
      | careers               | careers url part               |
      | community             | community url part             |
      | our brand             | our brand url part             |

  Scenario Outline: check for visibility of <element> then click it
    And I scroll until "industry expertise title" is visible
    Then I check that "<element>" is visible
    Then I click on "<element>"
    Then I check that I am on the "<url part>"
    Examples:
      | element              | url part                      |
      | industry expertise   | industry expertise url part   |
      | services we provide  | services we provide url part  |
      | solutions we deliver | solutions we deliver url part |
