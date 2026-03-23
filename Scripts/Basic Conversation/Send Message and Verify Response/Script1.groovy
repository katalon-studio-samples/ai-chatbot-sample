import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Send "hello" and wait for the response
String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('hello')

// Verify the response contains greeting-related text
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertResponseContains'(response, 'hello')

WebUI.comment("Send Message and Verify Response test passed. Response: ${response}")
