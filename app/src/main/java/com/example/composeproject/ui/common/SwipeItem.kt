package com.example.composeproject.ui.common

import android.util.Log
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.composeproject.R
import com.example.composeproject.ui.modifier.delayClick
import com.example.composeproject.ui.theme.LocalCustomColors
import com.example.composeproject.utils.mdp
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class DragAnchors {
    Start,
    Center,
    End
}

/**
 * https://canopas.com/how-to-implement-swipe-to-action-using-anchoreddraggable-in-jetpack-compose-cccb22e44dff
 * 可左右滑動的itemWeight
 * @param state 滑動的狀態, 如果不用 可直接在SwipeItem內
 * @param key 用來記錄item的唯一值 便於onStateChange時判斷更新 (相當於給這個weight命名)
 * @param withItemMode 後層按鈕是否隨著item滑動而跟著位移出現
 * @param swipeEnabled 是否允許左右滑動
 * @param startAnchor 左邊區塊的可滑動距離（單位：px），同時決定該區塊的寬度
 * @param endAnchor 右邊區塊的可滑動距離（單位：px），同時決定該區塊的寬度
 * @param startContent 左側滑出區塊的自訂 UI，寬度由 startAnchor 決定，offset 動畫由元件內部處理；傳 null 則不顯示左側區塊
 * @param endContent 右側滑出區塊的自訂 UI，寬度由 endAnchor 決定，offset 動畫由元件內部處理；傳 null 則不顯示右側區塊
 * @param contentItemWeight 列表組件
 * @param onStateChange 當item被滑動時的callback 回傳key(weight唯一值), DragAnchors狀態
 * */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeItem(
    state: AnchoredDraggableState<DragAnchors>,
    key: Int,
    withItemMode: Boolean = false,
    swipeEnabled: Boolean = true,
    startAnchor: Float = 0f,
    endAnchor: Float = 0f,
    startContent: (@Composable BoxScope.() -> Unit)? = null,
    endContent: (@Composable BoxScope.() -> Unit)? = null,
    contentItemWeight: @Composable BoxScope.() -> Unit,
    onStateChange: (key: Int, itemState: DragAnchors) -> Unit = { _, _ -> }
) {
    val density = LocalDensity.current
    val startBlockWidth = with(density) { startAnchor.toDp() }
    val endBlockWidth = with(density) { endAnchor.toDp() }

    LaunchedEffect(state.currentValue) {
        onStateChange(key, state.currentValue)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // startContent
            if (startContent != null) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(startBlockWidth)
                        .offset {
                            if (withItemMode) {
                                IntOffset(
                                    x = (-state
                                        .requireOffset() - startAnchor)
                                        .roundToInt(),
                                    y = 0
                                )
                            } else {
                                IntOffset(0, 0)
                            }
                        }
                ) {
                    startContent()
                }
            }

            // endContent
            if (endContent != null) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(endBlockWidth)
                        .offset {
                            if (withItemMode) {
                                IntOffset(
                                    x = (-state
                                        .requireOffset() + endAnchor)
                                        .roundToInt(),
                                    y = 0
                                )
                            } else {
                                IntOffset(0, 0)
                            }
                        }
                ) {
                    endContent()
                }
            }
        }

        //原本的Item view 尾隨編輯view
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Transparent)
                .offset {
                    IntOffset(
                        x = -state
                            .requireOffset()
                            .roundToInt(),
                        y = 0
                    )
                }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal, // 拖曳內容的方向
                    enabled = swipeEnabled, // 啟用/停用手勢
                    reverseDirection = true, // 反轉拖曳方向
                    interactionSource = null // 互動
                ),
        ) {
            contentItemWeight()
        }
    }
}

@Composable
fun SwipeExampleScreen(modifier: Modifier = Modifier) {
    val itemStates = remember { mutableStateMapOf<Int, AnchoredDraggableState<DragAnchors>>() }
    val density = LocalDensity.current
    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val startAnchor = screenWidthPx / 4f
    val endAnchor = screenWidthPx  / 4f
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val velocityThreshold = with(density) { 100.dp.toPx() }
    val scope = rememberCoroutineScope()
    val lazyColumn = rememberLazyListState()

    // 列表滾動時收起所有已開啟編輯的item
    LaunchedEffect(lazyColumn.isScrollInProgress) {
        scope.launch {
            itemStates.forEach {
                it.value.animateTo(DragAnchors.Center)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        state = lazyColumn
    ) {
        items(50) { index ->
            val state = itemStates.getOrPut(index) {
                AnchoredDraggableState(
                    initialValue = DragAnchors.Center,
                    anchors = DraggableAnchors {
                        DragAnchors.Start at -startAnchor
                        DragAnchors.Center at 0f
                        DragAnchors.End at endAnchor
                    },
                    // 滑動到一半時作為臨界點 決定DragAnchors的狀態
                    positionalThreshold = { totalDistance: Float -> totalDistance * 0.5f },
                    // 滑動的速度為多少可以切換DragAnchors的狀態 而不需要滑動到臨界點
                    velocityThreshold = { velocityThreshold },
                    snapAnimationSpec = spring(),
                    decayAnimationSpec = decayAnimationSpec,
                )
            }

            Column {
                SwipeItem(
                    state = state,
                    key = index,
                    startAnchor = startAnchor,
                    endAnchor = endAnchor,
                    startContent = {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray)
                                .delayClick {
                                    Log.d("*******", "click隨便: in")
                                },
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_cloud_24),
                                contentDescription = ""
                            )
                            Text(
                                modifier = Modifier,
                                text = "隨便"
                            )
                        }
                    },
                    endContent = {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(Color.Green)
                                    .delayClick {
                                        Log.d("*******", "click編輯: in")
                                    },
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_baseline_celebration_24),
                                    contentDescription = ""
                                )
                                Text(
                                    modifier = Modifier,
                                    text = "編輯"
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(Color.Yellow)
                                    .delayClick {
                                        Log.d("*******", "click刪除: in")
                                    },
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_baseline_celebration_24),
                                    contentDescription = ""
                                )
                                Text(
                                    modifier = Modifier,
                                    text = "刪除"
                                )
                            }
                        }
                    },
                    contentItemWeight = {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.mdp)
                                .background(LocalCustomColors.current.pinkRed100)
                                .align(Alignment.Center),
                            text = "把你想要滑動的元件放這裡",
                            textAlign = TextAlign.Center
                        )
                    },
                    onStateChange = { key, mState ->
                        // 如果item變換不是置中 則遍歷每項item並將狀態改為置中 (一次只允許一個item處於編輯狀態)
                        if (mState != DragAnchors.Center) {
                            itemStates.forEach { (itemKey, itemState) ->
                                if (key != itemKey) {
                                    scope.launch {
                                        itemState.animateTo(DragAnchors.Center)
                                    }
                                }
                            }
                        }
                    }
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.mdp)
                        .background(LocalCustomColors.current.dividerLineColor)
                )
            }
        }
    }
}
