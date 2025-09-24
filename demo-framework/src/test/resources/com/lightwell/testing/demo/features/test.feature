@pageObject=Common
Feature: test
  Background: background

    @adhoc
    Scenario:
      And I log "Constants.THIS_IS_A_CONSTANT"
      And I also want to log "this is a constant"