package ai.hermes.mobile.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ai.hermes.mobile.ui.theme.BennyGray
import ai.hermes.mobile.ui.theme.DarkSurface
import ai.hermes.mobile.ui.theme.LucyCyan

@Composable
fun MessageBubble(message: ChatMessage, modifier: Modifier = Modifier) {
    val isLucy = message.role == "assistant"
    val backgroundColor = if (isLucy) LucyCyan else BennyGray
    val alignment = if (isLucy) Alignment.Start else Alignment.End

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = if (isLucy) Arrangement.Start else Arrangement.End
        ) {
            Text(
                text = message.content,
                color = if (isLucy) androidx.compose.ui.graphics.Color.Black else androidx.compose.ui.graphics.Color.White
            )
        }
    }
}
