import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable

// Send a message that triggers a code block response
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('code example')

// Verify the response contains a code block
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertMessageContainsMarkdown'(1, 'code_block')

// Verify the copy button exists on the code block
TestObject copyButton = new TestObject('copy-code-button')
copyButton.addProperty('data-testid', ConditionType.EQUALS, 'message-1-copy-code')
WebUI.verifyElementPresent(copyButton, 5)

// Click the copy button
WebUI.click(copyButton)

WebUI.comment('Code Block Copy Button test passed.')
