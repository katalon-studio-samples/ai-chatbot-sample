Feature: Multi-Turn Conversation
  As a user of an AI chatbot
  I want the bot to remember context from earlier in the conversation
  So that I can have coherent multi-turn conversations

  Background:
    Given the chatbot application is open
    And the conversation is empty

  Scenario: Bot remembers information from earlier turns
    When I send the message "remember my name is Alice"
    And I wait for the response
    And I send the message "what is my name"
    And I wait for the response
    Then the last assistant response should contain "Alice"

  Scenario: Conversation reset clears context
    When I send the message "remember my name is Alice"
    And I wait for the response
    And I reset the conversation
    And I send the message "what is my name"
    And I wait for the response
    Then the last assistant response should not contain "Alice"

  Scenario: Long conversation remains stable
    When I send 10 sequential messages
    And I wait for each response
    Then all 10 responses should be received
    And the conversation should contain 20 messages
    And the chat input should still be enabled
    And scrolling should follow the latest message
