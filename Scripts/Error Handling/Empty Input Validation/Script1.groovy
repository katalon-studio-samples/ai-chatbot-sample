import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable

// Clear the input field
TestObject chatInput = new TestObject('chat-input')
chatInput.addProperty('data-testid', ConditionType.EQUALS, 'chat-input')
WebUI.clearText(chatInput)

// Attempt to click send with empty input
TestObject sendButton = new TestObject('send-button')
sendButton.addProperty('data-testid', ConditionType.EQUALS, 'send-button')
WebUI.click(sendButton)

// Verify no messages were sent (message count should remain 0)
int messageCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
assert messageCount == 0 : "Expected 0 messages when sending empty input, but found ${messageCount}"

WebUI.comment('Empty Input Validation test passed.')
