package com.example.composeproject.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.composeproject.ui.common.OverlayBadge
import com.example.composeproject.ui.common.TextOverlayBadge
import com.example.composeproject.ui.theme.LocalCustomColors
import com.example.composeproject.utils.mdp
import com.example.composeproject.utils.msp

@Composable
fun OverlayBadgeExampleScreen(
    modifier: Modifier = Modifier
) {
    var showBadgeA by remember { mutableStateOf(false) }
    var showBadgeB by remember { mutableStateOf(false) }
    var showBadgeC by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.mdp, vertical = 20.mdp),
        verticalArrangement = Arrangement.spacedBy(8.mdp)
    ) {
        Text(
            text = "標籤浮於元件上方，切換時上下元件位置不會移動",
            style = TextStyle(
                fontSize = 14.msp,
                color = LocalCustomColors.current.darkBlue500,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.mdp))

        // 並排兩張卡片
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.mdp)
        ) {
            BadgeCard(
                modifier = Modifier.weight(1f),
                label = "商品 A",
                badgeText = "NEW",
                showBadge = showBadgeA,
                onToggle = { showBadgeA = !showBadgeA }
            )
            BadgeCard(
                modifier = Modifier.weight(1f),
                label = "商品 B",
                badgeText = "HOT",
                showBadge = showBadgeB,
                onToggle = { showBadgeB = !showBadgeB }
            )
        }

        // 全寬卡片，帶 marginStart 偏移
        BadgeCard(
            modifier = Modifier.fillMaxWidth(),
            label = "商品 C（badge 帶水平偏移）",
            badgeText = "SALE",
            showBadge = showBadgeC,
            onToggle = { showBadgeC = !showBadgeC },
            badgeMarginStart = 20.mdp.value.toInt()
        )
    }
}

@Composable
private fun BadgeCard(
    modifier: Modifier = Modifier,
    label: String,
    badgeText: String,
    showBadge: Boolean,
    onToggle: () -> Unit,
    badgeMarginStart: Int = 0
) {
    val colors = LocalCustomColors.current
    Box(modifier = modifier) {
        OverlayBadge(
            badgeContent = if (showBadge) {
                { TextOverlayBadge(text = badgeText) }
            } else null,
            marginStart = badgeMarginStart.mdp,
            distanceTheTopEdge = (-9).mdp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.blueGray300, RoundedCornerShape(8.mdp))
                    .border(1.dp, colors.darkBlue30, RoundedCornerShape(8.mdp))
                    .padding(12.mdp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.mdp)
            ) {
                Text(
                    text = label,
                    style = TextStyle(
                        fontSize = 16.msp,
                        fontWeight = FontWeight.Medium,
                        color = colors.darkBlue800
                    )
                )

                Button(onClick = onToggle) {
                    Text(if (showBadge) "隱藏標籤" else "顯示標籤")
                }
            }
        }
    }
}
