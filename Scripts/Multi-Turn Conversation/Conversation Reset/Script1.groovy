import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Send a message to store context
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('remember my name is Alice')

// Reset the conversation to clear all context
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.resetConversation'()

// Ask the chatbot to recall the name (should not remember after reset)
String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('what is my name')

// Verify the response does NOT contain "Alice"
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertResponseDoesNotContain'(response, 'Alice')

WebUI.comment("Conversation Reset test passed. Response: ${response}")
