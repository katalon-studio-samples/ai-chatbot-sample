import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Send "explain" without waiting for full response
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageNoWait'('explain')

// Wait for thinking indicator to appear
CustomKeywords.'com.katalon.chatbot.StreamingKeywords.waitForThinkingIndicator'(10)

// Wait for the response to fully complete
CustomKeywords.'com.katalon.chatbot.StreamingKeywords.waitForResponseComplete'(30)

// Verify response text is not empty
String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
assert response != null && response.length() > 0 : 'Expected a non-empty response after streaming complete'

WebUI.comment("Wait For Streaming Complete test passed. Response length: ${response.length()}")
