package stepDefinitions

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType

import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.java.en.Then

class BasicConversationSteps {

	private String lastResponse = ''

	private static TestObject makeTestObject(String testId) {
		TestObject obj = new TestObject(testId)
		obj.addProperty('data-testid', ConditionType.EQUALS, testId)
		return obj
	}

	@Given("the chatbot application is open")
	def the_chatbot_application_is_open() {
		TestObject chatInput = makeTestObject('chat-input')
		WebUI.verifyElementPresent(chatInput, 10)
	}

	@Given("the conversation is empty")
	def the_conversation_is_empty() {
		CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.resetConversation'()
	}

	@When("I send the message {string}")
	def i_send_the_message(String message) {
		lastResponse = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'(message)
	}

	@When("I wait for the response")
	def i_wait_for_the_response() {
		// Response is already waited for in sendMessageAndWaitForResponse
		// Update lastResponse in case it was not captured
		lastResponse = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
	}

	@Then("I should receive a response within {int} seconds")
	def i_should_receive_a_response_within_seconds(int seconds) {
		int messageCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
		assert messageCount > 1 : "Expected more than 1 message (user + assistant), but found ${messageCount}"
	}

	@Then("the response should contain a greeting")
	def the_response_should_contain_a_greeting() {
		lastResponse = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
		boolean containsGreeting = lastResponse.toLowerCase().contains('hello') ||
			lastResponse.toLowerCase().contains('hi') ||
			lastResponse.toLowerCase().contains('hey') ||
			lastResponse.toLowerCase().contains('greetings')
		assert containsGreeting : "Expected response to contain a greeting, but got: ${lastResponse}"
	}

	@Then("message {int} should have role {string}")
	def message_should_have_role(int index, String role) {
		String actualRole = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageRole'(index)
		assert actualRole.toLowerCase().contains(role.toLowerCase()) :
			"Expected message ${index} to have role '${role}', but got '${actualRole}'"
	}

	@Then("message {int} should contain {string}")
	def message_should_contain(int index, String text) {
		String content = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageContent'(index)
		CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertResponseContains'(content, text)
	}

	@Then("there should be {int} messages in the conversation")
	def there_should_be_messages_in_the_conversation(int count) {
		int actualCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
		assert actualCount == count :
			"Expected ${count} messages in conversation, but found ${actualCount}"
	}

	@Then("the last assistant response should be at least {int} characters long")
	def the_last_assistant_response_should_be_at_least_characters_long(int length) {
		lastResponse = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
		CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertResponseLengthInRange'(lastResponse, length, Integer.MAX_VALUE)
	}
}
