Feature: Chatbot Error Handling
  As a user of an AI chatbot
  I want clear error messages when something goes wrong
  So that I understand what happened

  Background:
    Given the chatbot application is open
    And the conversation is empty

  Scenario: Rate limit error is handled gracefully
    When I send the message "error:rate_limit"
    And I wait for the response
    Then an error message should be displayed
    And the error message should indicate a rate limit
    And the chat input should still be enabled

  Scenario: Timeout error is handled gracefully
    When I send the message "error:timeout"
    And I wait up to 35 seconds for a response
    Then an error message should be displayed
    And the error message should indicate a timeout
    And the chat input should still be enabled

  Scenario: Server error is handled gracefully
    When I send the message "error:server"
    And I wait for the response
    Then an error message should be displayed
    And the error message should indicate a server error
    And the chat input should still be enabled

  Scenario: Empty input is rejected
    When I attempt to send an empty message
    Then the send button should be disabled or the message should not be sent
    And the message count should be 0

  Scenario: Whitespace-only input is rejected
    When I attempt to send the message "   "
    Then the send button should be disabled or the message should not be sent
    And the message count should be 0

  Scenario: Extremely long input is handled
    When I send a message with 10000 characters
    And I wait for the response
    Then the response should acknowledge the message length
    And the application should not crash or freeze
