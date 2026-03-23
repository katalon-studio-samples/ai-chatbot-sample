import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

// Start the local chatbot web server
String serverUrl = CustomKeywords.'com.katalon.chatbot.ServerKeywords.startServer'()
WebUI.comment("Server started at: ${serverUrl}")

// Verify the server is running and responding
boolean running = CustomKeywords.'com.katalon.chatbot.ServerKeywords.isServerRunning'()
assert running : 'Expected server to be running after startServer()'

WebUI.comment('Start Local Server test passed.')
