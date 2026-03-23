import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Send first message and wait for response
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('hello')

// Verify message 0 is from user
String role0 = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageRole'(0)
assert role0.toLowerCase().contains('user') : "Expected message 0 role to be 'user', but got '${role0}'"

// Verify message 1 is from assistant
String role1 = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageRole'(1)
assert role1.toLowerCase().contains('assistant') : "Expected message 1 role to be 'assistant', but got '${role1}'"

// Send another message
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.sendMessageAndWaitForResponse'('how are you')

// Verify ordering continues: message 2 is user, message 3 is assistant
String role2 = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageRole'(2)
assert role2.toLowerCase().contains('user') : "Expected message 2 role to be 'user', but got '${role2}'"

String role3 = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageRole'(3)
assert role3.toLowerCase().contains('assistant') : "Expected message 3 role to be 'assistant', but got '${role3}'"

// Verify total message count is 4
int messageCount = CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.getMessageCount'()
assert messageCount == 4 : "Expected 4 messages, but found ${messageCount}"

WebUI.comment('Verify Message Ordering test passed.')
