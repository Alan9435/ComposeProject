package com.example.composeproject.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.example.composeproject.R
import com.example.composeproject.ui.theme.LocalCustomColors

/**
 * 創建一個由左至右移動的 Shimmer 漸層效果。 並覆蓋在想
 * @param shimmerColor 光影顏色
 * @param durationMillis 動畫時長
 * @param shimmerWidth 光影長度
 * @param tiltFactor 光影傾斜係數
 * @sample com.example.composeproject.utils.RememberShimmerBrushSample
 */
@Composable
fun rememberShimmerBrush(
    shimmerColor: Color = LocalCustomColors.current.white,
    durationMillis: Int = 2000,
    shimmerWidth: Dp = 50.mdp,
    tiltFactor: Float = 1.5f
): Brush {
    val density = LocalDensity.current
    val shimmerWidthPx = with(density) { shimmerWidth.toPx() }

    val transition = rememberInfiniteTransition(label = "ShimmerTransition")

    // 動畫值從 -1.0f (左邊界外) 到 2.0f (右邊界外)
    val animatedValue: Float by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "animatedValue"
    )

    // 漸層顏色：[透明 -> 光線 -> 透明]
    val colors = listOf(
        Color.Transparent,
        shimmerColor.copy(alpha = 0.9f),
        Color.Transparent,
    )

    // 根據動畫值計算 Shimmer 漸層的位置
    val translationX = animatedValue * (LocalDensity.current.density * 500) // 放大移動範圍
    val yOffset = shimmerWidthPx * tiltFactor

    return Brush.linearGradient(
        colors = colors,
        start = Offset(translationX - shimmerWidthPx / 2, -yOffset),
        end = Offset(translationX + shimmerWidthPx / 2, yOffset)
    )
}

@Composable
fun RememberShimmerBrushSample() {
    val shimmerBrush = rememberShimmerBrush()

    Box(
        modifier = Modifier
    ) {
        // current weight
        Image(
            painter = painterResource(R.drawable.img_test),
            contentDescription = ""
        )

        // shimmer
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            drawRect(
                brush = shimmerBrush
            )
        }
    }
}