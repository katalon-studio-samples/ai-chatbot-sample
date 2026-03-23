package com.katalon.chatbot

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

/**
 * Keywords for managing the local HTTP server that serves the chatbot web application.
 * Provides methods to start, stop, and query the server used during test execution.
 */
class ServerKeywords {

	/** Reference to the running server process. */
	private static Process serverProcess = null

	/**
	 * Starts a Python HTTP server serving files from the Include/webapp directory
	 * on the port specified by GlobalVariable.CHATBOT_PORT.
	 * Kills any existing process on the port before starting.
	 *
	 * @return the base URL of the running server (e.g. "http://localhost:3456")
	 */
	@Keyword
	def String startServer() {
		int port = (int) GlobalVariable.CHATBOT_PORT

		// If we already have a running process, just return
		if (serverProcess != null && serverProcess.isAlive()) {
			WebUI.comment('Server is already running.')
			return getServerUrl()
		}

		// Kill any existing process on this port (from a previous run)
		try {
			String[] killCmd = ['bash', '-c', "lsof -ti:${port} | xargs kill -9 2>/dev/null || true"]
			Runtime.getRuntime().exec(killCmd).waitFor()
			Thread.sleep(500)
		} catch (Exception e) {
			// Ignore — port may already be free
		}

		String projectDir = RunConfiguration.getProjectDir()
		String webappDir = projectDir + File.separator + 'Include' + File.separator + 'webapp'

		ProcessBuilder processBuilder = new ProcessBuilder(
			'python3', '-m', 'http.server', String.valueOf(port)
		)
		processBuilder.directory(new File(webappDir))
		processBuilder.redirectErrorStream(true)

		serverProcess = processBuilder.start()

		// Wait for the server to be ready, polling with retries
		String baseUrl = getServerUrl()
		boolean ready = false
		for (int i = 0; i < 10; i++) {
			Thread.sleep(500)
			if (serverProcess != null && !serverProcess.isAlive()) {
				throw new Exception('HTTP server process exited with code: ' + serverProcess.exitValue())
			}
			if (isServerRunning()) {
				ready = true
				break
			}
		}

		if (!ready) {
			throw new Exception('HTTP server started but not responding at ' + baseUrl + ' after 5 seconds')
		}

		WebUI.comment("Server started at ${baseUrl} serving from ${webappDir}")
		return baseUrl
	}

	/**
	 * Stops the running HTTP server process.
	 * Destroys the process and waits briefly for it to terminate.
	 */
	@Keyword
	def void stopServer() {
		if (serverProcess != null) {
			serverProcess.destroyForcibly()
			serverProcess.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)
			serverProcess = null
			WebUI.comment('Server stopped.')
		} else {
			WebUI.comment('No server process to stop.')
		}
	}

	/**
	 * Checks whether the server is currently running and responding.
	 * Uses a raw TCP socket connection to bypass any JVM proxy settings.
	 *
	 * @return true if the server responds successfully, false otherwise
	 */
	@Keyword
	def boolean isServerRunning() {
		try {
			int port = (int) GlobalVariable.CHATBOT_PORT
			Socket socket = new Socket()
			socket.connect(new InetSocketAddress('127.0.0.1', port), 2000)
			socket.close()
			return true
		} catch (Exception e) {
			return false
		}
	}

	/**
	 * Returns the base URL of the server based on the configured port.
	 *
	 * @return the server URL string (e.g. "http://localhost:3456")
	 */
	@Keyword
	def String getServerUrl() {
		int port = (int) GlobalVariable.CHATBOT_PORT
		return "http://localhost:${port}"
	}
}
