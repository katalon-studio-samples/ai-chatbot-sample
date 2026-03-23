import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable

// Send "explain" without waiting
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageNoWait'('explain')

// Verify thinking indicator becomes visible
CustomKeywords.'com.katalon.chatbot.StreamingKeywords.waitForThinkingIndicator'(10)

// Confirm it is visible right now
boolean generating = CustomKeywords.'com.katalon.chatbot.StreamingKeywords.isGenerating'()
assert generating : 'Expected the chatbot to be generating (thinking indicator or stop button visible)'

// Wait for the response to complete
CustomKeywords.'com.katalon.chatbot.StreamingKeywords.waitForResponseComplete'(30)

// Verify thinking indicator is no longer visible
TestObject thinkingIndicator = new TestObject('thinking-indicator')
thinkingIndicator.addProperty('data-testid', ConditionType.EQUALS, 'thinking-indicator')
WebUI.verifyElementNotVisible(thinkingIndicator)

WebUI.comment('Verify Thinking Indicator test passed.')
