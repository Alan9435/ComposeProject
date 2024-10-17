package com.example.composeproject.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import kotlin.math.roundToInt

/**
 * 偏移 x,y (可當滑入 滑出效果)
 * */
fun Modifier.offsetPercent(offsetPercentX: Float = 0f, offsetPercentY: Float = 0f) =
    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints) // 測量自己
        val offsetX = (offsetPercentX * placeable.width).roundToInt()
        val offsetY = (offsetPercentY * placeable.height).roundToInt()
        layout(placeable.width, placeable.height) {// 該layout多高 多寬
            placeable.placeRelative(offsetX, offsetY) // 位置偏移
        }
    }