import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable

// Send the timeout trigger phrase and wait up to 35 seconds
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('error:timeout', 35)

// Verify error message is visible
TestObject errorMessage = new TestObject('error-message')
errorMessage.addProperty('data-testid', ConditionType.EQUALS, 'error-message')
WebUI.waitForElementVisible(errorMessage, 35)

// Verify error message contains timeout text
String errorText = WebUI.getText(errorMessage)
assert errorText.toLowerCase().contains('timeout') : "Expected error message to contain 'timeout', but got: ${errorText}"

WebUI.comment("Timeout Error test passed. Error text: ${errorText}")
