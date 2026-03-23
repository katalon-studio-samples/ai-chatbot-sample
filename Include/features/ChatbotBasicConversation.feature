Feature: Basic Chatbot Conversation
  As a user of an AI chatbot
  I want to send messages and receive responses
  So that I can interact with the AI assistant

  Background:
    Given the chatbot application is open
    And the conversation is empty

  Scenario: Send a simple greeting
    When I send the message "hello"
    Then I should receive a response within 10 seconds
    And the response should contain a greeting

  Scenario: Verify message ordering
    When I send the message "hello"
    And I wait for the response
    Then message 0 should have role "user"
    And message 0 should contain "hello"
    And message 1 should have role "assistant"

  Scenario: Send multiple messages
    When I send the message "hello"
    And I wait for the response
    And I send the message "explain how testing works"
    And I wait for the response
    Then there should be 4 messages in the conversation
    And message 0 should have role "user"
    And message 1 should have role "assistant"
    And message 2 should have role "user"
    And message 3 should have role "assistant"

  Scenario: Response contains substantive content
    When I send the message "explain how software testing works"
    And I wait for the response
    Then the last assistant response should be at least 100 characters long
