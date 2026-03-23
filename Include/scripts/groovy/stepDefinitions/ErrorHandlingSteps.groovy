package stepDefinitions

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType

import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.java.en.Then

class ErrorHandlingSteps {

	private static TestObject makeTestObject(String testId) {
		TestObject obj = new TestObject(testId)
		obj.addProperty('data-testid', ConditionType.EQUALS, testId)
		return obj
	}

	@When("I wait up to {int} seconds for a response")
	def i_wait_up_to_seconds_for_a_response(int seconds) {
		CustomKeywords.'com.katalon.chatbot.StreamingKeywords.waitForResponseComplete'(seconds)
	}

	@Then("an error message should be displayed")
	def an_error_message_should_be_displayed() {
		Boolean visible = (Boolean) WebUI.executeJavaScript(
			"var el = document.querySelector('[data-testid=\"error-message\"]'); " +
			"return el != null && el.classList.contains('visible');",
			null
		)
		assert visible : "Expected error message to be displayed, but it was not visible"
	}

	@Then("the error message should indicate a rate limit")
	def the_error_message_should_indicate_a_rate_limit() {
		String errorText = (String) WebUI.executeJavaScript(
			"var el = document.querySelector('[data-testid=\"error-message\"]'); " +
			"return el ? el.textContent : '';",
			null
		)
		boolean isRateLimitError = errorText.toLowerCase().contains('rate limit') ||
			errorText.contains('429') ||
			errorText.toLowerCase().contains('too many requests')
		assert isRateLimitError :
			"Expected error message to indicate a rate limit, but got: ${errorText}"
	}

	@Then("the error message should indicate a timeout")
	def the_error_message_should_indicate_a_timeout() {
		String errorText = (String) WebUI.executeJavaScript(
			"var el = document.querySelector('[data-testid=\"error-message\"]'); " +
			"return el ? el.textContent : '';",
			null
		)
		boolean isTimeoutError = errorText.toLowerCase().contains('timeout') ||
			errorText.toLowerCase().contains('timed out')
		assert isTimeoutError :
			"Expected error message to indicate a timeout, but got: ${errorText}"
	}

	@Then("the error message should indicate a server error")
	def the_error_message_should_indicate_a_server_error() {
		String errorText = (String) WebUI.executeJavaScript(
			"var el = document.querySelector('[data-testid=\"error-message\"]'); " +
			"return el ? el.textContent : '';",
			null
		)
		boolean isServerError = errorText.toLowerCase().contains('server error') ||
			errorText.contains('500') ||
			errorText.toLowerCase().contains('internal error')
		assert isServerError :
			"Expected error message to indicate a server error, but got: ${errorText}"
	}

	@Then("the chat input should still be enabled")
	def the_chat_input_should_still_be_enabled() {
		boolean enabled = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.isChatInputEnabled'()
		assert enabled : "Expected chat input to be enabled, but it was disabled"
	}

	@When("I attempt to send an empty message")
	def i_attempt_to_send_an_empty_message() {
		TestObject chatInput = makeTestObject('chat-input')
		TestObject sendButton = makeTestObject('send-button')
		WebUI.clearText(chatInput)
		WebUI.click(sendButton)
	}

	@When("I attempt to send the message {string}")
	def i_attempt_to_send_the_message(String text) {
		TestObject chatInput = makeTestObject('chat-input')
		TestObject sendButton = makeTestObject('send-button')
		WebUI.setText(chatInput, text)
		WebUI.click(sendButton)
	}

	@Then("the send button should be disabled or the message should not be sent")
	def the_send_button_should_be_disabled_or_the_message_should_not_be_sent() {
		Boolean sendDisabled = (Boolean) WebUI.executeJavaScript(
			"var btn = document.querySelector('[data-testid=\"send-button\"]'); " +
			"return btn != null && btn.disabled;",
			null
		)
		int messageCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
		assert sendDisabled || messageCount == 0 :
			"Expected send button to be disabled or no messages to be sent, but button is enabled and found ${messageCount} messages"
	}

	@Then("the message count should be {int}")
	def the_message_count_should_be(int count) {
		int actualCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
		assert actualCount == count :
			"Expected message count to be ${count}, but was ${actualCount}"
	}

	@When("I send a message with {int} characters")
	def i_send_a_message_with_characters(int count) {
		String longMessage = 'a'.multiply(count)
		CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'(longMessage)
	}

	@Then("the response should acknowledge the message length")
	def the_response_should_acknowledge_the_message_length() {
		String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
		boolean acknowledged = response.toLowerCase().contains('long') ||
			response.toLowerCase().contains('characters') ||
			response.toLowerCase().contains('truncat') ||
			response.toLowerCase().contains('length')
		assert acknowledged :
			"Expected response to acknowledge the message length, but got: ${response}"
	}

	@Then("the application should not crash or freeze")
	def the_application_should_not_crash_or_freeze() {
		boolean enabled = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.isChatInputEnabled'()
		assert enabled : "Expected chat input to still be enabled (application not crashed), but it was disabled"
	}
}
