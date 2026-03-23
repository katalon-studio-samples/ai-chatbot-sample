package com.katalon.chatbot

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

/**
 * Keywords for interacting with the AI chatbot interface.
 * Provides methods for sending messages, reading responses, and managing conversations.
 */
class ChatbotKeywords {

	/**
	 * Creates a TestObject using a data-testid attribute selector.
	 *
	 * @param testId the value of the data-testid attribute
	 * @return a TestObject configured to locate elements by data-testid
	 */
	private static TestObject makeTestObject(String testId) {
		TestObject obj = new TestObject(testId)
		obj.addProperty('data-testid', ConditionType.EQUALS, testId)
		return obj
	}

	/**
	 * Sends a message to the chatbot and waits for the full response to complete.
	 * Types the message into the chat input, clicks the send button, waits for the
	 * thinking indicator to appear and then disappear, and waits for the stop button
	 * to disappear before returning the last assistant message.
	 *
	 * @param message the message text to send
	 * @param timeoutSeconds maximum seconds to wait for a response (default 30)
	 * @return the text content of the last assistant message
	 */
	@Keyword
	def String sendMessageAndWaitForResponse(String message, int timeoutSeconds = 30) {
		TestObject chatInput = makeTestObject('chat-input')
		TestObject sendButton = makeTestObject('send-button')
		TestObject thinkingIndicator = makeTestObject('thinking-indicator')
		TestObject stopButton = makeTestObject('stop-button')

		WebUI.setText(chatInput, message)
		WebUI.click(sendButton)

		// Poll until thinking indicator and stop button are both gone
		long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L)
		// Brief wait for generation to start
		Thread.sleep(300)
		while (System.currentTimeMillis() < deadline) {
			Boolean thinkingVisible = (Boolean) WebUI.executeJavaScript(
				"var el = document.querySelector('[data-testid=\"thinking-indicator\"]'); " +
				"return el != null && el.classList.contains('visible');",
				null
			)
			Boolean stopVisible = (Boolean) WebUI.executeJavaScript(
				"var el = document.querySelector('[data-testid=\"stop-button\"]'); " +
				"return el != null && el.classList.contains('visible');",
				null
			)
			if (!thinkingVisible && !stopVisible) break
			Thread.sleep(GlobalVariable.STREAMING_POLL_INTERVAL)
		}

		return getLastAssistantMessage()
	}

	/**
	 * Sends a message to the chatbot without waiting for a response.
	 * Types the message into the chat input and clicks the send button immediately.
	 *
	 * @param message the message text to send
	 */
	@Keyword
	def void sendMessageNoWait(String message) {
		TestObject chatInput = makeTestObject('chat-input')
		TestObject sendButton = makeTestObject('send-button')

		WebUI.setText(chatInput, message)
		WebUI.click(sendButton)
	}

	/**
	 * Gets the text content of a message at the specified index.
	 *
	 * @param index the zero-based index of the message
	 * @return the text content of the message
	 */
	@Keyword
	def String getMessageContent(int index) {
		TestObject messageContent = makeTestObject("message-${index}-content")
		return WebUI.getText(messageContent)
	}

	/**
	 * Gets the role (e.g. "user", "assistant") of a message at the specified index.
	 *
	 * @param index the zero-based index of the message
	 * @return the role text of the message
	 */
	@Keyword
	def String getMessageRole(int index) {
		TestObject messageRole = makeTestObject("message-${index}-role")
		return WebUI.getText(messageRole)
	}

	/**
	 * Counts the total number of messages currently displayed in the chat.
	 * Uses JavaScript to query all elements whose data-testid matches the message pattern.
	 *
	 * @return the number of messages in the conversation
	 */
	@Keyword
	def int getMessageCount() {
		int count = (int) WebUI.executeJavaScript(
			"return document.querySelectorAll('[data-testid^=\"message-\"][data-testid\$=\"-content\"]').length",
			null
		)
		return count
	}

	/**
	 * Retrieves the text content of the last message with role "assistant".
	 * Iterates through messages in reverse order to find the most recent assistant response.
	 *
	 * @return the text content of the last assistant message, or empty string if none found
	 */
	@Keyword
	def String getLastAssistantMessage() {
		int count = getMessageCount()
		for (int i = count - 1; i >= 0; i--) {
			String role = getMessageRole(i)
			if (role.toLowerCase().contains('assistant')) {
				return getMessageContent(i)
			}
		}
		return ''
	}

	/**
	 * Resets the conversation by clicking the new chat button and verifying
	 * that the conversation has been cleared.
	 */
	@Keyword
	def void resetConversation() {
		TestObject newChatButton = makeTestObject('new-chat-button')
		WebUI.click(newChatButton)

		// Verify conversation is cleared
		int count = getMessageCount()
		assert count == 0 : "Expected conversation to be cleared but found ${count} messages"
	}

	/**
	 * Checks whether the chat input field is currently enabled and accepting input.
	 *
	 * @return true if the chat input is enabled, false otherwise
	 */
	@Keyword
	def boolean isChatInputEnabled() {
		TestObject chatInput = makeTestObject('chat-input')
		boolean disabled = (boolean) WebUI.executeJavaScript(
			"return document.querySelector('[data-testid=\"chat-input\"]').disabled",
			null
		)
		return !disabled
	}
}
