package com.example.composeproject.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

//todo 試著做出紅點含數字
fun Modifier.unread(show: Boolean, color: Color): Modifier = this.drawWithContent {
    // 內部並非一個 Composable範圍 所以無法使用Compose元件
    drawContent()
    if (show) {
        drawCircle(
            color = color,
            radius = 5.dp.toPx(),
            center = Offset(size.width - 1.dp.toPx(), 1.dp.toPx()) // x軸, y軸
        )
    }
}