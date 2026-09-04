package ai.hermes.mobile.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ai.hermes.mobile.agentic.LocalActions
import ai.hermes.mobile.ui.settings.SettingsScreen

@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel(), settingsViewModel: SettingsViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    var input by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }
    var showQuickActions by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsScreen(viewModel = settingsViewModel, onBack = { showSettings = false })
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { showSettings = true }) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Default.Settings, contentDescription = "Settings")
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages) { msg ->
                MessageBubble(message = msg)
            }
        }

        if (showQuickActions) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = {
                    val ctx = androidx.compose.ui.platform.LocalContext.current
                    LocalActions.openAppSettings(ctx)
                    showQuickActions = false
                }) {
                    Icon(imageVector = androidx.compose.material.icons.Icons.Default.Settings, contentDescription = "App settings")
                }
                IconButton(onClick = {
                    val ctx = androidx.compose.ui.platform.LocalContext.current
                    LocalActions.pickDocument(ctx, "*/*")
                    showQuickActions = false
                }) {
                    Icon(imageVector = androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Pick document")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { showQuickActions = !showQuickActions }) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Quick action")
            }
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask Lucy") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(autoCorrect = false),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (input.isNotBlank()) {
                            viewModel.sendMessage(input.trim())
                            input = ""
                        }
                    }
                )
            )
            IconButton(onClick = {
                if (input.isNotBlank()) {
                    viewModel.sendMessage(input.trim())
                    input = ""
                }
            }) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}
