import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Send a message to store context
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('remember my name is Alice')

// Ask the chatbot to recall the stored context
String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('what is my name')

// Verify the response contains "Alice"
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertResponseContains'(response, 'Alice')

WebUI.comment("Context Retention Across Turns test passed. Response: ${response}")
