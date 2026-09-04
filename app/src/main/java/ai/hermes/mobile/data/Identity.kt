package ai.hermes.mobile.data

import android.content.Context

object Identity {
    var botName: String = "Lucy"
    var userName: String = "Benny"
    var systemPrompt: String = ""
        private set

    fun load(context: Context) {
        systemPrompt = readAsset(context, "soul.md") ?: ""
    }

    private fun readAsset(context: Context, name: String): String? =
        runCatching {
            context.assets.open(name).bufferedReader().use { it.readText() }
        }.getOrNull()
}
