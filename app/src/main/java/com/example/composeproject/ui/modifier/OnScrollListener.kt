package com.example.composeproject.ui.modifier

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput

/**
 * @param scrollState 滾動狀態
 * @param onScroll 當滾動時要觸發的
 */
fun Modifier.addOnScrollListener(
    scrollState: ScrollState,
    onScroll: () -> Unit
): Modifier = composed {
    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y != 0f) {
                    onScroll()
                }
                return Offset.Zero
            }
        }
    }

    // 外部滾動監聽 如果拖曳的是內部組件會偵測不到 所以需補上 NestedScrollConnection用於偵測內部組件
    LaunchedEffect (scrollState) {
        snapshotFlow { scrollState.isScrollInProgress }
            .collect { isScrollInProgress ->
                if (isScrollInProgress) {
                    onScroll()
                }
            }
    }

    this
        .nestedScroll(connection)
        .verticalScroll(scrollState)
}

/**
 * 偵測是否觸摸滑動 通常用於滑動關鍵盤
 * 註: 會影響到本身會滾動的組件 只建議用在不會滾動的場景或個別為滾動內的項目添加此Modifier
 * */
fun Modifier.dragDetect(onDrag: () -> Unit): Modifier {// 偵測手勢拖曳就收鍵盤 寫在lazyColum會覆蓋掉滾動事件
    val dragOffsetY =
        mutableFloatStateOf(0f)

    return this.pointerInput(Unit) {
        detectDragGestures(onDrag = { _, dragAmount ->
            dragOffsetY.floatValue += dragAmount.y

            if (dragOffsetY.floatValue > 50 || dragOffsetY.floatValue < -50) {
                onDrag()
                dragOffsetY.floatValue = 0f
            }
        })
    }
}