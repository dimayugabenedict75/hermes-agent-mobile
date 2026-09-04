package ai.hermes.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ai.hermes.mobile.data.Identity

private val DarkColorScheme = darkColorScheme(
    primary = LucyCyan,
    surface = DarkSurface,
    background = DarkBackground,
    onPrimary = Color.Black,
    onSurface = Color.White,
    onBackground = Color.White
)

@Composable
fun HermesAgentMobileTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
