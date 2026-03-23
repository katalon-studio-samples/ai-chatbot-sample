/**
 * AI Chatbot Simulator
 * A deterministic chatbot simulator for Katalon Studio testing.
 * Responds to trigger phrases with predictable behavior.
 */
const app = (function () {
  // State
  let messages = [];
  let conversationMemory = {};
  let isGenerating = false;
  let currentStreamAbort = null;
  let chatHistory = []; // array of { id, title, messages }
  let uploadedFileName = null;
  let sidebarOpen = false;

  // DOM refs
  const $messages = document.getElementById('messages');
  const $input = document.getElementById('chat-input');
  const $sendBtn = document.getElementById('btn-send');
  const $stopBtn = document.getElementById('btn-stop');
  const $thinking = document.getElementById('thinking-indicator');
  const $error = document.getElementById('error-message');
  const $errorContent = document.getElementById('error-content');
  const $chips = document.getElementById('suggestion-chips');
  const $sidebar = document.getElementById('sidebar');
  const $sidebarList = document.getElementById('sidebar-list');
  const $fileDisplay = document.getElementById('file-display');

  // ---- Markdown rendering (simple) ----
  function renderMarkdown(text) {
    let html = text;
    // Code blocks (fenced)
    html = html.replace(/```(\w*)\n([\s\S]*?)```/g, function (_, lang, code) {
      return '<pre><code class="language-' + lang + '">' + escapeHtml(code.trim()) + '</code></pre>';
    });
    // Inline code
    html = html.replace(/`([^`]+)`/g, '<code>$1</code>');
    // Bold
    html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');
    // Italic
    html = html.replace(/\*(.+?)\*/g, '<em>$1</em>');
    // Links
    html = html.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank" rel="noopener">$1</a>');
    // Unordered lists
    html = html.replace(/^- (.+)$/gm, '<li>$1</li>');
    html = html.replace(/((?:<li>.*<\/li>\n?)+)/g, '<ul>$1</ul>');
    // Paragraphs (double newline)
    html = html.replace(/\n\n/g, '</p><p>');
    html = '<p>' + html + '</p>';
    // Clean up empty paragraphs
    html = html.replace(/<p>\s*<\/p>/g, '');
    return html;
  }

  function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  // ---- Response generators ----
  function getResponse(userText) {
    const lower = userText.toLowerCase().trim();

    if (lower.includes('error:rate_limit')) {
      return { type: 'error', error: 'Rate limit exceeded (429). Please wait a moment and try again.', delay: 500 };
    }
    if (lower.includes('error:timeout')) {
      return { type: 'error', error: 'Request timed out. The server took too long to respond.', delay: 30000 };
    }
    if (lower.includes('error:server')) {
      return { type: 'error', error: 'Internal server error (500). Something went wrong on our end.', delay: 800 };
    }

    if (userText.length > 10000) {
      return {
        type: 'text',
        text: 'Your message is quite long (' + userText.length + ' characters). I\'ve received it, but please note that very long messages may be truncated in processing. Here\'s a brief acknowledgment of your input.',
        delay: 200,
        streamDelay: 30
      };
    }

    if (lower.includes('hello') || lower.includes('hi')) {
      const name = conversationMemory.userName;
      const greeting = name
        ? 'Hello ' + name + '! How can I help you today?'
        : 'Hello! How can I help you today?';
      return { type: 'text', text: greeting, delay: 200, streamDelay: 30 };
    }

    if (lower.includes('explain')) {
      return {
        type: 'text',
        text: '**Understanding Software Testing**\n\nSoftware testing is a *critical* process in the software development lifecycle. It involves evaluating a system or its components to verify that they satisfy specified requirements.\n\n**Key Types of Testing:**\n\n- **Unit Testing**: Testing individual components or functions in isolation\n- **Integration Testing**: Testing how components work together\n- **End-to-End Testing**: Testing complete user workflows\n- **Performance Testing**: Evaluating system behavior under load\n\nHere\'s a simple example of a unit test:\n\n```python\ndef test_addition():\n    result = add(2, 3)\n    assert result == 5, f"Expected 5, got {result}"\n```\n\nTesting helps ensure *quality*, *reliability*, and *confidence* in your software. It catches bugs early, reduces maintenance costs, and provides documentation of expected behavior.\n\nFor more information, consider exploring frameworks like `pytest`, `JUnit`, or `Katalon Studio` for comprehensive test automation.',
        delay: 500,
        streamDelay: 20
      };
    }

    if (lower.includes('code example')) {
      return {
        type: 'text',
        text: 'Here\'s a practical code example:\n\n```javascript\n// Fibonacci sequence generator\nfunction fibonacci(n) {\n  if (n <= 1) return n;\n  let a = 0, b = 1;\n  for (let i = 2; i <= n; i++) {\n    [a, b] = [b, a + b];\n  }\n  return b;\n}\n\n// Usage\nfor (let i = 0; i < 10; i++) {\n  console.log(`F(${i}) = ${fibonacci(i)}`);\n}\n```\n\nThis function computes the nth Fibonacci number efficiently using an iterative approach with `O(n)` time complexity.',
        delay: 400,
        streamDelay: 15
      };
    }

    if (lower.includes('show sources')) {
      return {
        type: 'text',
        text: 'Here are some relevant sources on this topic:\n\nAccording to recent research, AI testing methodologies have evolved significantly. The key findings include:\n\n- Automated testing reduces regression bugs by 40% [Source 1](https://example.com/testing-research)\n- Continuous integration practices improve release quality [Source 2](https://example.com/ci-practices)\n- BDD frameworks enhance team collaboration [Source 3](https://example.com/bdd-benefits)\n\nThese sources provide comprehensive coverage of modern testing practices.',
        delay: 400,
        streamDelay: 20
      };
    }

    if (lower.includes('follow up')) {
      return {
        type: 'text',
        text: 'That\'s a great topic! I can help you explore this further. Here are some directions we could go:',
        delay: 300,
        streamDelay: 25,
        suggestions: [
          'Tell me more about unit testing',
          'How do I set up CI/CD?',
          'What are best practices for test automation?'
        ]
      };
    }

    if (lower.includes('remember my name is ')) {
      const match = userText.match(/remember my name is (\w+)/i);
      if (match) {
        conversationMemory.userName = match[1];
        return {
          type: 'text',
          text: 'Got it! I\'ll remember that your name is ' + match[1] + '. Nice to meet you!',
          delay: 300,
          streamDelay: 25
        };
      }
    }

    if (lower.includes('what is my name')) {
      const name = conversationMemory.userName;
      if (name) {
        return {
          type: 'text',
          text: 'Your name is ' + name + '! You told me earlier in our conversation.',
          delay: 300,
          streamDelay: 25
        };
      } else {
        return {
          type: 'text',
          text: 'I don\'t know your name yet. You haven\'t told me! You can say "remember my name is X" to let me know.',
          delay: 300,
          streamDelay: 25
        };
      }
    }

    if (lower.includes('upload test')) {
      if (uploadedFileName) {
        return {
          type: 'text',
          text: 'I received your uploaded file: **' + uploadedFileName + '**. I\'ve processed the file and it looks good. The file has been acknowledged successfully.',
          delay: 400,
          streamDelay: 25
        };
      } else {
        return {
          type: 'text',
          text: 'It looks like you haven\'t uploaded a file yet. Please use the attachment button to upload a file first, then ask me about it.',
          delay: 300,
          streamDelay: 25
        };
      }
    }

    // Default response
    return {
      type: 'text',
      text: 'Thank you for your message. I\'ve received your input and I\'m here to help. Could you provide more details about what you\'d like to know?',
      delay: 300,
      streamDelay: 25
    };
  }

  // ---- Rendering ----
  function renderAllMessages() {
    $messages.innerHTML = '';
    messages.forEach(function (msg, i) {
      appendMessageToDOM(msg, i);
    });
    scrollToBottom();
  }

  function appendMessageToDOM(msg, index) {
    const div = document.createElement('div');
    div.className = 'message';
    div.setAttribute('data-testid', 'message-' + index);

    const roleDiv = document.createElement('div');
    roleDiv.className = 'message-role';
    roleDiv.setAttribute('data-testid', 'message-' + index + '-role');
    roleDiv.textContent = msg.role;
    div.appendChild(roleDiv);

    const contentDiv = document.createElement('div');
    contentDiv.className = 'message-content';
    contentDiv.setAttribute('data-testid', 'message-' + index + '-content');
    if (msg.role === 'assistant') {
      contentDiv.innerHTML = renderMarkdown(msg.text);
      // Add copy buttons to code blocks
      contentDiv.querySelectorAll('pre').forEach(function (pre) {
        const btn = document.createElement('button');
        btn.className = 'copy-code-btn';
        btn.setAttribute('data-testid', 'message-' + index + '-copy-code');
        btn.textContent = 'Copy';
        btn.onclick = function () {
          const code = pre.querySelector('code').textContent;
          navigator.clipboard.writeText(code).then(function () {
            btn.textContent = 'Copied!';
            setTimeout(function () { btn.textContent = 'Copy'; }, 2000);
          });
        };
        pre.appendChild(btn);
      });
    } else {
      contentDiv.textContent = msg.text;
    }
    div.appendChild(contentDiv);

    // Feedback buttons for assistant messages
    if (msg.role === 'assistant') {
      const actions = document.createElement('div');
      actions.className = 'message-actions';

      const thumbsUp = document.createElement('button');
      thumbsUp.setAttribute('data-testid', 'message-' + index + '-thumbsup');
      thumbsUp.textContent = '\uD83D\uDC4D';
      thumbsUp.onclick = function () {
        thumbsUp.classList.toggle('selected');
        thumbsDown.classList.remove('selected');
      };

      const thumbsDown = document.createElement('button');
      thumbsDown.setAttribute('data-testid', 'message-' + index + '-thumbsdown');
      thumbsDown.textContent = '\uD83D\uDC4E';
      thumbsDown.onclick = function () {
        thumbsDown.classList.toggle('selected');
        thumbsUp.classList.remove('selected');
      };

      actions.appendChild(thumbsUp);
      actions.appendChild(thumbsDown);
      div.appendChild(actions);
    }

    $messages.appendChild(div);
  }

  function showThinking() {
    $thinking.classList.add('visible');
    scrollToBottom();
  }

  function hideThinking() {
    $thinking.classList.remove('visible');
  }

  function showError(text) {
    $errorContent.textContent = text;
    $error.classList.add('visible');
    scrollToBottom();
  }

  function hideError() {
    $error.classList.remove('visible');
  }

  function showStopButton() {
    $stopBtn.classList.add('visible');
  }

  function hideStopButton() {
    $stopBtn.classList.remove('visible');
  }

  function showSuggestions(suggestions) {
    $chips.innerHTML = '';
    suggestions.forEach(function (text, i) {
      const chip = document.createElement('button');
      chip.className = 'suggestion-chip';
      chip.setAttribute('data-testid', 'suggestion-chip-' + i);
      chip.textContent = text;
      chip.onclick = function () {
        $chips.innerHTML = '';
        $input.value = text;
        sendMessage();
      };
      $chips.appendChild(chip);
    });
  }

  function clearSuggestions() {
    $chips.innerHTML = '';
  }

  function scrollToBottom() {
    $messages.scrollTop = $messages.scrollHeight;
  }

  function setInputEnabled(enabled) {
    $input.disabled = !enabled;
    $sendBtn.disabled = !enabled;
  }

  // ---- Streaming simulation ----
  function streamText(fullText, streamDelay, index) {
    return new Promise(function (resolve) {
      let aborted = false;
      const words = fullText.split(/(\s+)/);
      let currentIndex = 0;
      let accumulated = '';

      currentStreamAbort = function () {
        aborted = true;
      };

      function nextChunk() {
        if (aborted || currentIndex >= words.length) {
          resolve(aborted ? accumulated : fullText);
          return;
        }

        // Add 2-4 words at a time for natural streaming
        const chunkSize = Math.floor(Math.random() * 3) + 2;
        for (let j = 0; j < chunkSize && currentIndex < words.length; j++) {
          accumulated += words[currentIndex];
          currentIndex++;
        }

        // Update the message in state and DOM
        messages[messages.length - 1].text = accumulated;
        const contentEl = document.querySelector('[data-testid="message-' + index + '-content"]');
        if (contentEl) {
          contentEl.innerHTML = renderMarkdown(accumulated);
          // Re-add copy buttons
          contentEl.querySelectorAll('pre').forEach(function (pre) {
            if (!pre.querySelector('.copy-code-btn')) {
              const btn = document.createElement('button');
              btn.className = 'copy-code-btn';
              btn.setAttribute('data-testid', 'message-' + index + '-copy-code');
              btn.textContent = 'Copy';
              btn.onclick = function () {
                const code = pre.querySelector('code').textContent;
                navigator.clipboard.writeText(code).then(function () {
                  btn.textContent = 'Copied!';
                  setTimeout(function () { btn.textContent = 'Copy'; }, 2000);
                });
              };
              pre.appendChild(btn);
            }
          });
        }
        scrollToBottom();

        setTimeout(nextChunk, streamDelay);
      }

      nextChunk();
    });
  }

  // ---- Public API ----
  function sendMessage() {
    const text = $input.value;

    // Block empty/whitespace
    if (!text || !text.trim()) {
      return;
    }

    hideError();
    clearSuggestions();

    // Add user message
    const userIndex = messages.length;
    messages.push({ role: 'user', text: text });
    appendMessageToDOM(messages[userIndex], userIndex);
    scrollToBottom();

    $input.value = '';
    $input.style.height = 'auto';

    // Get response config
    const response = getResponse(text);

    isGenerating = true;
    setInputEnabled(false);
    showThinking();
    showStopButton();

    if (response.type === 'error') {
      setTimeout(function () {
        hideThinking();
        hideStopButton();
        isGenerating = false;
        setInputEnabled(true);
        showError(response.error);
      }, response.delay);
      return;
    }

    // Simulate initial latency, then stream
    setTimeout(function () {
      hideThinking();

      const assistantIndex = messages.length;
      messages.push({ role: 'assistant', text: '' });
      appendMessageToDOM(messages[assistantIndex], assistantIndex);

      streamText(response.text, response.streamDelay || 20, assistantIndex).then(function () {
        hideStopButton();
        isGenerating = false;
        setInputEnabled(true);
        currentStreamAbort = null;

        // Re-render final message to ensure proper markdown + buttons
        const contentEl = document.querySelector('[data-testid="message-' + assistantIndex + '-content"]');
        if (contentEl) {
          contentEl.innerHTML = renderMarkdown(messages[assistantIndex].text);
          // Add copy buttons
          contentEl.querySelectorAll('pre').forEach(function (pre) {
            if (!pre.querySelector('.copy-code-btn')) {
              const btn = document.createElement('button');
              btn.className = 'copy-code-btn';
              btn.setAttribute('data-testid', 'message-' + assistantIndex + '-copy-code');
              btn.textContent = 'Copy';
              btn.onclick = function () {
                const code = pre.querySelector('code').textContent;
                navigator.clipboard.writeText(code).then(function () {
                  btn.textContent = 'Copied!';
                  setTimeout(function () { btn.textContent = 'Copy'; }, 2000);
                });
              };
              pre.appendChild(btn);
            }
          });
        }

        // Add feedback buttons to the just-completed message
        const msgEl = document.querySelector('[data-testid="message-' + assistantIndex + '"]');
        if (msgEl && !msgEl.querySelector('.message-actions')) {
          const actions = document.createElement('div');
          actions.className = 'message-actions';
          const thumbsUp = document.createElement('button');
          thumbsUp.setAttribute('data-testid', 'message-' + assistantIndex + '-thumbsup');
          thumbsUp.textContent = '\uD83D\uDC4D';
          const thumbsDown = document.createElement('button');
          thumbsDown.setAttribute('data-testid', 'message-' + assistantIndex + '-thumbsdown');
          thumbsDown.textContent = '\uD83D\uDC4E';
          thumbsUp.onclick = function () {
            thumbsUp.classList.toggle('selected');
            thumbsDown.classList.remove('selected');
          };
          thumbsDown.onclick = function () {
            thumbsDown.classList.toggle('selected');
            thumbsUp.classList.remove('selected');
          };
          actions.appendChild(thumbsUp);
          actions.appendChild(thumbsDown);
          msgEl.appendChild(actions);
        }

        // Show suggestions if any
        if (response.suggestions) {
          showSuggestions(response.suggestions);
        }

        scrollToBottom();
      });
    }, response.delay);
  }

  function stopGeneration() {
    if (currentStreamAbort) {
      currentStreamAbort();
      currentStreamAbort = null;
    }
    hideThinking();
    hideStopButton();
    isGenerating = false;
    setInputEnabled(true);
  }

  function resetConversation() {
    // Save current conversation to history if it has messages
    if (messages.length > 0) {
      const firstUserMsg = messages.find(function (m) { return m.role === 'user'; });
      chatHistory.push({
        id: Date.now(),
        title: firstUserMsg ? firstUserMsg.text.substring(0, 40) : 'New conversation',
        messages: JSON.parse(JSON.stringify(messages))
      });
      renderSidebar();
    }

    messages = [];
    conversationMemory = {};
    uploadedFileName = null;
    isGenerating = false;
    currentStreamAbort = null;
    $messages.innerHTML = '';
    hideThinking();
    hideError();
    hideStopButton();
    clearSuggestions();
    $fileDisplay.textContent = '';
    setInputEnabled(true);
  }

  function toggleSidebar() {
    sidebarOpen = !sidebarOpen;
    if (sidebarOpen) {
      $sidebar.classList.remove('hidden');
    } else {
      $sidebar.classList.add('hidden');
    }
  }

  function renderSidebar() {
    $sidebarList.innerHTML = '';
    chatHistory.forEach(function (conv, i) {
      const item = document.createElement('div');
      item.className = 'sidebar-item';
      item.setAttribute('data-testid', 'chat-history-item-' + i);
      item.textContent = conv.title;
      item.onclick = function () {
        loadConversation(conv);
      };
      $sidebarList.appendChild(item);
    });
  }

  function loadConversation(conv) {
    messages = JSON.parse(JSON.stringify(conv.messages));
    // Restore memory from conversation
    conversationMemory = {};
    messages.forEach(function (m) {
      if (m.role === 'user') {
        const match = m.text.match(/remember my name is (\w+)/i);
        if (match) conversationMemory.userName = match[1];
      }
    });
    renderAllMessages();
    hideError();
    clearSuggestions();
    setInputEnabled(true);
  }

  function handleFileUpload(event) {
    const file = event.target.files[0];
    if (file) {
      uploadedFileName = file.name;
      $fileDisplay.textContent = 'Attached: ' + file.name;
    }
  }

  function handleKeyDown(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      sendMessage();
    }
  }

  function autoResize(el) {
    el.style.height = 'auto';
    el.style.height = el.scrollHeight + 'px';
  }

  // Public interface
  return {
    sendMessage: sendMessage,
    stopGeneration: stopGeneration,
    resetConversation: resetConversation,
    toggleSidebar: toggleSidebar,
    handleFileUpload: handleFileUpload,
    handleKeyDown: handleKeyDown,
    autoResize: autoResize,
    // Expose for testing
    getMessages: function () { return messages; },
    isGenerating: function () { return isGenerating; }
  };
})();
