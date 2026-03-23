package stepDefinitions

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType

import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.java.en.Then

class MultiTurnSteps {

	private int sequentialMessageCount = 0

	@When("I reset the conversation")
	def i_reset_the_conversation() {
		CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.resetConversation'()
	}

	@Then("the last assistant response should contain {string}")
	def the_last_assistant_response_should_contain(String text) {
		String lastResponse = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
		CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertResponseContains'(lastResponse, text)
	}

	@Then("the last assistant response should not contain {string}")
	def the_last_assistant_response_should_not_contain(String text) {
		String lastResponse = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
		CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertResponseDoesNotContain'(lastResponse, text)
	}

	@When("I send {int} sequential messages")
	def i_send_sequential_messages(int count) {
		sequentialMessageCount = count
		for (int i = 1; i <= count; i++) {
			CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'("hello ${i}")
		}
	}

	@When("I wait for each response")
	def i_wait_for_each_response() {
		// Responses are already waited for in sendMessageAndWaitForResponse within the loop
	}

	@Then("all {int} responses should be received")
	def all_responses_should_be_received(int count) {
		int totalMessages = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
		int actualAssistantMessages = 0
		for (int i = 0; i < totalMessages; i++) {
			String role = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageRole'(i)
			if (role.toLowerCase().contains('assistant')) {
				actualAssistantMessages++
			}
		}
		assert actualAssistantMessages >= count :
			"Expected at least ${count} assistant responses, but found ${actualAssistantMessages}"
	}

	@Then("the conversation should contain {int} messages")
	def the_conversation_should_contain_messages(int count) {
		int actualCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
		assert actualCount == count :
			"Expected ${count} messages in conversation, but found ${actualCount}"
	}

	@Then("scrolling should follow the latest message")
	def scrolling_should_follow_the_latest_message() {
		Boolean isScrolledToBottom = (Boolean) WebUI.executeJavaScript(
			"var container = document.getElementById('messages'); " +
			"if (!container) return true; " +
			"return Math.abs(container.scrollHeight - container.scrollTop - container.clientHeight) < 50;",
			null
		)
		assert isScrolledToBottom :
			"Expected the chat to be scrolled to the latest message, but it was not"
	}
}
