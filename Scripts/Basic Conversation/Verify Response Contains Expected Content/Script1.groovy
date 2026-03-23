import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Send a message requesting an explanation about software testing
String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('explain how software testing works')

// Verify response length is greater than 100 characters (substantive content)
assert response.length() > 100 : "Expected response longer than 100 characters, but got ${response.length()}"

// Verify response contains testing-related content
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertResponseContains'(response, 'test')

WebUI.comment("Verify Response Contains Expected Content test passed. Response length: ${response.length()}")
