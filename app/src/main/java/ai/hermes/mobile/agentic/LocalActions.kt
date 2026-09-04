package ai.hermes.mobile.agentic

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.documentfile.provider.DocumentFile

object LocalActions {
    fun launchApp(context: Context, packageName: String): Boolean =
        runCatching {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else false
        }.getOrElse { false }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openQuickSettings() {
        // Opens the quick settings panel via status bar expansion.
        // No public API; requires system UI interaction, so leave as placeholder.
    }

    fun createDocument(context: Context, mimeType: String, suggestedName: String): Uri? =
        runCatching {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = mimeType
                putExtra(Intent.EXTRA_TITLE, suggestedName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            null
        }.getOrNull()

    fun pickDocument(context: Context, mimeType: String): Uri? =
        runCatching {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = mimeType
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            null
        }.getOrNull()
}
