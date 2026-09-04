package ai.hermes.mobile.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ai.hermes.mobile.data.BackendConfig

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val url = remember { mutableStateOf(BackendConfig.baseUrl) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Backend")
        OutlinedTextField(
            value = url.value,
            onValueChange = { url.value = it },
            label = { androidx.compose.material3.Text("Base URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                BackendConfig.baseUrl = url.value.trim()
                onBack()
            },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            androidx.compose.material3.Text("Save")
        }
    }
}
