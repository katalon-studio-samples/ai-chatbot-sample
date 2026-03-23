import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable

// Send a message that triggers suggestion chips
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('follow up')

// Verify suggestion chips appear
TestObject chip0 = new TestObject('suggestion-chip-0')
chip0.addProperty('data-testid', ConditionType.EQUALS, 'suggestion-chip-0')
WebUI.verifyElementPresent(chip0, 5)

// Get the text of the first chip before clicking
String chipText = WebUI.getText(chip0)
assert chipText != null && chipText.length() > 0 : 'Expected suggestion chip to have text'

// Click the first suggestion chip
WebUI.click(chip0)

// Verify a new message was sent with the chip text
CustomKeywords.'com.katalon.chatbot.StreamingKeywords.waitForResponseComplete'(30)
int messageCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
assert messageCount >= 4 : "Expected at least 4 messages after clicking suggestion chip, but found ${messageCount}"

// Verify the new user message contains the chip text
String userMessage = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageContent'(2)
assert userMessage.contains(chipText) : "Expected user message to contain chip text '${chipText}', but got '${userMessage}'"

WebUI.comment("Suggestion Chips test passed. Chip text: ${chipText}")
