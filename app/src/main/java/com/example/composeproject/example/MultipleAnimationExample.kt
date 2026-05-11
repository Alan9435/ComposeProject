package com.example.composeproject.example

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.composeproject.ui.animationView.AnimationCheck
import com.example.composeproject.utils.mdp
import com.example.composeproject.utils.msp
import com.example.composeproject.ui.modifier.notRippleClickable
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MultipleAnimationExampleScreen(
    modifier: Modifier = Modifier
) {
    var startCheck by remember {
        mutableStateOf(false)
    }

    Column(modifier) {
        Row(
            Modifier.height(200.mdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("適合放一些圖片點擊後的效果 Ex: 按讚 點他->")

            MultipleAnimationWeight(
                modifier = Modifier,
                floatSize = 30.mdp,
                imgRes = android.R.drawable.star_on, // 你要噴出去的圖示
                onClick = {
                    Log.d("*******", "MultipleAnimationExampleScreen: in")
                }
            ) {
                // 顯示在外面的圖示
                Image(
                    modifier = Modifier.padding(16.mdp).size(50.mdp),
                    painter = painterResource(android.R.drawable.star_on),
                    contentDescription = ""
                )
            }
        }

        Row(
            modifier = Modifier.height(300.mdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                startCheck = !startCheck
            }) {
                Text("簡單打勾動畫")
            }

            AnimationCheck(
                modifier = Modifier.size(190.mdp),
                size = 190.mdp,
                color = Color.Red,
                starAnimation = startCheck,
                lineWidth = 5.mdp
            )
        }

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