import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Measure latency for a simple "hello" message (should be < 5000ms)
long helloLatency = CustomKeywords.'com.katalon.chatbot.StreamingKeywords.measureResponseLatency'('hello', 10)
WebUI.comment("Hello latency: ${helloLatency}ms")
assert helloLatency < 5000 : "Expected hello latency < 5000ms, but was ${helloLatency}ms"

// Reset conversation for next measurement
CustomKeywords.'com.katalon.chatbot.ChatbotKeywords.resetConversation'()

// Measure latency for a longer "explain" message (should be < 15000ms)
long explainLatency = CustomKeywords.'com.katalon.chatbot.StreamingKeywords.measureResponseLatency'('explain', 20)
WebUI.comment("Explain latency: ${explainLatency}ms")
assert explainLatency < 15000 : "Expected explain latency < 15000ms, but was ${explainLatency}ms"

WebUI.comment("Measure Response Latency test passed. Hello: ${helloLatency}ms, Explain: ${explainLatency}ms")
