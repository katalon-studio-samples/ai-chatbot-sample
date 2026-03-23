package com.katalon.chatbot

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

/**
 * Keywords for handling streaming responses from the AI chatbot.
 * Provides methods for monitoring response generation, measuring latency,
 * and controlling the streaming process.
 */
class StreamingKeywords {

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
	 * Waits for the thinking indicator to become visible with the "visible" CSS class.
	 * Polls the DOM until the thinking indicator element has the "visible" class applied.
	 *
	 * @param timeoutSeconds maximum seconds to wait (default 10)
	 */
	@Keyword
	def void waitForThinkingIndicator(int timeoutSeconds = 10) {
		long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L)

		while (System.currentTimeMillis() < deadline) {
			Boolean hasVisibleClass = (Boolean) WebUI.executeJavaScript(
				"var el = document.querySelector('[data-testid=\"thinking-indicator\"]'); " +
				"return el != null && el.classList.contains('visible');",
				null
			)
			if (hasVisibleClass) {
				return
			}
			Thread.sleep(200)
		}

		throw new Exception("Thinking indicator did not become visible within ${timeoutSeconds} seconds")
	}

	/**
	 * Waits for the response to fully complete by polling until both the thinking
	 * indicator and the stop button are no longer visible.
	 *
	 * @param timeoutSeconds maximum seconds to wait (default 30)
	 */
	@Keyword
	def void waitForResponseComplete(int timeoutSeconds = 30) {
		long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L)

		while (System.currentTimeMillis() < deadline) {
			if (!isGenerating()) {
				return
			}
			Thread.sleep(300)
		}

		throw new Exception("Response did not complete within ${timeoutSeconds} seconds")
	}

	/**
	 * Checks whether the chatbot is currently generating a response.
	 * Returns true if either the thinking indicator is visible or the stop button is visible.
	 *
	 * @return true if the chatbot is actively generating a response
	 */
	@Keyword
	def boolean isGenerating() {
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
		return (thinkingVisible == true) || (stopVisible == true)
	}

	/**
	 * Stops the current response generation by clicking the stop button
	 * and waiting for it to disappear.
	 */
	@Keyword
	def void stopGeneration() {
		TestObject stopButton = makeTestObject('stop-button')
		WebUI.click(stopButton)
		WebUI.waitForElementNotVisible(stopButton, 10)
	}

	/**
	 * Measures the response latency for a given message. Sends the message and
	 * records the elapsed time in milliseconds until the response is fully complete.
	 *
	 * @param message the message to send to the chatbot
	 * @param timeoutSeconds maximum seconds to wait for the response (default 30)
	 * @return the response latency in milliseconds
	 */
	@Keyword
	def long measureResponseLatency(String message, int timeoutSeconds = 30) {
		ChatbotKeywords chatbotKeywords = new ChatbotKeywords()

		long startTime = System.currentTimeMillis()
		chatbotKeywords.sendMessageNoWait(message)
		waitForResponseComplete(timeoutSeconds)
		long endTime = System.currentTimeMillis()

		long latencyMs = endTime - startTime
		WebUI.comment("Response latency for message '${message}': ${latencyMs}ms")
		return latencyMs
	}

	/**
	 * Waits until the last assistant message reaches a minimum content length.
	 * Useful for verifying that streaming output has begun producing visible text.
	 *
	 * @param minLength the minimum number of characters expected in the response
	 * @param timeoutSeconds maximum seconds to wait (default 15)
	 */
	@Keyword
	def void waitForPartialResponse(int minLength, int timeoutSeconds = 15) {
		ChatbotKeywords chatbotKeywords = new ChatbotKeywords()
		long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L)

		while (System.currentTimeMillis() < deadline) {
			String content = chatbotKeywords.getLastAssistantMessage()
			if (content != null && content.length() >= minLength) {
				return
			}
			Thread.sleep(300)
		}

		throw new Exception("Partial response did not reach ${minLength} characters within ${timeoutSeconds} seconds")
	}
}
