package ai.hermes.mobile.ui.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ai.hermes.mobile.data.BackendConfig
import ai.hermes.mobile.data.DatabaseProvider
import ai.hermes.mobile.data.LocalMessage
import kotlinx.coroutines.Dispatchers
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
    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private val client = OkHttpClient()
    private val dao = DatabaseProvider.get(androidx.compose.ui.platform.LocalContext.current.applicationContext).messageDao()
    private val sessionId = "default"

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dao.forSession(sessionId).forEach { legacy ->
                _messages += ChatMessage(legacy.id, legacy.sessionId, legacy.role, legacy.content, legacy.timestamp)
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userMessage = ChatMessage(role = "user", content = text)
        _messages += userMessage
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(LocalMessage(sessionId = sessionId, role = "user", content = text))
        }
        streamReply(text)
    }

    private fun streamReply(userText: String) {
        val history = _messages.toList()
        val assistantMessage = ChatMessage(role = "assistant", content = "")
        _messages += assistantMessage

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
                _messages[_messages.lastIndex] = ChatMessage(role = "assistant", content = "Error: ${response.code}")
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
                            _messages[_messages.lastIndex] = ChatMessage(role = "assistant", content = fullText)
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
