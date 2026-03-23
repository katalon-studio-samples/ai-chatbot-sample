package com.katalon.chatbot

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

/**
 * Keywords for asserting chatbot response content and formatting.
 * Provides methods for validating text content, regex patterns, markdown rendering,
 * code blocks, and citation links within chat messages.
 */
class AssertionKeywords {

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
	 * Asserts that the response contains the expected substring (case-insensitive).
	 *
	 * @param response the full response text
	 * @param expected the expected substring to find
	 */
	@Keyword
	def void assertResponseContains(String response, String expected) {
		assert response.toLowerCase().contains(expected.toLowerCase()) :
			"Expected response to contain '${expected}' (case-insensitive), but it did not.\nResponse: ${response}"
	}

	/**
	 * Asserts that the response matches the given regular expression pattern.
	 *
	 * @param response the full response text
	 * @param pattern the regex pattern to match against
	 */
	@Keyword
	def void assertResponseMatchesPattern(String response, String pattern) {
		assert response ==~ pattern :
			"Expected response to match pattern '${pattern}', but it did not.\nResponse: ${response}"
	}

	/**
	 * Asserts that the response does not contain the specified forbidden text (case-insensitive).
	 *
	 * @param response the full response text
	 * @param forbidden the text that must not be present
	 */
	@Keyword
	def void assertResponseDoesNotContain(String response, String forbidden) {
		assert !response.toLowerCase().contains(forbidden.toLowerCase()) :
			"Expected response NOT to contain '${forbidden}' (case-insensitive), but it did.\nResponse: ${response}"
	}

	/**
	 * Asserts that the response length is within the specified range (inclusive).
	 *
	 * @param response the full response text
	 * @param minLength the minimum acceptable length
	 * @param maxLength the maximum acceptable length
	 */
	@Keyword
	def void assertResponseLengthInRange(String response, int minLength, int maxLength) {
		int length = response.length()
		assert length >= minLength && length <= maxLength :
			"Expected response length to be between ${minLength} and ${maxLength}, but was ${length}.\nResponse: ${response}"
	}

	/**
	 * Asserts that a message at the given index contains the specified markdown element type
	 * when rendered as HTML. Inspects the innerHTML of the message content element.
	 *
	 * Supported element types:
	 * - bold: checks for strong or b tags
	 * - italic: checks for em or i tags
	 * - code_inline: checks for code tags (not inside pre)
	 * - code_block: checks for pre > code tags
	 * - link: checks for a tags
	 * - list: checks for ul or ol tags
	 *
	 * @param messageIndex the zero-based index of the message
	 * @param elementType the type of markdown element to look for
	 */
	@Keyword
	def void assertMessageContainsMarkdown(int messageIndex, String elementType) {
		String innerHTML = (String) WebUI.executeJavaScript(
			"return document.querySelector('[data-testid=\"message-${messageIndex}-content\"]').innerHTML;",
			null
		)

		boolean found = false
		switch (elementType.toLowerCase()) {
			case 'bold':
				found = innerHTML.contains('<strong') || innerHTML.contains('<b>')
				break
			case 'italic':
				found = innerHTML.contains('<em') || innerHTML.contains('<i>')
				break
			case 'code_inline':
				found = innerHTML.contains('<code') && !innerHTML.contains('<pre')
				break
			case 'code_block':
				found = innerHTML.contains('<pre') && innerHTML.contains('<code')
				break
			case 'link':
				found = innerHTML.contains('<a ')
				break
			case 'list':
				found = innerHTML.contains('<ul') || innerHTML.contains('<ol')
				break
			default:
				throw new Exception("Unknown markdown element type: ${elementType}")
		}

		assert found :
			"Expected message at index ${messageIndex} to contain markdown element '${elementType}', but it was not found.\nInnerHTML: ${innerHTML}"
	}

	/**
	 * Asserts that a code block within the message at the given index contains
	 * the expected code substring.
	 *
	 * @param messageIndex the zero-based index of the message
	 * @param expectedCodeSubstring the code text expected within a code block
	 */
	@Keyword
	def void assertCodeBlockContains(int messageIndex, String expectedCodeSubstring) {
		String codeContent = (String) WebUI.executeJavaScript(
			"var el = document.querySelector('[data-testid=\"message-${messageIndex}-content\"] pre code'); " +
			"return el ? el.textContent : '';",
			null
		)

		assert codeContent.contains(expectedCodeSubstring) :
			"Expected code block at message index ${messageIndex} to contain '${expectedCodeSubstring}', but it did not.\nCode block content: ${codeContent}"
	}

	/**
	 * Asserts that the message at the given index contains at least the specified
	 * minimum number of citation links (anchor tags).
	 *
	 * @param messageIndex the zero-based index of the message
	 * @param minLinks the minimum number of anchor tags expected
	 */
	@Keyword
	def void assertCitationLinksPresent(int messageIndex, int minLinks) {
		int linkCount = (int) WebUI.executeJavaScript(
			"return document.querySelectorAll('[data-testid=\"message-${messageIndex}-content\"] a').length;",
			null
		)

		assert linkCount >= minLinks :
			"Expected at least ${minLinks} citation link(s) in message at index ${messageIndex}, but found ${linkCount}."
	}
}
