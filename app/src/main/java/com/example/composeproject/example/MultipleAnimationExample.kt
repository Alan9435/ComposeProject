package com.example.composeproject.example

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.composeproject.utils.mdp
import com.example.composeproject.utils.msp
import com.example.composeproject.ui.modifier.notRippleClickable
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MultipleAnimationExampleScreen(
    modifier: Modifier = Modifier
) {
    Text("適合放一些圖片點擊後的效果 Ex: 按讚")

    MultipleAnimationWeight(
        modifier = modifier,
        floatSize = 30.mdp,
        imgRes = android.R.drawable.star_on,
        onClick = {
            Log.d("*******", "MultipleAnimationExampleScreen: in")
        }
    ) {
        Text(
            text = "Click Me",
            fontSize = 16.msp
        )
    }
}

@Composable
fun MultipleAnimationWeight(
    modifier: Modifier = Modifier,
    floatSize: Dp = 0.dp,
    imgRes: Int,
    onClick: () -> Unit,
    targetWeight: @Composable BoxScope.() -> Unit
) {
    val flyingImages = remember { mutableStateListOf<FlyingImageState>() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier.notRippleClickable {
                onClick()
                coroutineScope.launch {
                    val offsetY = Animatable(0f)
                    val alpha = Animatable(1f)
                    val imageState = FlyingImageState(offsetY, alpha)
                    flyingImages.add(imageState)

                    launch {
                        offsetY.animateTo(
                            targetValue = -200f,
                            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
                        ) {
                            if (value == -200f) {
                                flyingImages.remove(imageState) // 動畫結束後移除
                            }
                        }
                    }

                    launch {
                        alpha.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
                        )
                    }
                }
            }
        ) {
            targetWeight()
        }

        flyingImages.forEach { imageState ->
            Image(
                modifier = if (floatSize == 0.dp) {
                    Modifier
                        .offset {
                            IntOffset(
                                x = 0,
                                y = imageState.offsetY.value.roundToInt()
                            )
                        }
                        .graphicsLayer(alpha = imageState.alpha.value)
                } else {
                    Modifier
                        .size(floatSize)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = imageState.offsetY.value.roundToInt()
                            )
                        }
                        .graphicsLayer(alpha = imageState.alpha.value)
                },
                painter = painterResource(id = imgRes), // 實際非出來的圖片
                contentDescription = "飛走的圖片",
                contentScale = ContentScale.Fit,
            )
        }
    }
}

//@Composable
//fun MultipleAnimationWeight(
//    modifier: Modifier = Modifier,
//    imgRes: Int,
//    onClick: () -> Unit,
//    targetWeight: @Composable BoxScope.() -> Unit
//) {
//    val flyingImages = remember { mutableStateListOf<FlyingImageState>() }
//    val coroutineScope = rememberCoroutineScope()
//
//    Box(
//        modifier,
//        contentAlignment = Alignment.Center
//    ) {
//        Box(
//            Modifier.notRippleClickable {
//                onClick()
//                coroutineScope.launch {
//                    val offsetY = Animatable(0f)
//                    val alpha = Animatable(1f)
//                    val imageState = FlyingImageState(offsetY, alpha)
//                    flyingImages.add(imageState)
//
//                    launch {
//                        offsetY.animateTo(
//                            targetValue = -200f,
//                            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
//                        ) {
//                            if (value == -200f) {
//                                flyingImages.remove(imageState) // 動畫結束後移除
//                            }
//                        }
//                    }
//
//                    launch {
//                        alpha.animateTo(
//                            targetValue = 0f,
//                            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
//                        )
//                    }
//                }
//            }
//        ) {
//            targetWeight()
//        }
//
//        flyingImages.forEach { imageState ->
//            Image(
//                painter = painterResource(id = imgRes), // 實際非出來的圖片
//                contentDescription = "飛走的圖片",
//                contentScale = ContentScale.Fit,
//                modifier = Modifier
//                    .offset {
//                        IntOffset(
//                            x = 0,
//                            y = imageState.offsetY.value.roundToInt()
//                        )
//                    }
//                    .graphicsLayer(alpha = imageState.alpha.value)
//            )
//        }
//    }
//}

// 儲存每個飛行圖片的動畫狀態
private data class FlyingImageState(
    val offsetY: Animatable<Float, AnimationVector1D>,
    val alpha: Animatable<Float, AnimationVector1D>
)