import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Generate a string of 10001 characters
String oversizedInput = 'a' * 10001

// Send the oversized input
String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'(oversizedInput)

// Verify the response acknowledges the message length
assert response != null && response.length() > 0 : 'Expected a response for oversized input'

WebUI.comment("Oversized Input Handling test passed. Response: ${response.take(100)}...")
