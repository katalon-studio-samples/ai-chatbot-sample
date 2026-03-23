import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable

// Send a message and wait for response
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('hello')

// Verify thumbs up button exists on the assistant message (index 1)
TestObject thumbsUp = new TestObject('thumbs-up')
thumbsUp.addProperty('data-testid', ConditionType.EQUALS, 'message-1-thumbsup')
WebUI.verifyElementPresent(thumbsUp, 5)

// Verify thumbs down button exists on the assistant message (index 1)
TestObject thumbsDown = new TestObject('thumbs-down')
thumbsDown.addProperty('data-testid', ConditionType.EQUALS, 'message-1-thumbsdown')
WebUI.verifyElementPresent(thumbsDown, 5)

// Click thumbs up
WebUI.click(thumbsUp)

// Verify thumbs up is selected (has 'selected' class)
boolean isSelected = (boolean) WebUI.executeJavaScript(
	"var el = document.querySelector('[data-testid=\"message-1-thumbsup\"]');" +
	"return el != null && el.classList.contains('selected');",
	null
)
assert isSelected : 'Expected thumbs up button to be selected after clicking'

WebUI.comment('Feedback Buttons test passed.')
