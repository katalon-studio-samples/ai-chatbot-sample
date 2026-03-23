import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable

// Send a message in the first conversation
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('hello from first conversation')

// Reset to start a new conversation
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.resetConversation'()

// Send a message in the second conversation
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('hello from second conversation')

// Toggle the chat history sidebar
TestObject historyToggle = new TestObject('chat-history-toggle')
historyToggle.addProperty('data-testid', ConditionType.EQUALS, 'chat-history-toggle')
WebUI.click(historyToggle)

// Verify at least one history item is present
TestObject historyItem0 = new TestObject('chat-history-item-0')
historyItem0.addProperty('data-testid', ConditionType.EQUALS, 'chat-history-item-0')
WebUI.verifyElementPresent(historyItem0, 5)

// Click the first history item to load a previous conversation
WebUI.click(historyItem0)

// Verify the conversation loaded with previous messages
int messageCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
assert messageCount >= 2 : "Expected at least 2 messages after loading history item, but found ${messageCount}"

WebUI.comment('Chat History Sidebar test passed.')
