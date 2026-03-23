import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable

// Use JavaScript to simulate file selection on the hidden file input
WebUI.executeJavaScript(
	"var input = document.querySelector('[data-testid=\"upload-input\"]');" +
	"var file = new File(['test content'], 'test-document.txt', { type: 'text/plain' });" +
	"var dataTransfer = new DataTransfer();" +
	"dataTransfer.items.add(file);" +
	"input.files = dataTransfer.files;" +
	"input.dispatchEvent(new Event('change', { bubbles: true }));",
	null
)

// Verify the file name is displayed
boolean fileNameDisplayed = (boolean) WebUI.executeJavaScript(
	"return document.body.innerText.includes('test-document.txt');",
	null
)
assert fileNameDisplayed : 'Expected file name "test-document.txt" to be displayed after upload'

// Send a message to trigger upload acknowledgment
String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('upload test')

// Verify the response acknowledges the upload
assert response != null && response.length() > 0 : 'Expected a response acknowledging the upload'

WebUI.comment("File Upload test passed. Response: ${response}")
