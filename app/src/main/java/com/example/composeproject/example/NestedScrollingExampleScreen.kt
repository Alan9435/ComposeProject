package com.example.composeproject.example

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.example.composeproject.ui.theme.LocalCustomColors
import com.example.composeproject.utils.mdp
import com.example.composeproject.utils.msp

@Composable
fun NestedScrollingExampleScreen(
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val expandThresholdPx = with(LocalDensity.current) { 60.dp.toPx() }

    var isCollapsed by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        var prevIndex = 0
        var prevOffset = 0
        var upwardAccumulation = 0f // 臨界值-滑動多少要收起來
        // snapshotFlow 大誇號內的數值改變時 發出一筆資料
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val isScrollingDown = index > prevIndex || (index == prevIndex && offset > prevOffset)
                val isScrollingUp = index < prevIndex || (index == prevIndex && offset < prevOffset)

                if (isScrollingDown) {
                    upwardAccumulation = 0f
                    isCollapsed = true
                } else if (isScrollingUp) {
                    // 每個項目多高
                    val itemHeight = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0
                    // 位移了幾個項目的距離
                    val indexDelta = (prevIndex - index) * itemHeight
                    // 同個item中位移的距離
                    val offsetDelta = if (index == prevIndex) prevOffset - offset else 0
                    upwardAccumulation += indexDelta + offsetDelta

                    if (upwardAccumulation >= expandThresholdPx) {
                        isCollapsed = false
                        upwardAccumulation = 0f
                    }
                }

                prevIndex = index
                prevOffset = offset
            }
    }

    val collapseRatio by animateFloatAsState(
        targetValue = if (isCollapsed) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "collapseRatio"
    )

    Column(modifier = modifier.fillMaxSize()) {
        CollapsingHeader(collapseRatio = collapseRatio)
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(50) { index ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.mdp, vertical = 12.mdp),
                    text = "列表項目 ${index + 1}",
                    style = TextStyle(
                        fontSize = 16.msp,
                        color = LocalCustomColors.current.darkBlue700
                    )
                )
                HorizontalDivider(
                    color = LocalCustomColors.current.dividerLineColor
                )
            }
        }
    }
}

@Composable
private fun CollapsingHeader(
    collapseRatio: Float,
    modifier: Modifier = Modifier
) {
    val colors = LocalCustomColors.current
    val tagsExpandedHeight = 48.mdp
    val tagsHeight = lerp(tagsExpandedHeight, 0.mdp, collapseRatio)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.white)
    ) {
        // 搜尋列
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.mdp, vertical = 8.mdp)
                .background(colors.blueGray300, RoundedCornerShape(8.mdp))
                .padding(horizontal = 12.mdp, vertical = 10.mdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🔍", fontSize = 16.msp)
            Spacer(modifier = Modifier.width(8.mdp))
            Text(
                text = "搜尋... (demo)",
                style = TextStyle(
                    fontSize = 15.msp,
                    color = colors.darkBlue200
                )
            )
        }

        // 分類標籤列
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(tagsHeight)
                .clipToBounds()
                .graphicsLayer { alpha = 1f - collapseRatio }
                .padding(horizontal = 16.mdp),
            horizontalArrangement = Arrangement.spacedBy(8.mdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val categories = remember { listOf("全部", "美食", "旅遊", "科技", "娛樂") }
            categories.forEach { label ->
                CategoryChip(label = label)
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String) {
    val colors = LocalCustomColors.current
    Box(
        modifier = Modifier
            .background(
                color = colors.blueGray400,
                shape = RoundedCornerShape(20.mdp)
            )
            .padding(horizontal = 12.mdp, vertical = 6.mdp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 13.msp,
                fontWeight = FontWeight.Medium,
                color = colors.darkBlue700
            )
        )
    }
}
