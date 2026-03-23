# Specification: katalon-chatbot-testing-sample

## Project Goal

Create a Katalon Studio sample project that demonstrates how to test AI chatbot and conversational AI user interfaces. The project should serve as a reference for QA teams who need to automate testing of chat-based AI features (customer support bots, copilot sidebars, AI assistants, etc.).

The project follows the same structure and conventions as the existing MCP sample:
https://github.com/katalon-studio-samples/katalon-mcp-sample

## System Under Test

The project should include a **self-contained test target** — a simple chatbot web application bundled with the project — so that anyone can clone the repo and run tests immediately without needing external API keys or third-party services.

### Bundled Test Application

Create a lightweight single-page chatbot UI (`Include/webapp/index.html` or similar) that simulates common AI chatbot behaviors. This is NOT a real AI — it's a deterministic simulator designed to exercise all the test patterns.

The webapp should be served locally (e.g., via a simple HTTP server started in a `@SetUp` test listener, or via `python -m http.server`, or via a small Node/Express app in the project).

**The chatbot simulator must support these behaviors:**

1. **Streaming token simulation** — Responses appear word-by-word or chunk-by-chunk with configurable delay (simulates SSE/streaming LLM output)
2. **Variable response latency** — Some responses take 500ms, others 3-5 seconds (simulates real model inference times)
3. **Multi-turn conversation state** — The bot tracks conversation history and responds differently based on prior turns
4. **Markdown rendering** — Bot responses include bold, italic, code blocks, lists, and links that render as HTML
5. **"Thinking" / "Generating" indicator** — Shows a typing indicator or spinner while the response is being generated
6. **Stop generation button** — A button to cancel in-progress responses (appears during streaming, disappears when done)
7. **Error states** — Configurable error responses (rate limit, timeout, server error) triggered by specific input phrases
8. **Empty/edge case handling** — Behavior for empty input, whitespace-only input, extremely long input (10K+ characters)
9. **Code block with copy button** — Rendered code blocks should have a "copy to clipboard" button
10. **Citation/source links** — Some responses include clickable source references
11. **Feedback buttons** — Thumbs up/down on each bot response
12. **Conversation reset** — A "New Chat" or "Clear" button
13. **Chat history sidebar** — A collapsible panel showing past conversations
14. **File/image upload** — An attachment button (simulated — accepts the file, bot acknowledges it)
15. **Suggested follow-up prompts** — Clickable suggestion chips after certain responses

**Trigger phrases for deterministic testing:**

The simulator should respond to specific trigger phrases so tests are deterministic:

| User Input Contains | Simulator Behavior |
|---|---|
| `hello` or `hi` | Simple greeting response, fast (~200ms) |
| `explain` | Long streaming response (multiple paragraphs with markdown), 3-5 second generation |
| `code example` | Response with a fenced code block (Python or JS) |
| `show sources` | Response with citation links |
| `error:rate_limit` | Simulates a 429 rate limit error message |
| `error:timeout` | Simulates a timeout (no response for 30s, then error) |
| `error:server` | Simulates a 500 internal server error message |
| `follow up` | Response with 3 suggested follow-up prompt chips |
| `remember my name is {X}` | Stores name, uses it in subsequent responses (tests multi-turn memory) |
| `what is my name` | Recalls stored name or says "I don't know" (tests multi-turn memory) |
| `upload test` | Prompts for file upload, acknowledges uploaded file name |
| (empty / whitespace only) | Input should be blocked or show validation message |
| (10,000+ characters) | Bot responds with a truncation notice |

### UI Element Structure

The chatbot UI should use stable `data-testid` attributes for all interactive elements:

```
data-testid="chat-input"              // The text input field
data-testid="send-button"             // Send message button
data-testid="stop-button"             // Stop generation button (visible during streaming)
data-testid="new-chat-button"         // Clear/reset conversation
data-testid="thinking-indicator"      // Typing/generating indicator
data-testid="message-{index}"         // Each message container (0-indexed)
data-testid="message-{index}-content" // Message text content
data-testid="message-{index}-role"    // "user" or "assistant"
data-testid="message-{index}-thumbsup"   // Feedback button
data-testid="message-{index}-thumbsdown" // Feedback button
data-testid="message-{index}-copy-code"  // Copy code button (if code block present)
data-testid="suggestion-chip-{index}"    // Follow-up suggestion chips
data-testid="upload-button"           // File upload trigger
data-testid="upload-input"            // Hidden file input
data-testid="chat-history-toggle"     // Toggle sidebar
data-testid="chat-history-item-{index}"  // Past conversation items
data-testid="error-message"           // Error display area
```

## Katalon Project Structure

```
katalon-chatbot-testing-sample/
├── Include/
│   ├── features/                        # BDD feature files
│   │   ├── ChatbotBasicConversation.feature
│   │   ├── ChatbotStreamingBehavior.feature
│   │   ├── ChatbotErrorHandling.feature
│   │   ├── ChatbotMultiTurn.feature
│   │   └── ChatbotUIComponents.feature
│   ├── scripts/groovy/
│   │   └── stepDefinitions/             # Cucumber step definitions
│   │       ├── BasicConversationSteps.groovy
│   │       ├── StreamingBehaviorSteps.groovy
│   │       ├── ErrorHandlingSteps.groovy
│   │       ├── MultiTurnSteps.groovy
│   │       └── UIComponentSteps.groovy
│   └── webapp/                          # Bundled test chatbot application
│       ├── index.html                   # Main chatbot UI
│       ├── app.js                       # Chatbot simulator logic
│       └── styles.css                   # Chatbot styling
├── Keywords/
│   └── com/katalon/chatbot/
│       ├── ChatbotKeywords.groovy       # Core chatbot interaction keywords
│       ├── StreamingKeywords.groovy     # Streaming/async-specific keywords
│       ├── AssertionKeywords.groovy     # AI-output assertion helpers
│       └── ServerKeywords.groovy        # Local webapp server management
├── Object Repository/
│   └── Chatbot/                         # Test objects for chatbot UI elements
│       ├── input_chat.rs
│       ├── btn_send.rs
│       ├── btn_stop.rs
│       ├── btn_new_chat.rs
│       ├── div_thinking_indicator.rs
│       ├── div_message.rs
│       ├── btn_thumbsup.rs
│       ├── btn_thumbsdown.rs
│       ├── btn_copy_code.rs
│       ├── div_suggestion_chip.rs
│       ├── btn_upload.rs
│       ├── btn_chat_history_toggle.rs
│       └── div_error_message.rs
├── Profiles/
│   └── default.glbl                     # Global variables (server port, timeouts, etc.)
├── Scripts/                             # Groovy test scripts (one per test case)
├── Test Cases/
│   ├── Basic Conversation/
│   │   ├── Send Simple Message.tc
│   │   ├── Send Message and Verify Response.tc
│   │   ├── Verify Response Contains Expected Content.tc
│   │   └── Verify Message Ordering.tc
│   ├── Streaming Behavior/
│   │   ├── Wait For Streaming Complete.tc
│   │   ├── Stop Generation Mid-Stream.tc
│   │   ├── Verify Thinking Indicator.tc
│   │   └── Measure Response Latency.tc
│   ├── Multi-Turn Conversation/
│   │   ├── Context Retention Across Turns.tc
│   │   ├── Conversation Reset.tc
│   │   └── Long Conversation Stability.tc
│   ├── Error Handling/
│   │   ├── Rate Limit Error.tc
│   │   ├── Timeout Error.tc
│   │   ├── Server Error.tc
│   │   ├── Empty Input Validation.tc
│   │   └── Oversized Input Handling.tc
│   ├── UI Components/
│   │   ├── Markdown Rendering.tc
│   │   ├── Code Block Copy Button.tc
│   │   ├── Citation Links.tc
│   │   ├── Feedback Buttons.tc
│   │   ├── Suggestion Chips.tc
│   │   ├── File Upload.tc
│   │   └── Chat History Sidebar.tc
│   ├── Run All BDD Tests.tc
│   └── Setup and Teardown/
│       └── Start Local Server.tc
├── Test Suites/
│   ├── Chatbot Smoke Suite.ts           # Quick validation (5-6 key tests)
│   ├── Chatbot Full Regression.ts       # All test cases
│   ├── Chatbot BDD Suite.ts             # All BDD feature files
│   └── Chatbot Streaming Suite.ts       # Streaming-specific tests
├── Test Listeners/
│   └── ChatbotTestListener.groovy       # @SetUp: start server, @TearDown: stop server
├── build.gradle
├── CLAUDE.md                            # Detailed technical documentation
├── AGENTS.md                            # -> Points to CLAUDE.md
├── LICENSE                              # Apache 2.0
└── README.md
```

## Custom Keywords Specification

### ChatbotKeywords.groovy

```groovy
package com.katalon.chatbot

class ChatbotKeywords {

    /**
     * Send a message to the chatbot and wait for the response to complete.
     * This is the primary high-level keyword most tests should use.
     *
     * Steps:
     * 1. Type message into chat input
     * 2. Click send button
     * 3. Wait for thinking indicator to appear (confirms message was received)
     * 4. Wait for thinking indicator to disappear (confirms response is complete)
     * 5. Wait for stop button to disappear (confirms streaming is done)
     * 6. Return the text content of the last assistant message
     *
     * @param message The user message to send
     * @param timeoutSeconds Max time to wait for response completion (default: 30)
     * @return String The assistant's response text
     */
    @Keyword
    def sendMessageAndWaitForResponse(String message, int timeoutSeconds = 30)

    /**
     * Send a message without waiting for response completion.
     * Use this when you need to interact with the UI during streaming
     * (e.g., clicking stop button, checking thinking indicator).
     *
     * @param message The user message to send
     */
    @Keyword
    def sendMessageNoWait(String message)

    /**
     * Get the text content of a specific message by index.
     * Index 0 = first message in the conversation.
     *
     * @param index Zero-based message index
     * @return String The message text content
     */
    @Keyword
    def getMessageContent(int index)

    /**
     * Get the role ("user" or "assistant") of a specific message.
     *
     * @param index Zero-based message index
     * @return String "user" or "assistant"
     */
    @Keyword
    def getMessageRole(int index)

    /**
     * Get the total number of messages in the current conversation.
     *
     * @return int Message count
     */
    @Keyword
    def getMessageCount()

    /**
     * Get the last assistant message content.
     * Convenience method — equivalent to finding the last message with role "assistant".
     *
     * @return String The last assistant response text
     */
    @Keyword
    def getLastAssistantMessage()

    /**
     * Click the "New Chat" button and verify the conversation is cleared.
     */
    @Keyword
    def resetConversation()

    /**
     * Verify the chat input is enabled and ready to accept input.
     *
     * @return boolean
     */
    @Keyword
    def isChatInputEnabled()
}
```

### StreamingKeywords.groovy

```groovy
package com.katalon.chatbot

class StreamingKeywords {

    /**
     * Wait for the thinking/generating indicator to appear.
     * Useful for confirming the bot has started processing.
     *
     * @param timeoutSeconds Max wait time (default: 10)
     */
    @Keyword
    def waitForThinkingIndicator(int timeoutSeconds = 10)

    /**
     * Wait for the thinking indicator to disappear, signaling response complete.
     *
     * @param timeoutSeconds Max wait time (default: 30)
     */
    @Keyword
    def waitForResponseComplete(int timeoutSeconds = 30)

    /**
     * Check if the bot is currently generating a response.
     * Checks for presence of thinking indicator OR stop button.
     *
     * @return boolean True if response is still being generated
     */
    @Keyword
    def isGenerating()

    /**
     * Click the stop generation button.
     * Verifies the button is visible before clicking.
     * After clicking, waits for the stop button to disappear.
     */
    @Keyword
    def stopGeneration()

    /**
     * Measure the time between sending a message and response completion.
     * Sends the message, starts a timer, waits for completion, returns elapsed ms.
     *
     * @param message The message to send
     * @param timeoutSeconds Max wait time
     * @return long Elapsed time in milliseconds
     */
    @Keyword
    def measureResponseLatency(String message, int timeoutSeconds = 30)

    /**
     * Wait for the response to reach a minimum character length.
     * Useful for verifying streaming is progressing without waiting for completion.
     * Polls the last assistant message content length.
     *
     * @param minLength Minimum character count to wait for
     * @param timeoutSeconds Max wait time
     */
    @Keyword
    def waitForPartialResponse(int minLength, int timeoutSeconds = 15)
}
```

### AssertionKeywords.groovy

```groovy
package com.katalon.chatbot

class AssertionKeywords {

    /**
     * Assert that the response contains a substring (case-insensitive).
     * Use this instead of exact match for non-deterministic AI outputs.
     *
     * @param response The response text to check
     * @param expected Substring that should be present
     */
    @Keyword
    def assertResponseContains(String response, String expected)

    /**
     * Assert that the response matches a regex pattern.
     * Useful for validating structured parts of a response
     * (e.g., "contains a URL", "contains a number").
     *
     * @param response The response text
     * @param pattern Regex pattern
     */
    @Keyword
    def assertResponseMatchesPattern(String response, String pattern)

    /**
     * Assert that the response does NOT contain a substring.
     * Useful for safety/guardrail testing.
     *
     * @param response The response text
     * @param forbidden Substring that should NOT be present
     */
    @Keyword
    def assertResponseDoesNotContain(String response, String forbidden)

    /**
     * Assert response length is within expected bounds.
     * AI responses vary in length — use ranges, not exact counts.
     *
     * @param response The response text
     * @param minLength Minimum acceptable length
     * @param maxLength Maximum acceptable length
     */
    @Keyword
    def assertResponseLengthInRange(String response, int minLength, int maxLength)

    /**
     * Assert that a message contains rendered markdown elements.
     * Checks the message's HTML (not text) for expected elements.
     *
     * @param messageIndex Zero-based message index
     * @param elementType One of: "bold", "italic", "code_inline", "code_block", "link", "list"
     */
    @Keyword
    def assertMessageContainsMarkdown(int messageIndex, String elementType)

    /**
     * Assert that a code block is present in the message and contains expected content.
     *
     * @param messageIndex Zero-based message index
     * @param expectedCodeSubstring Substring expected in the code block
     */
    @Keyword
    def assertCodeBlockContains(int messageIndex, String expectedCodeSubstring)

    /**
     * Assert that citation/source links are present and clickable.
     *
     * @param messageIndex Zero-based message index
     * @param minLinks Minimum number of citation links expected
     */
    @Keyword
    def assertCitationLinksPresent(int messageIndex, int minLinks)
}
```

### ServerKeywords.groovy

```groovy
package com.katalon.chatbot

class ServerKeywords {

    /**
     * Start the local chatbot web application.
     * Launches a lightweight HTTP server serving the bundled webapp.
     * Should be called in test listener @SetUp or first test case.
     *
     * Uses GlobalVariable.CHATBOT_PORT (default: 3456).
     *
     * @return String The base URL of the running server
     */
    @Keyword
    def startServer()

    /**
     * Stop the local chatbot web application.
     * Should be called in test listener @TearDown.
     */
    @Keyword
    def stopServer()

    /**
     * Check if the server is running and responding.
     *
     * @return boolean
     */
    @Keyword
    def isServerRunning()

    /**
     * Get the base URL of the running chatbot application.
     *
     * @return String e.g., "http://localhost:3456"
     */
    @Keyword
    def getServerUrl()
}
```

## BDD Feature Files

### ChatbotBasicConversation.feature

```gherkin
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
```

### ChatbotStreamingBehavior.feature

```gherkin
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
```

### ChatbotErrorHandling.feature

```gherkin
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
```

### ChatbotMultiTurn.feature

```gherkin
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
```

### ChatbotUIComponents.feature

```gherkin
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
```

## Global Variables (Profiles/default.glbl)

| Variable | Type | Default | Description |
|---|---|---|---|
| `CHATBOT_PORT` | int | `3456` | Port for the local chatbot webapp |
| `CHATBOT_URL` | String | `http://localhost:3456` | Base URL (derived from port) |
| `DEFAULT_TIMEOUT` | int | `30` | Default wait timeout in seconds |
| `STREAMING_POLL_INTERVAL` | int | `500` | Milliseconds between polling checks during streaming |
| `BROWSER` | String | `Chrome` | Browser to use for testing |

## Test Listener Specification

### ChatbotTestListener.groovy

```groovy
class ChatbotTestListener {

    /**
     * @BeforeTestSuite
     * - Start the local chatbot webapp server
     * - Wait for server health check to pass
     * - Log the server URL
     */

    /**
     * @BeforeTestCase
     * - Open browser to CHATBOT_URL (if not already open)
     * - Wait for chat input to be visible and enabled
     * - Reset conversation (click New Chat) to ensure clean state
     */

    /**
     * @AfterTestCase
     * - Take screenshot on failure
     * - Log conversation state (message count, last message) on failure
     */

    /**
     * @AfterTestSuite
     * - Stop the local chatbot webapp server
     * - Close browser
     */
}
```

## Key Implementation Patterns

### Pattern 1: Waiting for Streaming Completion

The most critical pattern in chatbot testing. Never use `Thread.sleep()`. Instead, use a polling approach:

```
ALGORITHM: waitForResponseComplete(timeout)
1. Record start time
2. LOOP while elapsed < timeout:
   a. Check if thinking indicator is visible → if NO, check if stop button is visible → if NO, DONE
   b. Sleep for STREAMING_POLL_INTERVAL ms
3. If timeout reached, FAIL with descriptive message including last known state
```

The key insight is that **two conditions** must both be false: thinking indicator gone AND stop button gone. Some chatbots remove the thinking indicator before the stop button disappears, or vice versa.

### Pattern 2: Non-Deterministic Response Assertion

AI responses are not deterministic. Tests must NEVER use exact string matching for response content. Instead:

- **Contains checks**: `response.toLowerCase().contains("greeting")` — verify the response is in the right category
- **Pattern matching**: Regex for structural validation (e.g., "response contains a URL")
- **Length ranges**: Assert response is between X and Y characters, not exactly N
- **Negative assertions**: Assert the response does NOT contain error messages or system prompt leakage
- **Structural checks**: Verify the response has markdown elements, code blocks, or links by inspecting rendered HTML

### Pattern 3: Handling Streaming Token Rendering

When testing streaming, the DOM updates as tokens arrive. The message element exists but its `textContent` grows over time:

```
ALGORITHM: waitForPartialResponse(minLength, timeout)
1. Identify the last assistant message element
2. LOOP while elapsed < timeout:
   a. Read element.textContent.length
   b. If length >= minLength, DONE
   c. Sleep for STREAMING_POLL_INTERVAL ms
3. If timeout reached, FAIL
```

### Pattern 4: Scroll Following

Long conversations push content off-screen. Tests should verify:
- After sending a message, the viewport scrolls to show the new message
- During streaming, the viewport follows the growing response
- After response completes, the chat input is visible

Check this with JavaScript execution: `element.getBoundingClientRect().bottom <= window.innerHeight`

### Pattern 5: Message Index Addressing

Messages are addressed by zero-based index. In a typical exchange:
- Message 0: user (first user message)
- Message 1: assistant (first bot response)
- Message 2: user (second user message)
- Message 3: assistant (second bot response)

The index is the position in the full conversation, not per-role. Test objects should use parameterized selectors: `[data-testid="message-${index}"]`

## Implementation Notes

### Webapp Technology

Keep the webapp simple. Vanilla HTML/CSS/JS is preferred over frameworks — this is a test fixture, not a production app. The entire thing should be a single HTML file with inline `<style>` and `<script>` if possible, or at most 3 files (HTML, JS, CSS).

Use CSS transitions for the streaming animation (tokens appearing) so Selenium can observe DOM changes rather than visual-only animations.

### Server Startup

For serving the webapp, use one of these approaches (in order of preference):
1. **Python `http.server`** — available everywhere Katalon runs, no dependencies
2. **Groovy embedded Jetty** — if more control is needed
3. **Node.js `http-server`** — if Node is available

The server process should be started as a background process and its PID stored for cleanup.

### Browser Considerations

- Tests should work in Chrome (headless and headed)
- Use WebDriver waits (`WebUI.waitForElementVisible`, `WebUI.waitForElementNotPresent`) instead of `Thread.sleep()`
- For clipboard testing (code block copy), use `WebUI.executeJavaScript()` to read from the clipboard API or verify the button's click handler was invoked

### What NOT to Include

- Do NOT include real LLM API integration — this is about testing UI patterns, not model quality
- Do NOT require API keys or external services
- Do NOT use Katalon's AI features (StudioAssist, KAI) — this is about testing OTHER people's AI features with Katalon
- Do NOT over-engineer the webapp — ugly is fine, testable is mandatory

## README Outline

The README should follow the same structure as the MCP sample:

1. **Title and one-line description**
2. **What This Project Shows** — bullet list of capabilities demonstrated
3. **Quick Start** — prerequisites, setup, run tests
4. **Test Coverage** — table of scenarios
5. **Key Testing Patterns** — the 5 patterns above, briefly explained with code snippets
6. **Project Structure** — directory tree
7. **Custom Keywords Reference** — brief description of each keyword class
8. **Adapting to Your Application** — guidance on how to take these patterns and apply them to a real chatbot (replace selectors, adjust timeouts, add real assertion logic)
9. **Troubleshooting** — common issues
10. **License** — Apache 2.0

## CLAUDE.md / AGENTS.md

Create a `CLAUDE.md` with detailed technical documentation (same role as in the MCP sample) and an `AGENTS.md` that points to it. The CLAUDE.md should include:

- Full custom keyword API documentation
- Step definition patterns for BDD
- Webapp simulator API (how trigger phrases work, how to add new ones)
- Troubleshooting: common timing issues, flaky test patterns, browser-specific quirks
- Architecture decisions and rationale

## Acceptance Criteria

The project is complete when:

1. `git clone` + open in Katalon Studio + run `Test Suites/Chatbot Smoke Suite` works with zero setup beyond Katalon itself
2. All BDD scenarios pass in Chrome
3. The webapp chatbot looks reasonable (not beautiful, but clearly a chat interface)
4. Custom keywords are documented with Javadoc comments
5. README is complete and follows the MCP sample's tone
6. A developer unfamiliar with the project can understand the patterns from reading the test cases and README
7. The project demonstrates at least these 5 patterns:
   - Waiting for streaming to complete
   - Non-deterministic response assertion
   - Multi-turn conversation state testing
   - Error state handling
   - Rich UI component interaction (code blocks, feedback buttons, suggestion chips)
