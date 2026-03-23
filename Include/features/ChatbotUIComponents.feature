Feature: Chatbot UI Components
  As a user of an AI chatbot
  I want rich UI features in the chat interface
  So that I can effectively interact with the AI

  Background:
    Given the chatbot application is open
    And the conversation is empty

  Scenario: Markdown is rendered correctly
    When I send the message "explain with formatting"
    And I wait for the response
    Then the response should contain rendered bold text
    And the response should contain rendered italic text
    And the response should contain a rendered list

  Scenario: Code blocks render with copy button
    When I send the message "code example"
    And I wait for the response
    Then the response should contain a rendered code block
    And the code block should have a copy button
    When I click the copy code button
    Then the clipboard should contain the code content

  Scenario: Citation links are present and clickable
    When I send the message "show sources"
    And I wait for the response
    Then the response should contain at least 2 citation links
    And each citation link should have an href attribute

  Scenario: Feedback buttons work
    When I send the message "hello"
    And I wait for the response
    Then the assistant message should have thumbs up and thumbs down buttons
    When I click the thumbs up button on message 1
    Then the thumbs up button should show as selected

  Scenario: Suggestion chips appear and are clickable
    When I send the message "follow up"
    And I wait for the response
    Then at least 2 suggestion chips should be visible
    When I click the first suggestion chip
    Then a new user message should be sent with the chip text
    And I should receive a response

  Scenario: File upload workflow
    When I click the upload button
    And I select a file named "test-document.txt"
    Then the file name should be displayed in the chat
    When I send the message "upload test"
    And I wait for the response
    Then the response should acknowledge the uploaded file

  Scenario: Chat history sidebar
    When I send the message "hello"
    And I wait for the response
    And I reset the conversation
    And I send the message "hi there"
    And I wait for the response
    When I toggle the chat history sidebar
    Then the sidebar should be visible
    And there should be at least 1 past conversation in the history
    When I click on the first history item
    Then the conversation should load with the previous messages
