package ai.hermes.mobile.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import ai.hermes.mobile.data.BackendConfig
import ai.hermes.mobile.data.Identity
import ai.hermes.mobile.data.HermesApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

@Composable
fun ChatScreen() {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val input = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val client = remember { OkHttpClient() }
    val identityLoaded = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Identity.load(LocalContext.current ?: return@LaunchedEffect)
        identityLoaded.value = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages) { msg ->
                MessageBubble(message = msg)
            }
        }

        TextField(
            value = input.value,
            onValueChange = { input.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text(text = if (!identityLoaded.value) "Loading..." else "Ask Lucy") },
            enabled = identityLoaded.value,
            keyboardOptions = KeyboardOptions(autoCorrect = false),
            singleLine = true
        )
    }
}
