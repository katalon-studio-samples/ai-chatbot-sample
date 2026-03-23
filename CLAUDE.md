# Katalon Chatbot Testing Sample — Technical Documentation

## Overview

This is a Katalon Studio sample project demonstrating how to test AI chatbot and conversational AI user interfaces. It includes a bundled chatbot simulator webapp and comprehensive test patterns for streaming responses, multi-turn conversations, error handling, and rich UI components.

## Architecture

### Bundled Webapp (`Include/webapp/`)

A vanilla HTML/CSS/JS chatbot simulator that responds deterministically to trigger phrases. No frameworks, no build step. Served via Python's `http.server` module.

- `index.html` — Main chat UI with data-testid attributes on all interactive elements
- `app.js` — Simulator logic: trigger phrase matching, streaming simulation, conversation state
- `styles.css` — Chat interface styling

The simulator is NOT a real AI. It matches user input against trigger phrases and returns pre-defined responses with configurable streaming delays.

### Trigger Phrase Reference

| User Input Contains | Behavior | Response Delay |
|---|---|---|
| `hello` / `hi` | Simple greeting | ~200ms |
| `explain` | Long markdown response with streaming | 500ms + 3-5s stream |
| `code example` | Fenced JS code block | 400ms |
| `show sources` | Response with 3 citation links | 400ms |
| `error:rate_limit` | 429 error message | 500ms |
| `error:timeout` | Timeout error after 30s | 30000ms |
| `error:server` | 500 error message | 800ms |
| `follow up` | Response + 3 suggestion chips | 300ms |
| `remember my name is {X}` | Stores name in memory | 300ms |
| `what is my name` | Recalls stored name | 300ms |
| `upload test` | Acknowledges uploaded file | 400ms |
| (empty/whitespace) | Blocked at input level | N/A |
| (10K+ chars) | Truncation notice | 200ms |

### Custom Keywords

#### `com.katalon.chatbot.ChatbotKeywords`
Core chatbot interaction:
- `sendMessageAndWaitForResponse(message, timeout)` — Primary keyword: type, send, wait for complete response
- `sendMessageNoWait(message)` — Send without waiting (for streaming tests)
- `getMessageContent(index)` — Get message text by zero-based index
- `getMessageRole(index)` — Get "user" or "assistant" role
- `getMessageCount()` — Total messages in conversation
- `getLastAssistantMessage()` — Last bot response text
- `resetConversation()` — Click New Chat, verify cleared
- `isChatInputEnabled()` — Check input is ready

#### `com.katalon.chatbot.StreamingKeywords`
Streaming/async behavior:
- `waitForThinkingIndicator(timeout)` — Wait for thinking dots to appear
- `waitForResponseComplete(timeout)` — Poll until thinking + stop button both gone
- `isGenerating()` — Check if currently generating
- `stopGeneration()` — Click stop button
- `measureResponseLatency(message, timeout)` — Time from send to completion (ms)
- `waitForPartialResponse(minLength, timeout)` — Wait for streaming to reach N chars

#### `com.katalon.chatbot.AssertionKeywords`
Non-deterministic response assertions:
- `assertResponseContains(response, expected)` — Case-insensitive substring check
- `assertResponseMatchesPattern(response, pattern)` — Regex match
- `assertResponseDoesNotContain(response, forbidden)` — Negative check
- `assertResponseLengthInRange(response, min, max)` — Length bounds
- `assertMessageContainsMarkdown(index, elementType)` — Check rendered HTML for bold/italic/code_block/link/list
- `assertCodeBlockContains(index, expected)` — Code content check
- `assertCitationLinksPresent(index, minLinks)` — Count anchor tags

#### `com.katalon.chatbot.ServerKeywords`
Local server management:
- `startServer()` — Start Python HTTP server on CHATBOT_PORT
- `stopServer()` — Kill server process
- `isServerRunning()` — Health check
- `getServerUrl()` — Returns base URL

### Test Object Selectors

All test objects use CSS selectors targeting `data-testid` attributes:
```
[data-testid="chat-input"]
[data-testid="send-button"]
[data-testid="stop-button"]
[data-testid="new-chat-button"]
[data-testid="thinking-indicator"]
[data-testid="message-{index}"]
[data-testid="message-{index}-content"]
[data-testid="message-{index}-role"]
[data-testid="message-{index}-thumbsup"]
[data-testid="message-{index}-thumbsdown"]
[data-testid="message-{index}-copy-code"]
[data-testid="suggestion-chip-{index}"]
[data-testid="upload-button"]
[data-testid="upload-input"]
[data-testid="chat-history-toggle"]
[data-testid="chat-history-item-{index}"]
[data-testid="error-message"]
```

Parameterized test objects (with dynamic index) are constructed in keywords using `TestObject` with `ConditionType.EQUALS` on the `data-testid` attribute.

## Key Testing Patterns

### Pattern 1: Waiting for Streaming Completion
Never use `Thread.sleep()`. Poll for two conditions:
1. Thinking indicator not visible
2. Stop button not visible

Both must be false. Use `GlobalVariable.STREAMING_POLL_INTERVAL` between polls.

### Pattern 2: Non-Deterministic Response Assertion
AI responses vary. Never use exact string matching. Use:
- Contains checks (case-insensitive)
- Regex pattern matching
- Length ranges
- Negative assertions
- Structural HTML checks (bold, code blocks, links)

### Pattern 3: Streaming Token Rendering
The message DOM element exists but `textContent` grows over time. Use `waitForPartialResponse` to assert streaming progress without waiting for completion.

### Pattern 4: Scroll Following
After sending messages or during streaming, verify the viewport follows content using `element.getBoundingClientRect()` via JavaScript execution.

### Pattern 5: Message Index Addressing
Messages are zero-indexed across the full conversation:
- 0: first user message
- 1: first assistant response
- 2: second user message
- etc.

## BDD Step Definitions

Step definitions are in `Include/scripts/groovy/stepDefinitions/`. They delegate to custom keywords:

- `BasicConversationSteps.groovy` — Send/receive, ordering, content checks
- `StreamingBehaviorSteps.groovy` — Thinking indicator, stop button, latency
- `ErrorHandlingSteps.groovy` — Error triggers, input validation
- `MultiTurnSteps.groovy` — Memory, reset, long conversations
- `UIComponentSteps.groovy` — Markdown, code blocks, feedback, chips, upload, sidebar

## Adding New Trigger Phrases

1. Add pattern matching in `Include/webapp/app.js` in the `getResponse()` function
2. Return an object with `type`, `text`, `delay`, `streamDelay`, and optional `suggestions`
3. Add corresponding test case and/or BDD scenario
4. Add custom keyword assertions if new response types are involved

## Troubleshooting

### Server won't start
- Check if port 3456 is already in use: `lsof -i :3456`
- Verify Python 3 is available: `python3 --version`
- Check the webapp files exist in `Include/webapp/`

### Flaky streaming tests
- Increase `GlobalVariable.DEFAULT_TIMEOUT` for slower machines
- Increase `GlobalVariable.STREAMING_POLL_INTERVAL` if poll loops are too tight
- The simulator uses `Math.random()` for chunk sizes — timing varies slightly between runs

### Element not found
- Verify the webapp is running: open `http://localhost:3456` in a browser
- Check that `data-testid` attributes are present in the HTML
- Ensure the test listener's `@BeforeTestCase` is resetting conversation state

### Timeout errors in tests
- The `error:timeout` trigger phrase has a real 30-second delay — increase test timeout to 35+ seconds
- For streaming tests, `explain` trigger produces long responses — allow 15+ seconds

## Global Variables

| Variable | Default | Description |
|---|---|---|
| `CHATBOT_PORT` | 3456 | Local server port |
| `CHATBOT_URL` | http://localhost:3456 | Base URL |
| `DEFAULT_TIMEOUT` | 30 | Wait timeout (seconds) |
| `STREAMING_POLL_INTERVAL` | 500 | Poll interval (ms) |
| `BROWSER` | Chrome | Test browser |
