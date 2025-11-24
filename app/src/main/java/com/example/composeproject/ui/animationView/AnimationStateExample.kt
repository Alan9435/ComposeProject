package com.example.composeproject.ui.animationView

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.composeproject.R
import com.example.composeproject.ui.modifier.delayClick
import com.example.composeproject.ui.modifier.notRippleClickable
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.CancellationException

@Composable
fun AnimationScreen(modifier: Modifier = Modifier) {
    val lineHeight = 90.sdp
    val scrollState = remember {
        ScrollState(0)
    }
    val itemModifier = Modifier
        .fillMaxWidth()
        .height(lineHeight)
    Column(
        modifier = Modifier.verticalScroll(
            state = scrollState
        )
    ) {
//        AnimationVisibleFade(modifier = itemModifier)
//        AnimationVisibleSlide(modifier = itemModifier)
//        AnimationVisibleScale(modifier = itemModifier)
//        AnimationVisibleExpand(modifier = itemModifier)
//        AnimationVisibleCrossFade(modifier = itemModifier)
        AnimationVisibleContent()
//        AnimationNothing(modifier = itemModifier)
//        AnimationSpring(modifier = itemModifier)
//        AnimationTweenSpec()
//        AnimationSnapSpec()
//        AnimationKeyframesSpec()
//        AnimationRepeatableSpec()
//        AnimationDecaySpec()
//        AnimationListenerTest()
//        AnimationCancel()
//        AnimationTransition(modifier = itemModifier)
    }
}

@Composable
fun AnimationVisibleFade(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(true) }

    Row(modifier = modifier.clickable {
        visible = !visible
    }) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(initialAlpha = 0.3f, animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            Box(
                modifier = Modifier
                    .size(90.sdp)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_penguin),
                    contentDescription = ""
                )
            }
        }

        Text(
            modifier = Modifier.weight(1f),
            text = "fade out/in \n 可調(透明度/時間) \n(可VS組合)",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )
    }
}

@Composable
fun AnimationVisibleSlide(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(true) }

    Row(modifier = modifier.clickable {
        visible = !visible
    }) {
        AnimatedVisibility(
            visible = visible,
            enter = slideIn(initialOffset = { fullSize: IntSize ->
                IntOffset(fullSize.width, fullSize.height)
            }, animationSpec = tween(500)), //
            exit = slideOut(targetOffset = { fullSize: IntSize ->
                IntOffset(fullSize.width, -fullSize.height) // 離出的座標點 0,0表示這個Box的最左上角
            }, animationSpec = tween(500))
        ) {
            Box(
                modifier = Modifier
                    .size(90.sdp)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_penguin),
                    contentDescription = ""
                )
            }
        }

        Text(
            modifier = Modifier.weight(1f),
            text = "Slide out/in \n 可調(出入點/角度/時間) \n(可VS組合)",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )
    }
}

@Composable
fun AnimationVisibleExpand(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(true) }

    Row(modifier = modifier.clickable {
        visible = !visible
    }) {
        AnimatedVisibility(
            visible = visible,
            enter = expandIn(
                initialSize = { fullSize: IntSize ->
                    // 座標 從fullSize的哪個點 延展
                    IntSize(fullSize.width / 2, fullSize.height / 2)
                },
                expandFrom = Alignment.BottomStart,
//                clip = false, // 是否裁切 (僅位移 在看不見的區塊元件仍被繪製完成)
                animationSpec = tween(1000)
            ),
            exit = shrinkOut(
                targetSize = { fullSize: IntSize ->
                    IntSize(0, 0)
                },
//                shrinkTowards = Alignment.Center,
                animationSpec = tween(1000)
            )
        ) {
            Box(
                modifier = Modifier
                    .size(90.sdp)
                    .background(color = Color.Green)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_penguin),
                    contentDescription = ""
                )
            }
        }

        Text(
            modifier = Modifier.weight(1f),
            text = "Expand out/in \n 可調(延展點/方向/時間) \n(可VS組合)",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )
    }
}

@Composable
fun AnimationVisibleScale(modifier: Modifier = Modifier) {
    // 只做繪製效果 不改實際尺寸
    var visible by remember { mutableStateOf(true) }

    Row(modifier = modifier.clickable {
        visible = !visible
    }) {
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(
                initialScale = 0.1f,
                animationSpec = tween(500),
                transformOrigin = TransformOrigin(0f, 0f) // 縮放軸心
            ),
            exit = scaleOut(targetScale = 0.1f, animationSpec = tween(500))
        ) {
            Box(
                modifier = Modifier
                    .size(90.sdp)
                    .background(color = Color.Red)
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.ic_penguin),
                    contentDescription = ""
                )
            }
        }

        Text(
            modifier = Modifier.weight(1f),
            text = "Scale out/in \n 可調(軸心/縮放起始大小/時間) \n(可VS組合)",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )
    }
}

/*
* 切換內容用的 Ture A內容 false B內容
* */
@Composable
fun AnimationVisibleCrossFade(modifier: Modifier = Modifier) {
    // 可改成when
    var toggle by remember { mutableStateOf(true) }
    Row(modifier = modifier.delayClick {
        // 如果避免他再切換前馬上點擊取消 可嘗試+delayClick
        toggle = !toggle
    }) {
        Crossfade(
            targetState = toggle, label = "",
            animationSpec = tween(2000)
        ) {
            if (it) {
                Box(
                    modifier = Modifier
                        .size(90.sdp)
                        .background(color = Color.Green)
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(id = R.drawable.ic_penguin),
                        contentDescription = ""
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(90.sdp)
                        .background(color = Color.Black)
                ) {
                    Text(text = "測試\n測試\n測試", color = Color.White)
                }
            }
        }

        Text(
            modifier = Modifier.weight(1f),
            text = "CrossFade 僅支援透明出入場 \n 可調(時間) \n(可VS組合)",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )
    }
}

@Composable
fun AnimationVisibleContent(modifier: Modifier = Modifier) {
    // 可改成when
    var screenState by remember {
        mutableIntStateOf(1)
    }

    Row(modifier = modifier.delayClick {
        // 如果避免他再切換前馬上點擊取消 可嘗試+delayClick
        if (screenState >= 2) {
            screenState = 0
        } else {
            screenState++
        }
        Log.d("********", "AnimationVisibleContent: ${screenState}")
    }) {
        /*
        * ContentTransform -> targetContentZIndex 繪製Z軸上面的先後順序 (遮蓋關係 入場蓋出場or相反)  sizeTransform：尺寸漸變的動畫效果
        * */
        AnimatedContent(targetState = screenState, transitionSpec = {
            if (screenState == 1) {
                (fadeIn(
                    animationSpec = tween(1500)
                ) + slideInVertically() togetherWith fadeOut(
                    animationSpec = tween(1500, delayMillis = 1500)
                )).apply {
                    // 即將進入黑色時 把他的維度降1 變成紅色蓋黑色的入場
                    targetContentZIndex = -1f
                }
            } else {
                // using SizeTransform() 設定裁切
                (fadeIn(
                    animationSpec = tween(1500)
                ) + slideInVertically() togetherWith fadeOut(
                    animationSpec = tween(1500, delayMillis = 1500)
                )) using SizeTransform()
            }
        }, label = "") {
            when (it) {
                0 -> {
                    Box(
                        modifier = Modifier
                            .size(70.sdp)
                            .background(color = Color.Red)
                    ) {
                        Text(text = "測試 Red\n測試 Red\n測試 Red", color = Color.White)
                    }
                }

                1 -> {
                    Box(
                        modifier = Modifier
                            .size(90.sdp)
                            .background(color = Color.Black)
                    ) {
                        Text(text = "測試 Black\n測試 Black\n測試 Black", color = Color.White)
                    }
                }

                2 -> {
                    Box(
                        modifier = Modifier
                            .size(80.sdp)
                            .background(color = Color.Blue)
                    ) {
                        Text(text = "測試 blue\n測試 blue\n測試 blue", color = Color.White)
                    }
                }
            }
        }

        Text(
            modifier = Modifier.weight(1f),
            text = "Content 各種場景轉換 \n 可調(all) \n(可VS組合)",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )
    }
}

@Composable
fun AnimationNothing(modifier: Modifier = Modifier) {
    var big by remember {
        mutableStateOf(false)
    }
    val boxSize = if (big) 90.sdp else 50.sdp
    val anim by animateDpAsState(targetValue = boxSize, label = "")

    Row(modifier = modifier.clickable {
        big = !big
    }) {
        Box(
            modifier = Modifier
                .size(anim)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_penguin),
                contentDescription = ""
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = "單純漸變 \n 可調(顏色/大小/形狀/../時間) \n (可VS組合)",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )
    }
}

@Composable
fun AnimationSpring(modifier: Modifier = Modifier) {
    var big by remember {
        mutableStateOf(false)
    }

    val boxSize = if (big) 90.sdp else 50.sdp

    val anim = remember {
        Animatable(
            boxSize, Dp.VectorConverter
        )
    }

    LaunchedEffect(big) {
        /*
        * dampingRatio 阻尼比 (有多彈, 受到的阻力有多大) 越大越沒彈性
        * stiffness 剛度(硬度)  彈簧有多想變回去, 越小越Q彈
        * visibilityThreshold 可視域值 (防止彈簧一直彈) ex晃動小於15.dp時 立刻停止
        * initialVelocity 初始速度
        * */
        anim.animateTo(
            targetValue = boxSize,
            animationSpec = spring(
                dampingRatio = 0.1f,
                stiffness = 500f,
//                visibilityThreshold = 15.dp,
            ),
            initialVelocity = 2000.dp
        )
    }

    Row(modifier = modifier.clickable {
        big = !big
    }) {
        Box(
            modifier = Modifier
                .size(anim.value)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_penguin),
                contentDescription = ""
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = "Spring \n (可調彈度/頻率/)\n *無法設定時間 \n (可VS組合)",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )
    }
}

@Composable
fun AnimationTweenSpec(modifier: Modifier = Modifier) {
    val boxSize = 25.sdp
    var startState by remember {
        mutableStateOf(true)
    }

    val anim1 by animateDpAsState(
        targetValue = if (startState) 0.dp else LocalConfiguration.current.screenWidthDp.dp - boxSize,
        animationSpec = tween(
            delayMillis = 0,
            durationMillis = 2000,
            easing = FastOutSlowInEasing,
        ),
        label = ""
    )

    val anim2 by animateDpAsState(
        targetValue = if (startState) 0.dp else LocalConfiguration.current.screenWidthDp.dp - boxSize,
        animationSpec = tween(
            delayMillis = 0,
            durationMillis = 2000,
            easing = LinearOutSlowInEasing,
        ),
        label = ""
    )

    val anim3 by animateDpAsState(
        targetValue = if (startState) 0.dp else LocalConfiguration.current.screenWidthDp.dp - boxSize,
        animationSpec = tween(
            delayMillis = 0,
            durationMillis = 2000,
            easing = FastOutLinearInEasing,
        ),
        label = ""
    )

    val anim4 by animateDpAsState(
        targetValue = if (startState) 0.dp else LocalConfiguration.current.screenWidthDp.dp - boxSize,
        animationSpec = tween(
            delayMillis = 0,
            durationMillis = 2000,
            easing = LinearEasing,
        ),
        label = ""
    )

    Column(modifier = modifier
        .fillMaxWidth()
        .clickable {
            startState = !startState
        }) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 13.sdp),
            text = "Tween AnimationSpec \n 可自訂義",
            fontSize = 19.ssp,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "FastOutSlowInEasing",
            fontSize = 16.ssp,
            textAlign = TextAlign.Start
        )
        Box(
            modifier = Modifier
                .offset(
                    x = anim1,
                    y = 0.dp
                )
                .padding(vertical = 8.sdp)
                .size(boxSize)
                .clip(shape = CircleShape)
                .background(Color.Black)
        ) {

        }

        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "LinearOutSlowInEasing",
            fontSize = 16.ssp,
            textAlign = TextAlign.Start
        )
        Box(
            modifier = Modifier
                .offset(
                    x = anim2,
                    y = 0.dp
                )
                .padding(vertical = 8.sdp)
                .size(boxSize)
                .clip(shape = CircleShape)
                .background(Color.Black)
        ) {

        }

        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "FastOutLinearInEasing",
            fontSize = 16.ssp,
            textAlign = TextAlign.Start
        )
        Box(
            modifier = Modifier
                .offset(
                    x = anim3,
                    y = 0.dp
                )
                .padding(vertical = 8.sdp)
                .size(boxSize)
                .clip(shape = CircleShape)
                .background(Color.Black)
        ) {

        }

        //LinearEasing
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "LinearEasing",
            fontSize = 16.ssp,
            textAlign = TextAlign.Start
        )
        Box(
            modifier = Modifier
                .offset(
                    x = anim4,
                    y = 0.dp
                )
                .padding(vertical = 8.sdp)
                .size(boxSize)
                .clip(shape = CircleShape)
                .background(Color.Black)
        ) {

        }
    }
}

@Composable
fun AnimationSnapSpec(modifier: Modifier = Modifier) {
    val boxSize = 25.sdp
    var startState by remember {
        mutableStateOf(true)
    }

    val anim1 by animateDpAsState(
        targetValue = if (startState) 0.dp else LocalConfiguration.current.screenWidthDp.dp - boxSize,
        animationSpec = snap(
            delayMillis = 2000
        ),
        label = ""
    )

    Column(modifier = modifier
        .fillMaxWidth()
        .clickable {
            startState = !startState
        }) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 13.sdp),
            text = "Snap AnimationSpec \n 差別僅在延遲啟動",
            fontSize = 19.ssp,
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .offset(
                    x = anim1,
                    y = 0.dp
                )
                .padding(vertical = 8.sdp)
                .size(boxSize)
                .clip(shape = CircleShape)
                .background(Color.Black)
        ) {

        }
    }
}

@Composable
fun AnimationKeyframesSpec(modifier: Modifier = Modifier) {
    val boxSize = 25.sdp
    var startState by remember {
        mutableStateOf(true)
    }

    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp

    val anim1 by animateDpAsState(
        targetValue = if (startState) 0.dp else (screenWidthDp - boxSize) / 2,
        animationSpec = keyframes {
            durationMillis = 1000 // 設置動畫時長
            delayMillis = 100 // 設置延遲
            // 在150ms時的數值 並在150ms後面那段曲線為 FastOutSlowInEasing 預設為 LinearEasing
            (screenWidthDp - boxSize) at 300 using FastOutSlowInEasing
            (screenWidthDp - boxSize) / 3 at 600 using FastOutSlowInEasing
        },
        label = ""
    )

    Column(modifier = modifier
        .fillMaxWidth()
        .clickable {
            startState = !startState
        }) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 13.sdp),
            text = "Keyframes AnimationSpec",
            fontSize = 19.ssp,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "多段動畫效果 預設=LinearEasing \n ps.反向時要另外訂製",
            fontSize = 16.ssp,
            textAlign = TextAlign.Start
        )

        Box(
            modifier = Modifier
                .offset(
                    x = anim1,
                    y = 0.dp
                )
                .padding(vertical = 8.sdp)
                .size(boxSize)
                .clip(shape = CircleShape)
                .background(Color.Black)
        ) {

        }
    }
}

@Composable
fun AnimationRepeatableSpec(modifier: Modifier = Modifier) {
    var big by remember {
        mutableStateOf(false)
    }

    val boxSize = if (big) 90.sdp else 50.sdp

    val anim = remember {
        Animatable(
            boxSize, Dp.VectorConverter
        )
    }

    LaunchedEffect(big) {
        /*
        * iterations 迭代次數 (重複次數)
        * animation 動畫參數 僅供TweenSpec,SnapSpec, KeyframesSpec
        * repeatMode 重複模式 Reverse記得填單數
        * initialStartOffset 初始的偏移值(時間) 非位置
        * initialVelocity 初始速度
        * */
        anim.animateTo(
            targetValue = boxSize,
            animationSpec = repeatable(
                iterations = 3,
                animation = tween(),
                repeatMode = RepeatMode.Reverse,
                /*
                * offsetType -> StartOffsetType.Delay 延遲型 單純延遲啟動時間
                *            -> StartOffsetType.FastForward 快進 = 直接從200ms開始
                * */
//                initialStartOffset = StartOffset(
//                    offsetMillis = 200,
//                    offsetType = StartOffsetType.FastForward
//                )
            ),
//            initialVelocity = 2000.dp
        )
    }

    Row(modifier = modifier.clickable {
        big = !big
    }) {
        Box(
            modifier = Modifier
                .size(anim.value)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_penguin),
                contentDescription = ""
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = "Repeatable \n 重複性動畫 \n (僅支援 tween,snap, keyframes)",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )
    }
}

@Composable
fun AnimationDecaySpec(modifier: Modifier = Modifier) {
    var moveOver by remember {
        mutableStateOf(false)
    }

    val anim = remember {
        Animatable(
            0.dp, Dp.VectorConverter
        )
    }

    // 指數衰減
    val decay = remember {
        exponentialDecay<Dp>(
            frictionMultiplier = 0.5f, // 摩擦力
            absVelocityThreshold = 0.3f // 速度域值的絕對值 (當動畫到達數值時間直接結束動畫)
        )
    }

    /*
        樣條 (listview recyclerview的慣性滑動 ) 通常情況直接使用rememberSplineBasedDecay即可
        帶density是為了讓 摩擦力與象素密度掛勾
     */
//    splineBasedDecay<>()

    // 帶remember以及density的
//    val decay = rememberSplineBasedDecay<Dp>()

    LaunchedEffect(moveOver) {
        /*
        * initialVelocity 初始速度, 每秒多少個單位(視你想要的類型決定) 越快越晚停 但在象素密度越高的裝置也會越快停下
        * */
        anim.animateDecay(
            initialVelocity = 300.dp,
            animationSpec = decay
        )
    }

    Row(modifier = modifier
        .height(500.dp)
        .clickable {
            moveOver = !moveOver
        }) {
        Box(
            modifier = Modifier
                .padding(0.dp, if (moveOver) anim.value else 0.dp, 0.dp, 0.dp)
                .size(50.sdp)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_penguin),
                contentDescription = ""
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = "DecaySpec \n 衰減/慣性動畫",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )
    }
}

@Composable
fun AnimationListenerTest(modifier: Modifier = Modifier) {
    var big by remember {
        mutableStateOf(false)
    }

    val boxSize = if (big) 90.sdp else 50.sdp

    val anim = remember {
        Animatable(
            boxSize, Dp.VectorConverter
        )
    }

    var animCurrentValue by remember {
        mutableStateOf(0.dp)
    }

    LaunchedEffect(big) {
        /*
        * dampingRatio 阻尼比 (有多彈, 受到的阻力有多大) 越大越沒彈性
        * stiffness 剛度(硬度)  彈簧有多想變回去, 越小越Q彈
        * visibilityThreshold 可視域值 (防止彈簧一直彈) ex晃動小於15.dp時 立刻停止
        * initialVelocity 初始速度
        * */
        anim.animateTo(
            targetValue = boxSize,
            animationSpec = spring(
                dampingRatio = 0.1f,
                stiffness = 500f,
                visibilityThreshold = 10.dp,
            ),
            initialVelocity = 2000.dp
        ) {
            // 在動畫的每一禎都會觸發
            animCurrentValue = value
        }
        // 動畫結束時觸發
        Log.d("*********", "動畫結束")
    }

    Row(modifier = modifier.clickable {
        big = !big
    }) {
        Box(
            modifier = Modifier
                .size(anim.value)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_penguin),
                contentDescription = ""
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = "監聽每禎動畫\n目前為:${animCurrentValue} \n (可VS組合)",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )
    }
}

/*
* 中斷動畫的各種形式及應用
* */
@Composable
fun AnimationCancel(modifier: Modifier = Modifier) {

    var toggle by remember {
        mutableStateOf(false)
    }

    val anim = remember {
        Animatable(
            0.dp, Dp.VectorConverter
        )
    }

    val animY = remember {
        Animatable(
            0.dp, Dp.VectorConverter
        )
    }

    // 指定指數衰減型的
    val decay = remember {
        exponentialDecay<Dp>()
    }

    val maxWidth = LocalConfiguration.current.screenWidthDp.dp

    LaunchedEffect(toggle) {
        try {
            anim.updateBounds(lowerBound = 0.dp, upperBound = maxWidth - 50.dp) // 設定動畫邊界 def沒邊界
            // 因為有可能2維動畫撞到右邊界導致往下的動畫也被停止
            var animResult = anim.animateDecay(6000.dp, decay)
//            if(animResult.endReason == AnimationEndReason.BoundReached) { // 如果這個動畫撞擊到邊界
//                // 啟動新動畫 或做別的事情
//                anim.animateDecay(- animResult.endState.velocity, decay)
//            }
            // 抓取anim的終止原因, 如果是觸碰到邊界
            while (animResult.endReason == AnimationEndReason.BoundReached) {
                Log.d("************", "AnimationCancel 橫向 v: ${animResult.endState.velocity}")
                // 將目前的anim 設定為衰減類 的反向剩餘速度
                animResult = anim.animateDecay(-animResult.endState.velocity, decay)
            }
        } catch (e: CancellationException) {
            Log.d("**********", "出現異常CancellationException")
        }
    }

    LaunchedEffect(toggle) {
        animY.updateBounds(lowerBound = 0.dp, upperBound = 100.dp)
        var animResult = animY.animateDecay(3000.dp, decay)
//        if(animResult.endReason == AnimationEndReason.BoundReached) {
//            Log.d("************", "AnimationCancel 垂直 v: ${animResult.endState.velocity}")
//        }

        while (animResult.endReason == AnimationEndReason.BoundReached) {
            animResult = animY.animateDecay(-animResult.endState.velocity, decay)
        }
    }

//    LaunchedEffect(Unit) {
//        delay(1500)
//        anim.animateDecay((-300).dp, decay)
//    }

    Column(modifier = modifier
        .height(200.dp)
        .fillMaxWidth()
        .background(color = Color.Red)
        .clickable {
            toggle = !toggle
//            c.launch {
//                anim.stop()
//            }
        }) {
        Text(
            modifier = Modifier,
            text = "反彈",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )

        Box(
            modifier = Modifier
                .padding(anim.value, animY.value, 0.dp, 0.dp)
                .size(50.dp)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_penguin),
                contentDescription = ""
            )
        }
    }
}

@Composable
fun AnimationTransition(modifier: Modifier = Modifier) {
    var big by remember {
        mutableStateOf(false)
    }

    /*
        1.在初始話的時候創建Transition對象出來
        2.後續重組後會更新狀態 會把收到的參數更新到之前的targetState內
        bigTransition會擁有一個把false慢慢變成true的狀態 並時時更新狀態
        統一在bigTransition內部 對多屬性動畫 都只會啟動一個協程管理
        所以性能較好
    */
    val bigTransition = updateTransition(targetState = big, label = "企鵝膨脹狀態")

    //效果一樣 但好處=可以設置設定初始值與目標值
//    val bigTransition = updateTransition(targetState = remember {
//        MutableTransitionState(!big).apply {
//            targetState = big
//        }
//    } , label = "企鵝膨脹狀態")

    // 通過transition的目標狀態 來計算出動畫的目標值
    val anim by bigTransition.animateDp(label = "size") {
        if (it) 90.sdp else 50.sdp
    }
    val corner by bigTransition.animateDp(label = "corner", transitionSpec = {
        // 為什麼要包進{}內? 為了讓不同的狀態去給定不同的Spec
        when {
            false isTransitioningTo true -> tween()
            else -> spring()
        }
    }) {
        if (it) 0.sdp else 18.sdp
    }

    /*
        原本寫法 是會產生個別的協程
        Ex下面就會產生2個
    */
//    val anim by animateDpAsState(targetValue = if(big) 90.sdp else 50.sdp, label = "anim")
//    val corner by animateDpAsState(targetValue = if(big) 90.sdp else 50.sdp, label = "corner")


    Row(modifier = modifier.clickable {
        big = !big
    }) {
        Box(
            modifier = Modifier
                .size(anim)
                .clip(RoundedCornerShape(corner))
                .background(color = Color.Black)

        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_penguin),
                contentDescription = ""
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = "Transition \n 單狀態管理多動畫\n 支援多段不同Spec \n 開preview可看效果",
            textAlign = TextAlign.End,
            fontSize = 18.ssp
        )
    }
}

/**
 * 漸層顏色動畫效果
 * */
@Composable
fun LineGradientAnimation(modifier: Modifier) {
    var isInitStatus by remember {
        mutableStateOf(true)
    }

    val scrollState = rememberLazyListState()
    val firstVisibleItemIndex by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex }
    }

    // 0xffffBeB8
    val lineGradientTop by
    animateColorAsState(
        targetValue = if (isInitStatus) Color(255, 190, 184) else Color(255, 52, 97),
        label = ""
    )

    // 0xffFED2DC 0xffFA3461
    val lineGradientMid by
    animateColorAsState(
        targetValue = if (isInitStatus) Color(254, 210, 220) else Color(255, 52, 97),
        label = ""
    )

    // 0xffFFFFFF
    val lineGradientBottom by
    animateColorAsState(
        targetValue = if (isInitStatus) Color(255, 255, 255) else Color(255, 52, 97),
        label = ""
    )

    val brush = Brush.verticalGradient(
        colors = listOf(
            lineGradientTop,
            lineGradientMid,
            lineGradientBottom,
        ),
    )

    Column(modifier = modifier) {
        Box(
            Modifier
                .height(150.sdp)
                .fillMaxWidth()
                .background(brush = brush)
                .notRippleClickable {
                    isInitStatus = isInitStatus.not()

                }
        ) {
            Text(modifier = Modifier.padding(vertical = 30.sdp), text = "${firstVisibleItemIndex}")
        }

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            state = scrollState
        ) {
            items(50) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.sdp), text = "測試項目"
                )
            }
        }

        Box(
            Modifier
                .height(56.sdp)
                .fillMaxWidth()
                .background(color = Color.Red)
        ) {

        }
    }
}

@Preview(
)
@Composable
private fun Test() {
    AnimationTransition()
}

