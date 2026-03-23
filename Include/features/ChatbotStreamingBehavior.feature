Feature: Chatbot Streaming Behavior
  As a user of an AI chatbot
  I want to see responses appear progressively
  So that I know the system is working

  Background:
    Given the chatbot application is open
    And the conversation is empty

  Scenario: Thinking indicator appears during generation
    When I send the message "explain" without waiting
    Then the thinking indicator should be visible within 2 seconds
    And I wait for the response to complete
    And the thinking indicator should not be visible

  Scenario: Stop generation button appears and works
    When I send the message "explain" without waiting
    Then the stop button should be visible within 2 seconds
    When I click the stop generation button
    Then the stop button should disappear within 5 seconds
    And the thinking indicator should not be visible
    And there should be a partial assistant response

  Scenario: Streaming response grows over time
    When I send the message "explain" without waiting
    And I wait for a partial response of at least 20 characters
    Then the response should still be generating
    And I wait for the response to complete
    And the final response should be longer than the partial response

  Scenario: Response latency is within acceptable range
    When I measure the response time for "hello"
    Then the response time should be less than 5000 milliseconds

  Scenario: Long response latency is within acceptable range
    When I measure the response time for "explain everything about testing"
    Then the response time should be less than 15000 milliseconds
