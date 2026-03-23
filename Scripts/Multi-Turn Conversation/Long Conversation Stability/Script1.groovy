import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Send 10 sequential messages and wait for each response
for (int i = 1; i <= 10; i++) {
	String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'("message number ${i}")
	assert response != null && response.length() > 0 : "Expected non-empty response for message ${i}"
	WebUI.comment("Message ${i} response received: ${response.take(50)}...")
}

// Verify all 20 messages are present (10 user + 10 assistant)
int messageCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
assert messageCount == 20 : "Expected 20 messages (10 user + 10 assistant), but found ${messageCount}"

// Verify input is still enabled after a long conversation
boolean inputEnabled = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.isChatInputEnabled'()
assert inputEnabled : 'Expected chat input to still be enabled after 10 exchanges'

WebUI.comment('Long Conversation Stability test passed.')
