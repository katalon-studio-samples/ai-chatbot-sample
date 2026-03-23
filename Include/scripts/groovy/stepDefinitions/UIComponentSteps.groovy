package stepDefinitions

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType

import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.java.en.Then

class UIComponentSteps {

	private String chipText = ''

	private static TestObject makeTestObject(String testId) {
		TestObject obj = new TestObject(testId)
		obj.addProperty('data-testid', ConditionType.EQUALS, testId)
		return obj
	}

	// --- Markdown rendering steps ---

	@Then("the response should contain rendered bold text")
	def the_response_should_contain_rendered_bold_text() {
		int lastAssistantIndex = getLastAssistantMessageIndex()
		CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertMessageContainsMarkdown'(lastAssistantIndex, 'bold')
	}

	@Then("the response should contain rendered italic text")
	def the_response_should_contain_rendered_italic_text() {
		int lastAssistantIndex = getLastAssistantMessageIndex()
		CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertMessageContainsMarkdown'(lastAssistantIndex, 'italic')
	}

	@Then("the response should contain a rendered list")
	def the_response_should_contain_a_rendered_list() {
		int lastAssistantIndex = getLastAssistantMessageIndex()
		CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertMessageContainsMarkdown'(lastAssistantIndex, 'list')
	}

	// --- Code block steps ---

	@Then("the response should contain a rendered code block")
	def the_response_should_contain_a_rendered_code_block() {
		int lastAssistantIndex = getLastAssistantMessageIndex()
		CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertMessageContainsMarkdown'(lastAssistantIndex, 'code_block')
	}

	@Then("the code block should have a copy button")
	def the_code_block_should_have_a_copy_button() {
		int lastAssistantIndex = getLastAssistantMessageIndex()
		Boolean hasCopyBtn = (Boolean) WebUI.executeJavaScript(
			"return document.querySelector('[data-testid=\"message-${lastAssistantIndex}-copy-code\"]') !== null;",
			null
		)
		assert hasCopyBtn : "Expected code block to have a copy button, but none found"
	}

	@When("I click the copy code button")
	def i_click_the_copy_code_button() {
		int lastAssistantIndex = getLastAssistantMessageIndex()
		TestObject copyButton = makeTestObject("message-${lastAssistantIndex}-copy-code")
		WebUI.click(copyButton)
	}

	@Then("the clipboard should contain the code content")
	def the_clipboard_should_contain_the_code_content() {
		// Verify the copy button changed text to "Copied!" as confirmation
		int lastAssistantIndex = getLastAssistantMessageIndex()
		Boolean copyConfirmed = (Boolean) WebUI.executeJavaScript(
			"var btn = document.querySelector('[data-testid=\"message-${lastAssistantIndex}-copy-code\"]'); " +
			"return btn != null && btn.textContent.toLowerCase().includes('copied');",
			null
		)
		assert copyConfirmed :
			"Expected copy button to confirm clipboard copy (show 'Copied!'), but it did not"
	}

	// --- Citation links steps ---

	@Then("the response should contain at least {int} citation links")
	def the_response_should_contain_at_least_citation_links(int count) {
		int lastAssistantIndex = getLastAssistantMessageIndex()
		CustomKeywords.'com.katalon.chatbot.AssertionKeywords.assertCitationLinksPresent'(lastAssistantIndex, count)
	}

	@Then("each citation link should have an href attribute")
	def each_citation_link_should_have_an_href_attribute() {
		int lastAssistantIndex = getLastAssistantMessageIndex()
		Boolean allHaveHref = (Boolean) WebUI.executeJavaScript(
			"var links = document.querySelectorAll('[data-testid=\"message-${lastAssistantIndex}-content\"] a'); " +
			"if (links.length === 0) return false; " +
			"for (var i = 0; i < links.length; i++) { " +
			"  if (!links[i].getAttribute('href')) return false; " +
			"} " +
			"return true;",
			null
		)
		assert allHaveHref :
			"Expected all citation links to have an href attribute, but at least one was missing"
	}

	// --- Feedback buttons steps ---

	@Then("the assistant message should have thumbs up and thumbs down buttons")
	def the_assistant_message_should_have_thumbs_up_and_thumbs_down_buttons() {
		int lastAssistantIndex = getLastAssistantMessageIndex()
		Boolean hasThumbsUp = (Boolean) WebUI.executeJavaScript(
			"return document.querySelector('[data-testid=\"message-${lastAssistantIndex}-thumbsup\"]') !== null;",
			null
		)
		Boolean hasThumbsDown = (Boolean) WebUI.executeJavaScript(
			"return document.querySelector('[data-testid=\"message-${lastAssistantIndex}-thumbsdown\"]') !== null;",
			null
		)
		assert hasThumbsUp : "Expected thumbs up button on assistant message, but none found"
		assert hasThumbsDown : "Expected thumbs down button on assistant message, but none found"
	}

	@When("I click the thumbs up button on message {int}")
	def i_click_the_thumbs_up_button_on_message(int index) {
		TestObject thumbsUp = makeTestObject("message-${index}-thumbsup")
		WebUI.click(thumbsUp)
	}

	@Then("the thumbs up button should show as selected")
	def the_thumbs_up_button_should_show_as_selected() {
		int lastAssistantIndex = getLastAssistantMessageIndex()
		Boolean isSelected = (Boolean) WebUI.executeJavaScript(
			"var btn = document.querySelector('[data-testid=\"message-${lastAssistantIndex}-thumbsup\"]'); " +
			"return btn != null && btn.classList.contains('selected');",
			null
		)
		assert isSelected :
			"Expected thumbs up button to show as selected, but it was not"
	}

	// --- Suggestion chips steps ---

	@Then("at least {int} suggestion chips should be visible")
	def at_least_suggestion_chips_should_be_visible(int count) {
		int chipCount = (int) WebUI.executeJavaScript(
			"return document.querySelectorAll('[data-testid^=\"suggestion-chip-\"]').length;",
			null
		)
		assert chipCount >= count :
			"Expected at least ${count} suggestion chips, but found ${chipCount}"
	}

	@When("I click the first suggestion chip")
	def i_click_the_first_suggestion_chip() {
		chipText = (String) WebUI.executeJavaScript(
			"var chip = document.querySelector('[data-testid=\"suggestion-chip-0\"]'); " +
			"return chip ? chip.textContent.trim() : '';",
			null
		)
		TestObject firstChip = makeTestObject('suggestion-chip-0')
		WebUI.click(firstChip)
	}

	@Then("a new user message should be sent with the chip text")
	def a_new_user_message_should_be_sent_with_the_chip_text() {
		// Wait for the message to be sent and response to start
		CustomKeywords.'com.katalon.chatbot.StreamingKeywords.waitForResponseComplete'(30)
		int count = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
		// Find the last user message
		String lastUserMessage = ''
		for (int i = count - 1; i >= 0; i--) {
			String role = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageRole'(i)
			if (role.toLowerCase().contains('user')) {
				lastUserMessage = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageContent'(i)
				break
			}
		}
		assert chipText.length() > 0 : "Chip text was not captured before clicking"
		assert lastUserMessage.toLowerCase().contains(chipText.toLowerCase()) :
			"Expected new user message to contain chip text '${chipText}', but got: ${lastUserMessage}"
	}

	@Then("I should receive a response")
	def i_should_receive_a_response() {
		String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
		assert response != null && response.length() > 0 :
			"Expected to receive a response, but the last assistant message was empty"
	}

	// --- File upload steps ---

	@When("I click the upload button")
	def i_click_the_upload_button() {
		TestObject uploadButton = makeTestObject('upload-button')
		WebUI.click(uploadButton)
	}

	@When("I select a file named {string}")
	def i_select_a_file_named(String filename) {
		WebUI.executeJavaScript(
			"var input = document.querySelector('[data-testid=\"upload-input\"]'); " +
			"if (input) { " +
			"  var dt = new DataTransfer(); " +
			"  var file = new File(['test content'], '" + filename + "', { type: 'text/plain' }); " +
			"  dt.items.add(file); " +
			"  input.files = dt.files; " +
			"  input.dispatchEvent(new Event('change', { bubbles: true })); " +
			"}",
			null
		)
	}

	@Then("the file name should be displayed in the chat")
	def the_file_name_should_be_displayed_in_the_chat() {
		Boolean fileDisplayed = (Boolean) WebUI.executeJavaScript(
			"var el = document.getElementById('file-display'); " +
			"return el != null && el.textContent.trim().length > 0;",
			null
		)
		assert fileDisplayed :
			"Expected file name to be displayed in the chat, but no file display was found"
	}

	@Then("the response should acknowledge the uploaded file")
	def the_response_should_acknowledge_the_uploaded_file() {
		String response = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()
		boolean acknowledged = response.toLowerCase().contains('file') ||
			response.toLowerCase().contains('document') ||
			response.toLowerCase().contains('upload')
		assert acknowledged :
			"Expected response to acknowledge the uploaded file, but got: ${response}"
	}

	// --- Chat history sidebar steps ---

	@When("I toggle the chat history sidebar")
	def i_toggle_the_chat_history_sidebar() {
		TestObject historyToggle = makeTestObject('chat-history-toggle')
		WebUI.click(historyToggle)
	}

	@Then("the sidebar should be visible")
	def the_sidebar_should_be_visible() {
		Boolean visible = (Boolean) WebUI.executeJavaScript(
			"var el = document.getElementById('sidebar'); " +
			"return el != null && !el.classList.contains('hidden');",
			null
		)
		assert visible : "Expected sidebar to be visible, but it was hidden"
	}

	@Then("there should be at least {int} past conversation in the history")
	def there_should_be_at_least_past_conversations_in_the_history(int count) {
		int historyCount = (int) WebUI.executeJavaScript(
			"return document.querySelectorAll('[data-testid^=\"chat-history-item-\"]').length;",
			null
		)
		assert historyCount >= count :
			"Expected at least ${count} past conversation(s) in history, but found ${historyCount}"
	}

	@When("I click on the first history item")
	def i_click_on_the_first_history_item() {
		TestObject firstItem = makeTestObject('chat-history-item-0')
		WebUI.click(firstItem)
	}

	@Then("the conversation should load with the previous messages")
	def the_conversation_should_load_with_the_previous_messages() {
		Thread.sleep(500)
		int messageCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
		assert messageCount > 0 :
			"Expected conversation to load with previous messages, but found ${messageCount} messages"
	}

	// --- Helper methods ---

	private int getLastAssistantMessageIndex() {
		int count = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
		for (int i = count - 1; i >= 0; i--) {
			String role = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageRole'(i)
			if (role.toLowerCase().contains('assistant')) {
				return i
			}
		}
		throw new Exception("No assistant message found in conversation")
	}
}
