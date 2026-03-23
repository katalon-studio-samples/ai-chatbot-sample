import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Send a message that triggers citation links in the response
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('show sources')

// Verify at least 2 citation links are present in the assistant message (index 1)
CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertCitationLinksPresent'(1, 2)

// Verify the links have href attributes
boolean linksHaveHref = (boolean) WebUI.executeJavaScript(
	"var links = document.querySelectorAll('[data-testid=\"message-1-content\"] a');" +
	"return Array.from(links).every(function(a) { return a.href && a.href.length > 0; });",
	null
)
assert linksHaveHref : 'Expected all citation links to have href attributes'

WebUI.comment('Citation Links test passed.')
