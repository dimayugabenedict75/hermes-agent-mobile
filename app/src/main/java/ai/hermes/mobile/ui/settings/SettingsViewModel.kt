package ai.hermes.mobile.ui.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ai.hermes.mobile.data.BackendConfig

class SettingsViewModel : ViewModel() {
    private val _baseUrl = mutableStateOf(BackendConfig.baseUrl)
    val baseUrl: State<String> = _baseUrl

    fun updateBaseUrl(url: String) {
        _baseUrl.value = url
        BackendConfig.baseUrl = url.trim()
    }
}
