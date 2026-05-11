package com.example.composeproject.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.composeproject.ui.theme.LocalCustomColors
import com.example.composeproject.utils.mdp
import com.example.composeproject.utils.msp

@Composable
fun OverlayBadge(
    badgeContent: (@Composable () -> Unit)? = null,
    marginStart: Dp = 0.dp,
    distanceTheTopEdge: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    // 保存最後一次非 null 的 badge 內容，讓淡出動畫期間仍可繪製
    var lastBadgeContent by remember { mutableStateOf(badgeContent) }
    if (badgeContent != null) lastBadgeContent = badgeContent

    // 用 alpha 動畫取代 AnimatedVisibility：
    // AnimatedVisibility 動畫結束時會把自身 Layout 節點移除，
    // 導致 measurables 只剩 1 個造成 IndexOutOfBoundsException。
    // graphicsLayer alpha 則讓 Box 始終留在 Layout 樹中（永遠 2 個 measurables）。
    val alpha by animateFloatAsState(
        targetValue = if (badgeContent != null) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "badgeAlpha"
    )

    Layout(
        content = {
            content()
            Box(Modifier.graphicsLayer { this.alpha = alpha }) {
                lastBadgeContent?.invoke()
            }
        }
    ) { measurables, constraints ->
        val contentPlaceable: Placeable = measurables[0].measure(constraints)
        val badgePlaceable: Placeable = measurables[1].measure(Constraints())

        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
            badgePlaceable.place(
                x = marginStart.roundToPx(),
                y = -distanceTheTopEdge.roundToPx() - badgePlaceable.height
            )
        }
    }
}

@Composable
fun TextOverlayBadge(
    text: String = "NEW",
    fontSize: TextUnit = 11.msp,
    textColor: Color = LocalCustomColors.current.white,
    backgroundColor: Color = LocalCustomColors.current.pinkRed600,
) {
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(9.mdp)
            )
            .padding(horizontal = 4.mdp, vertical = 1.mdp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            color = textColor,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOverlayBadge_Text() {
    Box(modifier = Modifier.padding(40.mdp)) {
        OverlayBadge(
            badgeContent = { TextOverlayBadge(text = "NEW") }
        ) {
            Text("商品名稱")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOverlayBadge_WithOffset() {
    Box(modifier = Modifier.padding(40.mdp)) {
        OverlayBadge(
            badgeContent = { TextOverlayBadge(text = "HOT") },
            marginStart = 8.mdp,
            distanceTheTopEdge = 4.mdp
        ) {
            Text("商品名稱（偏移）")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOverlayBadge_NoBadge() {
    Box(modifier = Modifier.padding(40.mdp)) {
        OverlayBadge(badgeContent = null) {
            Text("沒有標籤")
        }
    }
}
