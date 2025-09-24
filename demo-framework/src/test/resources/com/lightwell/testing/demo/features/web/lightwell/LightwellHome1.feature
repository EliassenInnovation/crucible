@sampleWeb @lightwellHome1 @webdemo
Feature: Lightwell Home 1

  Background: Navigate to the the Lightwell home page
    Given I am using the "Lightwell" view
    And I create the browser chosen at the command line
    And I navigate to the page

    Scenario: I check for the See How button
      Then I check that "see how button" is visible

    Scenario: I check that the careers link navigates to the career page
      And I scroll until "careers link" is visible then click on it
      Then The page title is "IT Jobs and Careers at Lightwell | Consulting, Delivery, Development"

    Scenario: I check that the career link in the company tab navigates to the career page
      And I hover over "mega menu company tab"
      And I move the mouse to the "mega menu careers link"
      And I click on "mega menu careers link"
      Then I check that "career page heading" is visible

    Scenario: I check that the magnifying class icon opens and closes a field to enter search text.
      And I click on "magnifying glass"
      Then I check that "search field" is large enough to see
      And I click on "close icon"
      Then I check that "search field" is too small to see


    Scenario: I check that the data analytics card navigates to the data analytics page
      And I scroll until "data analytics card" is visible then click on it
      And I click on "data analytics card"
      Then The page title is "IT Solutions for Integration & Digital Transformation | Lightwell"

    Scenario: I check that the drop down menu allows for navigation to the move to the cloud page
      And I scroll until "I want to menu" is visible then click on it
      And I click on "move to the cloud selection"
      And I click on "drop down cloud link"
      Then The page title is "Move to the Cloud - Lightwell"