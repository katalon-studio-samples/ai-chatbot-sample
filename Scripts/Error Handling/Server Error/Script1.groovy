import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable

// Send the server error trigger phrase
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('error:server')

// Verify error message is visible
TestObject errorMessage = new TestObject('error-message')
errorMessage.addProperty('data-testid', ConditionType.EQUALS, 'error-message')
WebUI.verifyElementVisible(errorMessage)

// Verify error message contains server error text
String errorText = WebUI.getText(errorMessage)
assert errorText.toLowerCase().contains('server error') : "Expected error message to contain 'server error', but got: ${errorText}"

WebUI.comment("Server Error test passed. Error text: ${errorText}")
