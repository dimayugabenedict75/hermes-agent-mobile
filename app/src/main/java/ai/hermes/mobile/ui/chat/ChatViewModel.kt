package ai.hermes.mobile.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ai.hermes.mobile.data.BackendConfig
import ai.hermes.mobile.data.DatabaseProvider
import ai.hermes.mobile.data.LocalMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val client = OkHttpClient()
    private val dao = ai.hermes.mobile.LucyMobileApp.getInstance().database.messageDao()
    private val sessionId = "default"

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _messages.value = dao.forSession(sessionId).map {
                ChatMessage(it.id, it.sessionId, it.role, it.content, it.timestamp)
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userMessage = ChatMessage(role = "user", content = text)
        _messages.value = _messages.value + userMessage
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(LocalMessage(sessionId = sessionId, role = "user", content = text))
        }
        streamReply(text)
    }

    private fun streamReply(userText: String) {
        val history = _messages.value
        val assistantMessage = ChatMessage(role = "assistant", content = "")
        _messages.value = history + assistantMessage

        val payload = JSONObject().apply {
            put("model", "Lucy-MOE")
            put("stream", true)
            put("messages", history.map { mapOf("role" to it.role, "content" to it.content) } + mapOf("role" to "user", "content" to userText))
            put("max_tokens", 1024)
            put("temperature", 0.7)
        }.toString().toRequestBody()

        val request = Request.Builder()
            .url("${BackendConfig.baseUrl}/v1/chat/completions")
            .post(payload)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Hermes-Session-Id", sessionId)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                _messages.value = _messages.value.dropLast(1) + ChatMessage(role = "assistant", content = "Error: ${response.code}")
                return
            }

            val reader = BufferedReader(InputStreamReader(response.body!!.byteStream()))
            var fullText = ""
            var line: String? = reader.readLine()
            while (line != null) {
                if (line.startsWith("data: ") && !line.endsWith("[DONE]")) {
                    val data = line.removePrefix("data: ").trim()
                    try {
                        val json = JSONObject(data)
                        val delta = json.getJSONArray("choices").getJSONObject(0).getJSONObject("delta")
                        val content = delta.optString("content", "")
                        if (content.isNotEmpty()) {
                            fullText += content
                            _messages.value = _messages.value.dropLast(1) + ChatMessage(role = "assistant", content = fullText)
                        }
                    } catch (_: Exception) {
                        // ignore partial SSE parse failures
                    }
                }
                line = reader.readLine()
            }

            if (fullText.isNotEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.insert(LocalMessage(sessionId = sessionId, role = "assistant", content = fullText))
                }
            }
        }
    }
}
