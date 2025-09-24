@News
  Feature: Company > News
    Background: Navigate to News page
      Given I am using the "News" view
      And I create the browser chosen at the command line
      And I navigate to the page
      And I add necessary cookies

    Scenario: Validate breadcrumbs
      Then I check that "news breadcrumbs" is visible

    Scenario Outline: : Validate Read more links
      And I scroll until "<readmore link>" is visible then click on it
      Then I check that "<page header>" is visible
      Examples:
      |readmore link              | page header        |
      |eg group read more link    |  eg group header   |
      |lightwell read more link   |  lightwell header  |
      |covid read more link       |  covid header      |
