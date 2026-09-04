package ai.hermes.mobile.data

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming

interface HermesApi {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    @Streaming
    suspend fun chat(
        @Body request: ChatRequest,
        @Header("X-Hermes-Session-Id") sessionId: String? = null
    ): ResponseBody

    @GET("v1/health")
    suspend fun health(): HealthResponse
}

data class ChatRequest(
    val model: String = "Lucy-MOE",
    val messages: List<Message>,
    val stream: Boolean = true,
    val max_tokens: Int = 1024,
    val temperature: Double = 0.7
)

data class Message(
    val role: String,
    val content: String
)

data class HealthResponse(
    val status: String
)
