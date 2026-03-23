import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable

// Send the rate limit trigger phrase
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('error:rate_limit')

// Verify error message is visible
TestObject errorMessage = new TestObject('error-message')
errorMessage.addProperty('data-testid', ConditionType.EQUALS, 'error-message')
WebUI.verifyElementVisible(errorMessage)

// Verify error message contains rate limit text
String errorText = WebUI.getText(errorMessage)
assert errorText.toLowerCase().contains('rate limit') : "Expected error message to contain 'rate limit', but got: ${errorText}"

// Verify chat input is still enabled (user can retry)
boolean inputEnabled = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.isChatInputEnabled'()
assert inputEnabled : 'Expected chat input to be enabled after rate limit error'

WebUI.comment("Rate Limit Error test passed. Error text: ${errorText}")
