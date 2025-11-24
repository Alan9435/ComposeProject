package com.example.composeproject.ui.common

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import kotlin.math.abs

/**
 * 可縮放的圖片
 * */
@Composable
fun ZoomableImage(
    modifier: Modifier = Modifier,
    imageUrl: String
) {
    if (imageUrl.isEmpty()) return

    // 圖片的縮放比例狀態
    var scale by remember { mutableStateOf(1f) }
    // 圖片的位移狀態
    var offset by remember { mutableStateOf(Offset.Zero) }

    // 最小和最大縮放比例
    val minScale = 1f
    val maxScale = 5f // 最大可放大到 3 倍

    AsyncImage(
        model = imageUrl,
        contentDescription = "Zoomable Image",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    /*
                    * 抓第一次手指按下的事件
                    * */
                    val down = awaitFirstDown()

                    var zoom = 1f // 用於手勢期間的累積縮放
                    var pan = Offset.Zero // 用於手勢期間的累積平移

                    // 儲存手勢開始時的狀態
                    val initialScale = scale
                    val initialOffset = offset

                    var pointer = down // 追蹤第一個按下的指針

                    /*
                     * 限制位移範圍 避免一直滑動出很遠的地方
                     * 原本圖片的size * (縮放的比例 -> -1是因為 scale = 2f 放大2倍數時 表示圖片長寬都增加了100%)
                     * / 2 -> 因為當圖片放大時，它是從中心向外均勻放大的。因此，任何超出原始邊界的溢出部分，都是平均分佈在左右兩邊（或上下兩邊）的
                     * coerceAtLeast -> 確保不會是負數而產生程式碼錯誤的情況
                     */
                    val maxOffsetX = (size.width * (scale - 1) / 2).coerceAtLeast(0f)
                    val maxOffsetY = (size.height * (scale - 1) / 2).coerceAtLeast(0f)

                    /*
                    * pointer.id == down.id -> 當下的(按下手勢id = 最初的手勢id)
                    * it.id != down.id
                    * it.pressed -> 當前遍歷的觸控點是否處於按下
                    * = 只要最初按下的那根手指還在螢幕上 (條件一)，或者除了最初按下的手指之外還有其他手指按在螢幕上 (條件二)，就繼續執行迴圈。
                    * */
                    while (pointer.id == down.id || currentEvent.changes.any { it.id != down.id && it.pressed }) {
                        val event = awaitPointerEvent() // 等待下一個指針事件

                        val zoomChange = event.calculateZoom() // 計算當前事件的縮放變化
                        val panChange = event.calculatePan()   // 計算當前事件的平移變化

                        if (event.changes.size > 1) { // 多指手勢 (縮放和平移) changes > 1 表示觸摸點為多指
                            // 消費所有指針事件，阻止其傳播給 HorizontalPager
                            event.changes.forEach { it.consume() }

                            // 更新縮放比例
                            zoom *= zoomChange // 累計縮放變化
                            scale = (initialScale * zoom).coerceIn(minScale, maxScale)

                            // 更新平移 (考慮縮放後的平移)
                            // 這裡直接將 panChange 加到累積的 pan 上，因為它是實際的像素移動量
                            pan += panChange
                            val currentPanX = initialOffset.x + pan.x
                            val currentPanY = initialOffset.y + pan.y

                            // 更新偏移
                            offset = Offset(
                                x = currentPanX.coerceIn(-maxOffsetX, maxOffsetX),
                                y = currentPanY.coerceIn(-maxOffsetY, maxOffsetY)
                            )
                        } else if (event.changes.size == 1 && pointer.id == down.id) { // 單指手勢
                            val change = event.changes[0] // 獲取單指的變化

                            // 判斷是否超過觸摸閾值 (touch slop)
                            // 只要圖片縮放不是 minScale (即 scale > minScale)，就允許上下左右滑動
                            // 這裡的 zoomAccumulated != 1f 確保了即使剛開始縮放，也能立即接管平移
                            if (scale > minScale) {
                                /*
                                * 手勢在螢幕移動的距離減去已消耗的距離
                                * 放在消費事件前 否則會偵測不到變動量
                                * */
                                val dragAmount = change.positionChange()

                                change.consume() // 消費事件

                                // 使用當前單指的移動量 dragAmount 來更新 offset
                                offset = Offset(
                                    x = (offset.x + dragAmount.x).coerceIn(
                                        -maxOffsetX,
                                        maxOffsetX
                                    ),
                                    y = (offset.y + dragAmount.y).coerceIn(
                                        -maxOffsetY,
                                        maxOffsetY
                                    )
                                )
                            } else { // 圖片原始大小 (scale == minScale) 相當於沒縮放過圖片
                                /*
                                * 區分是水平滑動或垂直滑動
                                * */
                                if (abs(change.positionChange().x) > abs(change.positionChange().y)) {
                                    // 水平滑動 不消費，讓 HorizontalPager 處理
                                } else {
                                    // 垂直滑動 消費滑動
                                    change.consume()
                                }
                            }
                        }

                        // 更新 pointer 為下一個事件做準備
                        // 尋找仍然按下的與 down.id 匹配的指針，如果沒有，則跳出迴圈
                        pointer = event.changes.firstOrNull { it.id == down.id && it.pressed } ?: break

                        /*
                        * event.changes.all { it.changedToUp() -> 所有手指都離開螢幕
                        * pointer.isOutOfBounds(size, Size.Zero)) -> 按下後拖曳出了螢幕
                        * 終止手勢偵測
                        * */
                        if (event.changes.all { it.changedToUp() } || pointer.isOutOfBounds(size, Size.Zero)) break
                    }

                    // 手勢結束後 如果縮放回到原始大小 將位移歸零
                    if (scale <= minScale) {
                        offset = Offset.Zero
                    }
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
    )
}