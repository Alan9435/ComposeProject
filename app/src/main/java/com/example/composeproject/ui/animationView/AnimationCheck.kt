package com.example.composeproject.ui.animationView

import android.util.Log
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.delay

/**
 * 打勾動畫
 * @param modifier = Box() { Canvas() {} }
 * @param starAnimation 是否開始動畫
 * @param size 勾勾大小 通常=此modifier的大小 (請換算成sdp) 要使用正常dp再把*0.8f拔掉
 * @param color 勾勾顏色
 * @param lineWidth 線條寬度
 * */
@Composable
fun AnimationCheck(modifier: Modifier = Modifier, starAnimation: Boolean, size: Dp, color: Color, lineWidth: Dp) {
    if (!starAnimation) return

    val density = LocalDensity.current
    val path = Path()
    // 畫完整個勾勾的時間 (millisecond)
    val animationSpeed = 1500

    var step1 by remember {
        mutableStateOf(false)
    }

    var step2 by remember {
        mutableStateOf(false)
    }

    with(density) {
        // 定義第1,2筆畫的起點與終點  * 0.8f 是因為用sdp套件
        val step1XStartLocation = ((size / 4) * 0.8f).toPx()
        val step1XEndLocation = ((size / 2.1f) * 0.8f).toPx()
        val step1YStartLocation = ((size / 1.5f) * 0.8f).toPx()
        val step1YEndLocation = ((size / 1.1f) * 0.8f).toPx()
        val step2XStartLocation = ((size / 2.1f) * 0.8f).toPx()
        val step2XEndLocation = (size * 0.8f).toPx()
        val step2YStartLocation = ((size / 1.1f) * 0.8f).toPx()
        val step2YEndLocation = ((size / 2.7f) * 0.8f).toPx()
        val checkWidth = lineWidth.toPx()

        val step1X = updateTransition(targetState = step1, label = "step1X")
        val animationStep1X: Float by step1X.animateFloat(
            transitionSpec = {
                tween(animationSpeed / 2)
            },
            label = "animationStep2X"
        ) {
            if (it) {
                step1XEndLocation
            } else {
                step1XStartLocation
            }
        }

        val step1Y = updateTransition(targetState = step1, label = "step1Y")
        val animationStep1Y: Float by step1Y.animateFloat(
            transitionSpec = {
                tween(animationSpeed / 2)
            },
            label = "animationStep2X"
        ) {
            if (it) {
                step1YEndLocation
            } else {
                step1YStartLocation
            }
        }

        val step2X = updateTransition(targetState = step2, label = "step2")

        val animationStep2X: Float by step2X.animateFloat(
            transitionSpec = {
                tween(animationSpeed / 2)
            },
            label = "animationStep2X"
        ) {
            if (it) {
                step2XEndLocation
            } else {
                step2XStartLocation
            }
        }

        val step2Y = updateTransition(targetState = step2, label = "step2")
        val animationStep2Y: Float by step2Y.animateFloat(
            transitionSpec = {
                tween(animationSpeed / 2)
            },
            label = "animationStep2Y"
        ) {
            if (it) {
                step2YEndLocation
            } else {
                step2YStartLocation
            }
        }

        LaunchedEffect(true) {
            step1 = true
            delay(animationSpeed.toLong() / 2) // 直接delay 第一段動畫時間才繪製第二筆
            step2 = true
        }

        // 定義繪製路徑
        path.apply {
            moveTo(step1XStartLocation, step1YStartLocation)

            lineTo(animationStep1X, animationStep1Y)

            if (step2) {
                moveTo(animationStep1X, animationStep1Y)

                lineTo(
                    animationStep2X,
                    animationStep2Y
                )
                moveTo(animationStep2X, animationStep2Y)
            }
        }

        // 繪畫到Canvas內
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = checkWidth, cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Preview
@Composable
private fun AnimationCheckPrev() {
    var startCheck by remember {
        mutableStateOf(false)
    }

    Button(onClick = {
        startCheck = !startCheck
    }) {

    }

    AnimationCheck(
        modifier = Modifier.size(190.sdp),
        size = 190.sdp,
        color = Color.Red,
        starAnimation = startCheck,
        lineWidth = 5.sdp
    )
}