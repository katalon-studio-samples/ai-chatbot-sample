# Katalon Chatbot Testing Sample

A Katalon Studio sample project demonstrating how to test AI chatbot and conversational AI user interfaces. Includes a bundled chatbot simulator so you can run all tests immediately with zero external dependencies.

## What This Project Shows

- **Streaming response testing** — Wait for token-by-token responses, stop generation mid-stream, measure latency
- **Non-deterministic output assertion** — Use contains checks, regex patterns, and length ranges instead of exact matching
- **Multi-turn conversation testing** — Verify context retention across turns, test conversation reset
- **Error handling verification** — Rate limits, timeouts, server errors, input validation
- **Rich UI component testing** — Markdown rendering, code blocks with copy buttons, citation links, feedback buttons, suggestion chips, file uploads, chat history sidebar
- **BDD/Cucumber integration** — Full Gherkin feature files with step definitions

## Quick Start

### Prerequisites

- [Katalon Studio](https://katalon.com/katalon-studio) (any recent version)
- Python 3 (for the local test server — pre-installed on macOS/Linux)
- Chrome browser

### Setup

1. Clone this repository:
   ```bash
   git clone https://github.com/katalon-studio-samples/katalon-chatbot-testing-sample.git
   ```

2. Open the project in Katalon Studio:
   - File → Open Project → Select the cloned folder

3. Run the smoke suite:
   - Open `Test Suites/Chatbot Smoke Suite`
   - Click the Run button (or press Ctrl+Shift+A)
   - The test listener automatically starts the local chatbot server

That's it. No API keys, no external services, no configuration needed.

### Running from Command Line

```bash
katalonc -projectPath="<path-to-project>" \
  -testSuitePath="Test Suites/Chatbot Smoke Suite" \
  -browserType="Chrome"
```

## Test Coverage

### Basic Conversation (4 tests)
| Test Case | What It Verifies |
|---|---|
| Send Simple Message | Message is sent and response received |
| Send Message and Verify Response | Response contains expected greeting content |
| Verify Response Contains Expected Content | Long response has substantive content |
| Verify Message Ordering | User/assistant messages alternate correctly |

### Streaming Behavior (4 tests)
| Test Case | What It Verifies |
|---|---|
| Wait For Streaming Complete | Thinking indicator lifecycle |
| Stop Generation Mid-Stream | Stop button cancels in-progress response |
| Verify Thinking Indicator | Indicator appears/disappears correctly |
| Measure Response Latency | Response times within acceptable bounds |

### Multi-Turn Conversation (3 tests)
| Test Case | What It Verifies |
|---|---|
| Context Retention Across Turns | Bot remembers earlier conversation context |
| Conversation Reset | New Chat clears all state |
| Long Conversation Stability | 10+ exchanges remain stable |

### Error Handling (5 tests)
| Test Case | What It Verifies |
|---|---|
| Rate Limit Error | 429 error displayed gracefully |
| Timeout Error | Timeout handled after 30s |
| Server Error | 500 error displayed gracefully |
| Empty Input Validation | Empty/whitespace input blocked |
| Oversized Input Handling | 10K+ character input handled |

### UI Components (7 tests)
| Test Case | What It Verifies |
|---|---|
| Markdown Rendering | Bold, italic, lists render as HTML |
| Code Block Copy Button | Code blocks have working copy buttons |
| Citation Links | Source links are present and clickable |
| Feedback Buttons | Thumbs up/down work correctly |
| Suggestion Chips | Follow-up chips appear and are clickable |
| File Upload | Attachment workflow functions |
| Chat History Sidebar | Past conversations are accessible |

### BDD Suites
All of the above scenarios are also available as Cucumber/Gherkin feature files in `Include/features/`.

## Key Testing Patterns

### 1. Waiting for Streaming Completion

Never use `Thread.sleep()`. Poll for two conditions:

```groovy
// Both must be false for response to be "complete"
def waitForResponseComplete(int timeoutSeconds) {
    long start = System.currentTimeMillis()
    while (System.currentTimeMillis() - start < timeoutSeconds * 1000) {
        boolean thinkingVisible = isThinkingIndicatorVisible()
        boolean stopVisible = isStopButtonVisible()
        if (!thinkingVisible && !stopVisible) return
        Thread.sleep(GlobalVariable.STREAMING_POLL_INTERVAL)
    }
    KeywordUtil.markFailed("Response did not complete within ${timeoutSeconds}s")
}
```

### 2. Non-Deterministic Response Assertion

AI responses vary. Use flexible assertions:

```groovy
// Good: contains check
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertResponseContains'(response, "testing")

// Good: length range
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertResponseLengthInRange'(response, 50, 5000)

// Bad: exact match (will break with any response variation)
assert response == "Here is exactly what I expected"  // DON'T DO THIS
```

### 3. Multi-Turn Context Testing

```groovy
// Store information
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'("remember my name is Alice")

// Verify recall
String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'("what is my name")
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertResponseContains'(response, "Alice")
```

### 4. Error State Handling

```groovy
// Trigger specific error
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'("error:rate_limit")

// Verify error UI
WebUI.verifyElementVisible(findTestObject('Chatbot/div_error_message'))

// Verify recovery
assert CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.isChatInputEnabled'()
```

### 5. Rich UI Component Interaction

```groovy
// Verify markdown rendered as HTML
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertMessageContainsMarkdown'(1, "bold")
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertMessageContainsMarkdown'(1, "code_block")

// Verify citation links
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertCitationLinksPresent'(1, 2)
```

## Project Structure

```
katalon-chatbot-testing-sample/
├── Include/
│   ├── features/                    # BDD feature files (5 files)
│   ├── scripts/groovy/
│   │   └── stepDefinitions/         # Cucumber step definitions (5 files)
│   └── webapp/                      # Bundled chatbot simulator
│       ├── index.html
│       ├── app.js
│       └── styles.css
├── Keywords/
│   └── com/katalon/chatbot/         # Custom keywords (4 classes)
├── Object Repository/
│   └── Chatbot/                     # Test objects (13 files)
├── Profiles/
│   └── default.glbl                 # Global variables
├── Scripts/                         # Test scripts (25 scripts)
├── Test Cases/                      # Test cases (25 cases)
├── Test Suites/                     # 4 test suites
├── Test Listeners/
│   └── ChatbotTestListener.groovy   # Server start/stop, browser management
├── CLAUDE.md                        # Technical documentation
└── README.md
```

## Custom Keywords Reference

| Class | Purpose |
|---|---|
| `ChatbotKeywords` | Core chatbot interaction — send, receive, reset, query |
| `StreamingKeywords` | Streaming-specific — wait, poll, stop, measure latency |
| `AssertionKeywords` | Flexible assertions for non-deterministic AI output |
| `ServerKeywords` | Local webapp server lifecycle management |

## Adapting to Your Application

This project tests a simulator. To test your real chatbot:

1. **Replace selectors** — Update `Object Repository/Chatbot/` test objects with your app's selectors. If your app uses `data-testid`, you're in luck. If not, use stable CSS selectors or XPath.

2. **Adjust timeouts** — Real AI models are slower. Increase `DEFAULT_TIMEOUT` and `STREAMING_POLL_INTERVAL` in `Profiles/default.glbl`.

3. **Update trigger phrases** — Replace deterministic trigger phrases with your real test scenarios. Adjust assertions from exact contains to broader pattern matching.

4. **Handle authentication** — Add login steps to the test listener's `@BeforeTestSuite` if your chatbot requires authentication.

5. **Add model-specific assertions** — If testing a specific model, you may want to add assertions for response quality, safety, or format compliance.

6. **Remove the bundled server** — Delete `Include/webapp/` and `ServerKeywords.groovy`. Update the test listener to navigate to your app's URL instead.

## Troubleshooting

### Tests fail with "Element not found"
- Verify the local server is running: open http://localhost:3456 in your browser
- Check that Python 3 is installed: `python3 --version`
- Try running `Test Cases/Setup and Teardown/Start Local Server` manually

### Streaming tests are flaky
- Increase `STREAMING_POLL_INTERVAL` from 500 to 1000 in the global profile
- The simulator uses random chunk sizes — slight timing variations are expected

### Server port conflict
- Change `CHATBOT_PORT` in `Profiles/default.glbl` to an available port
- Also update `CHATBOT_URL` to match

### Chrome issues
- Ensure ChromeDriver version matches your Chrome browser version
- Katalon Studio manages ChromeDriver automatically — try updating Katalon

## License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.
