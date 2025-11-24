package com.example.composeproject.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.zIndex
import com.example.composeproject.ui.modifier.notRippleClickable
import ir.kaaveh.sdpcompose.sdp

@Composable
fun LoadingMask(modifier: Modifier = Modifier, show: Boolean) {
    if (!show) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent) // 可以改帶透明度的灰色
            .zIndex(Float.MAX_VALUE) // 放到最上層避免點擊
            .notRippleClickable {

            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = modifier
                .clip(
                    shape = RoundedCornerShape(10.sdp)
                )
                .background(Color.White)
                .padding(10.sdp)
        ) {
            CircularProgressIndicator(
                modifier = modifier.size(38.sdp),
                color = Color.Red,
                strokeCap = StrokeCap.Round // 尖端的形狀
            )
        }
    }
}