package stepDefinitions

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType

import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.java.en.Then

class StreamingBehaviorSteps {

	private String partialResponse = ''
	private long responseLatencyMs = 0

	private static TestObject makeTestObject(String testId) {
		TestObject obj = new TestObject(testId)
		obj.addProperty('data-testid', ConditionType.EQUALS, testId)
		return obj
	}

	@When("I send the message {string} without waiting")
	def i_send_the_message_without_waiting(String message) {
		CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageNoWait'(message)
	}

	@Then("the thinking indicator should be visible within {int} seconds")
	def the_thinking_indicator_should_be_visible_within_seconds(int seconds) {
		CustomKeywords.'com.katalon.chatbot.StreamingKeywords.waitForThinkingIndicator'(seconds)
	}

	@Then("I wait for the response to complete")
	def i_wait_for_the_response_to_complete() {
		CustomKeywords.'com.katalon.chatbot.StreamingKeywords.waitForResponseComplete'(30)
	}

	@Then("the thinking indicator should not be visible")
	def the_thinking_indicator_should_not_be_visible() {
		Boolean visible = (Boolean) WebUI.executeJavaScript(
			"var el = document.querySelector('[data-testid=\"thinking-indicator\"]'); " +
			"return el != null && el.classList.contains('visible');",
			null
		)
		assert !visible : "Expected thinking indicator to not be visible, but it was"
	}

	@Then("the stop button should be visible within {int} seconds")
	def the_stop_button_should_be_visible_within_seconds(int seconds) {
		long deadline = System.currentTimeMillis() + (seconds * 1000L)
		while (System.currentTimeMillis() < deadline) {
			Boolean visible = (Boolean) WebUI.executeJavaScript(
				"var el = document.querySelector('[data-testid=\"stop-button\"]'); " +
				"return el != null && el.classList.contains('visible');",
				null
			)
			if (visible) return
			Thread.sleep(200)
		}
		throw new Exception("Stop button did not become visible within ${seconds} seconds")
	}

	@When("I click the stop generation button")
	def i_click_the_stop_generation_button() {
		CustomKeywords.'com.katalon.chatbot.StreamingKeywords.stopGeneration'()
	}

	@Then("the stop button should disappear within {int} seconds")
	def the_stop_button_should_disappear_within_seconds(int seconds) {
		long deadline = System.currentTimeMillis() + (seconds * 1000L)
		while (System.currentTimeMillis() < deadline) {
			Boolean visible = (Boolean) WebUI.executeJavaScript(
				"var el = document.querySelector('[data-testid=\"stop-button\"]'); " +
				"return el != null && el.classList.contains('visible');",
				null
			)
			if (!visible) return
			Thread.sleep(200)
		}
		throw new Exception("Stop button did not disappear within ${seconds} seconds")
	}

	@Then("there should be a partial assistant response")
	def there_should_be_a_partial_assistant_response() {
		String lastMessage = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
		assert lastMessage != null && lastMessage.length() > 0 :
			"Expected a partial assistant response, but the last assistant message was empty"
	}

	@When("I wait for a partial response of at least {int} characters")
	def i_wait_for_a_partial_response_of_at_least_characters(int chars) {
		CustomKeywords.'com.katalon.chatbot.StreamingKeywords.waitForPartialResponse'(chars)
		partialResponse = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
	}

	@Then("the response should still be generating")
	def the_response_should_still_be_generating() {
		boolean generating = CustomKeywords.'com.katalon.chatbot.StreamingKeywords.isGenerating'()
		assert generating : "Expected the response to still be generating, but it is not"
	}

	@Then("the final response should be longer than the partial response")
	def the_final_response_should_be_longer_than_the_partial_response() {
		String finalResponse = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
		assert finalResponse.length() > partialResponse.length() :
			"Expected final response (${finalResponse.length()} chars) to be longer than partial response (${partialResponse.length()} chars)"
	}

	@When("I measure the response time for {string}")
	def i_measure_the_response_time_for(String message) {
		responseLatencyMs = CustomKeywords.'com.katalon.chatbot.StreamingKeywords.measureResponseLatency'(message)
	}

	@Then("the response time should be less than {int} milliseconds")
	def the_response_time_should_be_less_than_milliseconds(int ms) {
		assert responseLatencyMs < ms :
			"Expected response time to be less than ${ms}ms, but was ${responseLatencyMs}ms"
	}
}
