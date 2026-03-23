import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Send "explain" without waiting (triggers long streaming response)
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageNoWait'('explain')

// Wait 1 second to allow partial generation
Thread.sleep(1000)

// Click stop to halt generation mid-stream
CustomKeywords.'com.katalon.chatbot.StreamingKeywords.stopGeneration'()

// Get the partial response
String partialResponse = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getLastAssistantMessage'()

// Verify response is partial (present but shorter than a full response would be)
assert partialResponse != null : 'Expected a partial response after stopping generation'
WebUI.comment("Partial response length: ${partialResponse.length()}")

// Now send the same message and let it complete fully for comparison
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.resetConversation'()
String fullResponse = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('explain')

// Verify the stopped response is shorter than the full response
assert partialResponse.length() < fullResponse.length() : "Expected partial response (${partialResponse.length()} chars) to be shorter than full response (${fullResponse.length()} chars)"

WebUI.comment('Stop Generation Mid-Stream test passed.')
