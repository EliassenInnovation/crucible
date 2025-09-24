@sideBySide @db_a @db_b @dbTest
Feature: Side by Side 10000

  Background: Connect to the dbs and create test data
    Given I connect to the "db_a" derby database
    And I am using "DbDemo" objects
    And I seed "db_a" with "10000" "people" records

  Scenario: Check we created a DB and test data
    Then I check there are records in "people"

  Scenario: Seed db_b with records and check it worked
    And I seed "db_b" with my remembered "people" records
    Then I check there are records in "people"

  Scenario: confirm there are the same number of records in both dbs
    And I seed "db_b" with my remembered "people" records
    And I grab the "people" records from "db_a" to "db_b"
    Then I confirm the row counts from both dbs match

  Scenario: confirm all columns match across both dbs
    And I seed "db_b" with my remembered "people" records
    And I grab the "people" records from "db_a" to "db_b"
    Then I confirm all columns match across both records sets

  Scenario: introduce chaos to demo failed matches
    And I seed "db_b" with my remembered "people" records but with "5" percent chaos
    And I grab the "people" records from "db_a" to "db_b"
    Then I confirm all columns match across both records sets
