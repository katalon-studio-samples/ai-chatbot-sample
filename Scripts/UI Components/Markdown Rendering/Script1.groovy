import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Send a message that triggers markdown-formatted response
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('explain with formatting')

// Verify bold text is rendered in the assistant message (message index 1)
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertMessageContainsMarkdown'(1, 'bold')

// Verify italic text is rendered
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertMessageContainsMarkdown'(1, 'italic')

// Verify list is rendered
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertMessageContainsMarkdown'(1, 'list')

WebUI.comment('Markdown Rendering test passed.')
