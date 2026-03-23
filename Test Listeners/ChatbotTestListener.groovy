import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import internal.GlobalVariable

class ChatbotTestListener {

	@BeforeTestSuite
	def beforeTestSuite(TestSuiteContext testSuiteContext) {
		// Start the server once for the entire suite
		String url = CustomKeywords.'com.katalon.chatbot.ServerKeywords.startServer'()
		KeywordUtil.logInfo("Chatbot server started at ${url}")
	}

	@BeforeTestCase
	def beforeTestCase(TestCaseContext testCaseContext) {
		// Skip browser setup for the Start Local Server test case
		String testCaseId = testCaseContext.getTestCaseId()
		if (testCaseId.contains('Start Local Server')) {
			return
		}

		// Ensure server is running (handles standalone test case execution without suite)
		boolean running = CustomKeywords.'com.katalon.chatbot.ServerKeywords.isServerRunning'()
		if (!running) {
			CustomKeywords.'com.katalon.chatbot.ServerKeywords.startServer'()
		}

		// Open browser and navigate to chatbot
		WebUI.openBrowser(GlobalVariable.CHATBOT_URL)
		WebUI.maximizeWindow()
		WebUI.waitForPageLoad(GlobalVariable.DEFAULT_TIMEOUT)
	}

	@AfterTestCase
	def afterTestCase(TestCaseContext testCaseContext) {
		if (testCaseContext.getTestCaseStatus() == 'FAILED') {
			try {
				WebUI.takeScreenshot()
				int messageCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
				String lastMessage = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
				KeywordUtil.logInfo("Test failed. Message count: ${messageCount}, Last message: ${lastMessage}")
			} catch (Exception e) {
				KeywordUtil.logInfo("Test failed. Could not capture diagnostics: ${e.message}")
			}
		}

		// Close browser after each test case to ensure clean state
		try {
			WebUI.closeBrowser()
		} catch (Exception e) {
			// Browser may not have been opened
		}
	}

	@AfterTestSuite
	def afterTestSuite(TestSuiteContext testSuiteContext) {
		CustomKeywords.'com.katalon.chatbot.ServerKeywords.stopServer'()
	}
}
