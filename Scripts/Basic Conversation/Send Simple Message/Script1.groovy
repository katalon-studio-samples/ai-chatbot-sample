import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Send a simple "hello" message and verify a response is received
String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('hello')

// Verify a response was received (non-empty)
assert response != null && response.length() > 0 : 'Expected a non-empty response from the chatbot'

// Verify message count is 2 (one user message + one assistant message)
int messageCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
assert messageCount == 2 : "Expected 2 messages (user + assistant), but found ${messageCount}"

WebUI.comment("Send Simple Message test passed. Response: ${response}")
