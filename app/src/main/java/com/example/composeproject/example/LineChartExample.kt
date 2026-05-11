package com.example.composeproject.example

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeproject.utils.mdp
import com.example.composeproject.data.BuffLabelTip
import com.example.composeproject.data.ChartPointDataInfo
import com.example.composeproject.data.LineChartScreenState
import com.example.composeproject.data.PointInfoResult
import com.example.composeproject.data.getRangeData
import com.example.composeproject.ui.modifier.PointedBackground
import com.example.composeproject.ui.modifier.PointerPosition
import com.example.composeproject.ui.modifier.inVisible
import com.example.composeproject.ui.textStyle.ChartTextStyle
import com.example.composeproject.ui.textStyle.ChartToolTipStyle
import com.example.composeproject.ui.theme.LocalCustomColors
import com.example.composeproject.viewmodel.LineChartViewModel
import com.example.composeproject.viewmodel.LineChartViewModel.Companion.CHART_ITEMS_PER_PAGE
import kotlin.math.roundToInt

@Composable
fun LineChartExampleScreen(
    modifier: Modifier = Modifier,
    viewModel: LineChartViewModel = viewModel()
) {
    val screenStatus by viewModel.screenState.collectAsStateWithLifecycle()

    val pageStatus = rememberPagerState(
        initialPage = 0,
        pageCount = { screenStatus.totalChartPage },
    )

    Box(
        modifier = modifier.height(500.mdp)
    ) {
        ChartLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.mdp),
            screenStatus = screenStatus,
            leftColumTextList = screenStatus.chartViewList,
            rightColumTextList = screenStatus.chartApplyOrTalkList,
            pageStatus = pageStatus
        )
    }
}

@Composable
fun ChartLayout(
    modifier: Modifier = Modifier,
    rowLine: Int = 4,
    columLine: Int = 7,
    leftColumTextList: List<String> = listOf("100", "75", "50", "25", "0"),
    rightColumTextList: List<String> = listOf("100", "75", "50", "25", "0"),
    screenStatus: LineChartScreenState? = null,
    pageStatus: PagerState
) {
    val density = LocalDensity.current
    val lineHeightDp = (0.5).mdp
    val verticalLineColor = LocalCustomColors.current.percent10Black // 垂直實線顏色
    val horizontalLineColor = LocalCustomColors.current.percent10Black // 水平虛線顏色
    val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    // 偵測繪製完成後圖表左右的padding 這樣上下的文字/圖示均分後才能對齊
    var leftLabelColumnWidthDp by remember { mutableStateOf(0.dp) }
    var rightLabelColumnWidthDp by remember { mutableStateOf(0.dp) }

    // 左右 Column 的 padding 寬度= 8*2 + 文字寬度
    val chartBorderTextHorizontalPadding = 8.mdp

    val currentPageDateList = remember(pageStatus.currentPage, screenStatus?.chartDateList) {
        screenStatus?.chartDateList?.getRangeData(
            currentPage = pageStatus.currentPage,
            perPage = CHART_ITEMS_PER_PAGE
        )
    }

    // 取得目前頁面區間的資料
    val currentResultList =
        remember(pageStatus.currentPage, screenStatus?.pointInfoResult) {
            screenStatus?.pointInfoResult?.getRangeData(
                currentPage = pageStatus.currentPage,
                perPage = CHART_ITEMS_PER_PAGE
            )
        }

    val androidPoints = remember(pageStatus.currentPage, screenStatus?.androidLineChartList) {
        (screenStatus?.androidLineChartList ?: listOf()).getRangeData(
            currentPage = pageStatus.currentPage,
            perPage = CHART_ITEMS_PER_PAGE
        )
    }

    val cloudPoints = remember(pageStatus.currentPage, screenStatus?.cloudLineChartList) {
        (screenStatus?.cloudLineChartList ?: listOf()).getRangeData(
            currentPage = pageStatus.currentPage,
            perPage = CHART_ITEMS_PER_PAGE
        )
    }

    val alertPoints = remember(pageStatus.currentPage, screenStatus?.alertLineChartList) {
        (screenStatus?.alertLineChartList ?: listOf()).getRangeData(
            currentPage = pageStatus.currentPage,
            perPage = CHART_ITEMS_PER_PAGE
        )
    }

    // format過後的日期 用於toolTip顯示用
    val currentDateForHintList = remember(pageStatus.currentPage, screenStatus?.dateForHint) {
        (screenStatus?.dateForHint ?: listOf()).getRangeData(
            currentPage = pageStatus.currentPage,
            perPage = CHART_ITEMS_PER_PAGE
        )
    }

    // 追蹤拖曳指示線的列索引 (從 0 到 columLine - 1)
    var selectedColumnIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier) {
        // 圖表上方標籤
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = leftLabelColumnWidthDp, end = rightLabelColumnWidthDp)
        ) {
            currentResultList?.forEach {
                ChartWeight(
                    modifier = Modifier.weight(1f),
                    chartDataPerformancesList = BuffLabelTip.getBuffLabelByFlagList(it.buffs)
                )
            }

            repeat(CHART_ITEMS_PER_PAGE - (currentResultList?.size ?: 0)) {
                ChartWeight(
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // 圖表左邊文字
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .onGloballyPositioned { coordinates ->
                        leftLabelColumnWidthDp = with(density) { coordinates.size.width.toDp() }
                    }
                    .padding(horizontal = chartBorderTextHorizontalPadding),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                repeat(leftColumTextList.size) { index ->
                    Text(
                        text = leftColumTextList.getOrNull(index) ?: "",
                        style = ChartTextStyle
                    )
                }
            }

            // 左右文字 繪製完成後剩餘的寬度用來繪製表格
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 7.mdp)
            ) {
                val chartBoxWidthPx = constraints.maxWidth.toFloat()
                val chartBoxHeightPx = constraints.maxHeight.toFloat()
                // 每格的寬度
                val segmentWidthPx = chartBoxWidthPx / columLine.toFloat()

                // 底部圖表格線
                Column(modifier = Modifier.fillMaxSize()) {
                    repeat(rowLine) { row ->
                        val isFirstRow = row == 0 // 新增判斷

                        Row(
                            modifier = Modifier
                                .weight(1f) // 確保行高均分
                                .fillMaxWidth()
                        ) {
                            repeat(columLine) { column ->
                                val isFirstColumn = column == 0

                                Box(
                                    // 修正 1：添加佈局權重，確保寬度均分
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .drawBehind {
                                            val strokeWidthPx = lineHeightDp.toPx()

                                            // 繪製最左邊的實線 (只在第一列繪製)
                                            if (isFirstColumn) {
                                                drawLine(
                                                    color = verticalLineColor,
                                                    start = Offset(strokeWidthPx / 2, 0f),
                                                    end = Offset(
                                                        strokeWidthPx / 2,
                                                        size.height
                                                    ),
                                                    strokeWidth = strokeWidthPx
                                                )
                                            }

                                            // 繪製中間和最右邊的垂直實線
                                            // 繪製右邊線
                                            drawLine(
                                                color = verticalLineColor,
                                                start = Offset(
                                                    size.width - strokeWidthPx / 2,
                                                    0f
                                                ),
                                                end = Offset(
                                                    size.width - strokeWidthPx / 2,
                                                    size.height + 0f
                                                ),
                                                strokeWidth = strokeWidthPx
                                            )

                                            // ----------------------------------------------------
                                            // 2. 水平虛線：整合頂部和底部線條 (取代所有外部 Spacer)
                                            // ----------------------------------------------------

                                            // 繪製頂部虛線 (只在第一行繪製，作為網格的最頂線)
                                            if (isFirstRow) {
                                                drawLine(
                                                    color = horizontalLineColor,
                                                    start = Offset(
                                                        0f,
                                                        (strokeWidthPx / 2) + 0f
                                                    ),
                                                    end = Offset(
                                                        size.width,
                                                        (strokeWidthPx / 2) + 0f
                                                    ),
                                                    strokeWidth = strokeWidthPx,
                                                    pathEffect = dashPathEffect
                                                )
                                            }

                                            // 繪製底邊虛線 (所有行的底線，包含最後一行，作為行的分隔線或網格的最底線)
                                            drawLine(
                                                color = horizontalLineColor,
                                                start = Offset(
                                                    0f,
                                                    size.height - (strokeWidthPx / 2)
                                                ),
                                                end = Offset(
                                                    size.width,
                                                    size.height - (strokeWidthPx / 2)
                                                ),
                                                strokeWidth = strokeWidthPx,
                                                pathEffect = dashPathEffect
                                            )
                                        }
                                ) {
                                }
                            }
                        }
                    }
                }

                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pageStatus
                ) { pageIndex ->
                    val androidLineList =
                        (screenStatus?.androidLineChartList ?: listOf()).getRangeData(
                            currentPage = pageIndex,
                            perPage = CHART_ITEMS_PER_PAGE
                        )

                    val alertLineList =
                        (screenStatus?.alertLineChartList ?: listOf()).getRangeData(
                            currentPage = pageIndex,
                            perPage = CHART_ITEMS_PER_PAGE
                        )

                    val cloudLineList =
                        (screenStatus?.cloudLineChartList ?: listOf()).getRangeData(
                            currentPage = pageIndex,
                            perPage = CHART_ITEMS_PER_PAGE
                        )

                    /*
                    * pointerInput 同時覆寫 detectTapGestures跟detectHorizontalDragGestures
                    * 會導致衝突只有先寫的才生效 故要同時生效需分寫2個pointerInput
                    * 為了讓折線圖的動畫每次切換頁面都會跑
                    * 故pageIndex == pageStatus.currentPage判斷 讓每次頁面為當頁才顯示
                    * 相當於關閉預加載的感覺
                    * */
                    if (pageIndex == pageStatus.currentPage) {
                        LineChartScreen(
                            modifier = Modifier
                                .zIndex(7f)
                                .fillMaxSize()
                                .background(Color.Transparent)
                                .pointerInput(columLine) {
                                    fun calculateIndex(xOffset: Float): Int? {
                                        if (xOffset < 0 || xOffset > chartBoxWidthPx) return null
                                        // 計算最近的中心點
                                        val indexFloat = xOffset / segmentWidthPx - 0.5f
                                        val index =
                                            indexFloat.roundToInt().coerceIn(0, columLine - 1)
                                        return index
                                    }

                                    detectTapGestures { offset: Offset ->
                                        selectedColumnIndex = calculateIndex(offset.x)
                                    }
                                },
                            androidLineFloatList = androidLineList,
                            alertLineFloatList = alertLineList,
                            cloudLineFloatList = cloudLineList,
                            pageIndex = pageIndex
                        )
                    }
                }

                // 2. 繪製拖曳指示線和資訊框 (包含圓圈)
                if (selectedColumnIndex != null) {
                    val columnIndex = selectedColumnIndex!!

                    // 計算指示線的精確 X 座標 (吸附到中心點)
                    val indicatorX = (columnIndex * segmentWidthPx) + (segmentWidthPx / 2f)

                    // 繪製指示線和訊息框 (DragIndicator 內部包含圓圈繪製邏輯)
                    // 圓圈繪製邏輯已在 DragIndicator 內實現
                    DragIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(10f),
                        currentPageDataList = currentResultList ?: listOf(),
                        columnIndex = columnIndex,
                        indicatorX = indicatorX,
                        gridHeight = chartBoxHeightPx,
                        gridWidth = chartBoxWidthPx,
                        androidPointList = androidPoints,
                        cloudPointList = cloudPoints,
                        alertPointList = alertPoints,
                        toolTipDateList = currentDateForHintList,
                    )
                }
            }

            // 圖表右邊文字
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .onGloballyPositioned { coordinates ->
                        rightLabelColumnWidthDp = with(density) { coordinates.size.width.toDp() }
                    }
                    .padding(horizontal = chartBorderTextHorizontalPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(rightColumTextList.size) { index ->
                    Text(
                        text = rightColumTextList.getOrNull(index) ?: "",
                        style = ChartTextStyle
                    )
                }
            }
        }

        // 圖表下方日期
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = leftLabelColumnWidthDp, end = rightLabelColumnWidthDp)
        ) {
            repeat(currentPageDateList?.size ?: 0) { bottomIndex ->
                Text(
                    modifier = Modifier.weight(1f),
                    text = currentPageDateList?.getOrNull(bottomIndex) ?: "",
                    style = ChartTextStyle
                )
            }

            // 不滿7個就填空白補
            if ((currentPageDateList?.size ?: 0) < CHART_ITEMS_PER_PAGE) {
                repeat(CHART_ITEMS_PER_PAGE - (currentPageDateList?.size ?: 0)) {
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun LineChartScreen(
    modifier: Modifier = Modifier,
    columLine: Int = CHART_ITEMS_PER_PAGE,
    androidLineFloatList: List<Float> = listOf(),
    cloudLineFloatList: List<Float> = listOf(),
    alertLineFloatList: List<Float> = listOf(),
    pageIndex: Int
) {
    Box(
        modifier = modifier
    ) {
        DrawLineChartWeight(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f),
            lineColor = ChartPointDataInfo.Android.lineColor,
            columLine = columLine,
            chartYaxisPointData = androidLineFloatList,
            pageIndex = pageIndex
        )

        DrawLineChartWeight(
            modifier = Modifier.fillMaxSize(),
            lineColor = ChartPointDataInfo.ALERT.lineColor,
            columLine = columLine,
            chartYaxisPointData = alertLineFloatList,
            pageIndex = pageIndex
        )

        DrawLineChartWeight(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f),
            lineColor = ChartPointDataInfo.CLOUD.lineColor,
            columLine = columLine,
            chartYaxisPointData = cloudLineFloatList,
            pageIndex = pageIndex
        )
    }
}

@Composable
fun DrawLineChartWeight(
    modifier: Modifier = Modifier,
    lineColor: Color = LocalCustomColors.current.pinkRed700,
    chartYaxisPointData: List<Float> = listOf(
        10f,
        20f,
        30f,
        50f,
        70f,
        80f,
        95f,
    ),
    columLine: Int, // X 軸分段數
    pageIndex: Int, // 當前頁碼，作為動畫重啟的依賴
) {
    val lineStrokeWidth = 1.mdp

    val animationProgress = remember { androidx.compose.animation.core.Animatable(0f) }

    // 線條動畫 每次重啟時啟動
    LaunchedEffect(chartYaxisPointData, columLine, pageIndex) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Canvas(
        modifier = modifier
    ) {
        val gridWidth = size.width
        val gridHeight = size.height

        // 計算每個網格區間的寬度 (X 軸)
        // 假設每個數據點對應網格中的一列中心
        val segmentWidth = gridWidth / columLine.toFloat()

        // 假設 Y 軸範圍為 0 到 100 (與 leftColumTextList 預設值對應)
        val maxValue = 100f
        val minValue = 0f
        val valueRange = maxValue - minValue

        // 計算所有數據點的座標
        val points = chartYaxisPointData.mapIndexed { index, float ->
            // X 座標: 區間中心點
            // index * segmentWidth 得到該區段左緣
            // + (segmentWidth / 2f) 得到該區段中心
            val x = (index * segmentWidth) + (segmentWidth / 2f)

            // Y 座標: 將數值 (0-100) 映射到 gridHeight (從上到下)
            val normalizedValue = (float.coerceIn(minValue, maxValue) - minValue) / valueRange
            // 1.0 - normalizedValue 是因為 Canvas 座標 (0,0) 在左上角
            val y = gridHeight * (1f - normalizedValue)

            Offset(x, y)
        }

        // 總共要繪製的線段數
        val totalSegments = points.size - 1

        // 根據動畫進度繪製折線和數據點
        // currentSegmentIndex: 正在繪製的線段的索引
        // segmentFraction: 當前線段繪製的比例 (0f 到 1f)
        val currentSegmentIndex =
            (animationProgress.value * totalSegments).toInt().coerceIn(0, totalSegments)
        val segmentFraction = (animationProgress.value * totalSegments) - currentSegmentIndex

        // 繪製折線和數據點
        points.forEachIndexed { index, point ->
            // 繪製折線
            if (index > 0) {
                val startPoint = points[index - 1]
                val endPoint = point

                if (index - 1 < currentSegmentIndex) {
                    drawLine(
                        color = lineColor,
                        start = points[index - 1],
                        end = point,
                        strokeWidth = lineStrokeWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                } else if (index - 1 == currentSegmentIndex && animationProgress.value < 1f) {
                    // 計算要繪製的線段終點 (利用 segmentFraction)
                    val dx = endPoint.x - startPoint.x
                    val dy = endPoint.y - startPoint.y

                    val animatedX = startPoint.x + dx * segmentFraction
                    val animatedY = startPoint.y + dy * segmentFraction

                    drawLine(
                        color = lineColor,
                        start = startPoint,
                        end = Offset(animatedX, animatedY),
                        strokeWidth = lineStrokeWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

/**
 * 預設一個不顯示但仍占據位子的Icon 避免畫面跳動
 * */
@Composable
fun ChartWeight(
    modifier: Modifier = Modifier,
    chartDataPerformancesList: List<BuffLabelTip> = listOf(),
) {
    val needOverlapIcon = chartDataPerformancesList.size >= 3
    Box(
        modifier = modifier.inVisible(chartDataPerformancesList.isEmpty()),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .then(
                    if (needOverlapIcon) {
                        Modifier.width(42.mdp)
                    } else {
                        Modifier
                    }
                )
                .background(
                    color = LocalCustomColors.current.blueGray300,
                    shape = RoundedCornerShape(4.mdp)
                )
                .padding(2.mdp)
        ) {
            chartDataPerformancesList.forEach {
                Icon(
                    modifier = Modifier.weight(1f),
                    painter = painterResource(it.buffLabelIconRes),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
        }
    }
}

/**
 * 繪製拖曳指示線和資訊框 (Tooltip)
 * **這個函數包含了在折線交叉點繪製圓圈的邏輯。**
 * @param currentPageDataList 所有的圖表數據 (用於顯示資訊)
 * @param columnIndex 當前選中的列索引
 * @param indicatorX 指示線的 X 座標 (像素值)
 * @param gridHeight 圖表網格的高度 (像素值)
 * @param gridWidth 圖表最大寬度 (像素值)
 */
@Composable
fun DragIndicator(
    modifier: Modifier = Modifier,
    currentPageDataList: List<PointInfoResult>,
    columnIndex: Int,
    indicatorX: Float,
    gridHeight: Float,
    gridWidth: Float, // 使用實際寬度
    androidPointList: List<Float>,
    cloudPointList: List<Float>,
    alertPointList: List<Float>,
    toolTipDateList: List<String>
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val strokeWidth = 1.mdp // 虛線寬度
    val dotRadiusDp = 3.mdp // 標記點的半徑
    val dotBorderDp = 1.mdp // 標記點外框寬度

    val data = currentPageDataList.getOrNull(columnIndex) ?: return
    val androidPoint = androidPointList.getOrNull(columnIndex) ?: 0f
    val cloudPoint = cloudPointList.getOrNull(columnIndex) ?: 0f
    val alertPoint = alertPointList.getOrNull(columnIndex) ?: 0f
    val date = toolTipDateList.getOrNull(columnIndex) ?: ""

    // toolTip樣式設定
    val toolTipArrowWidth = 14.mdp
    val toolTipArrowWidthPx = with(density) { toolTipArrowWidth.toPx() }

    // Tooltip 的內容
    val tooltipText = remember(data) {
        "${date}\n" +
                "Android：${data.android.toIntOrNull() ?: 0}\n" +
                "Cloud：${data.cloud.toIntOrNull() ?: 0}\n" +
                "Alert：${data.alert.toIntOrNull() ?: 0}"
    }

    // 排列順序越上面越先繪製(圖層越下)
    val lineDataPoints = remember(data) {
        listOf(
            Pair(alertPoint, ChartPointDataInfo.ALERT),
            Pair(cloudPoint, ChartPointDataInfo.CLOUD),
            Pair(androidPoint, ChartPointDataInfo.Android),
        )
    }

    // *** 新增狀態：用於儲存 Tooltip 實際測量到的寬度 ***
    var tooltipMeasuredWidthPx by remember { mutableFloatStateOf(0f) }
    val tooltipMeasuredWidthInt = tooltipMeasuredWidthPx.roundToInt() // 使用狀態中的測量寬度
    var tooltipMeasuredHeightPx by remember { mutableFloatStateOf(0f) }
    val tooltipMeasuredHeightInt = tooltipMeasuredHeightPx.roundToInt() // 使用狀態中的測量高度

    // Tooltip 的 X/Y 偏移 (使用 IntOffset 進行放置)
    // 調整 X 偏移，讓 Tooltip 位於指示線的右側或左側，並避開邊緣
    val toolTipAtLeftSide = indicatorX + toolTipArrowWidthPx + tooltipMeasuredWidthInt > gridWidth
    val adjustedX = if (toolTipAtLeftSide) {
        // 如果 Tooltip 會超出右邊界，則將其放在指示線的左側
        (indicatorX - tooltipMeasuredWidthInt - toolTipArrowWidthPx).roundToInt()
    } else {
        // 放在指示線的右側
        (indicatorX + toolTipArrowWidthPx).roundToInt()
    }

    // Tooltip Y 座標：基於第一個點，稍微向上偏移
    val adjustedY = ((gridHeight / 2) - (tooltipMeasuredHeightInt / 2)).roundToInt()

    Box(
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

            // 垂直線
            drawLine(
                color = Color.Black,
                start = Offset(indicatorX, 0f),
                end = Offset(indicatorX, size.height),
                strokeWidth = strokeWidth.toPx(),
            )

            // 繪製每個交點的圓點 <-- 這是圓圈標示的邏輯
            // 繪製水平虛線和交點圓點
            lineDataPoints.forEach { (value, lineInfo) ->
                val pointNormalizedValue = (value.coerceIn(0f, 100f) - 0f) / 100f
                val pointY = size.height * (1f - pointNormalizedValue)

                // 1. 繪製水平虛線: 從垂直線延伸到圖表右邊緣
                when (lineInfo.flag) {
                    ChartPointDataInfo.Android.flag -> {
                        drawLine(
                            color = lineInfo.lineColor, // 使用線條顏色
                            start = Offset(indicatorX, pointY), // 從垂直線開始
                            end = Offset(0f, pointY),    // 延伸到圖表右邊緣
                            strokeWidth = strokeWidth.toPx(),
                            pathEffect = dashPathEffect
                        )
                    }

                    else -> {
                        drawLine(
                            color = lineInfo.lineColor, // 使用線條顏色
                            start = Offset(indicatorX, pointY), // 從垂直線開始
                            end = Offset(size.width, pointY),    // 延伸到圖表右邊緣
                            strokeWidth = strokeWidth.toPx(),
                            pathEffect = dashPathEffect
                        )
                    }
                }

                // 2. 繪製交點圓點
                // 外層圓圈
                drawCircle(
                    color = Color.White,
                    center = Offset(indicatorX, pointY),
                    radius = (dotRadiusDp + dotBorderDp).toPx(),
                    style = androidx.compose.ui.graphics.drawscope.Fill
                )
                // 內側點
                drawCircle(
                    color = lineInfo.lineColor,
                    center = Offset(indicatorX, pointY),
                    radius = dotRadiusDp.toPx(),
                    style = androidx.compose.ui.graphics.drawscope.Fill
                )
            }
        }

        // toolTip
        PointedBackground(
            modifier = Modifier
                .offset {
                    // 使用 IntOffset 放置在計算好的位置
                    IntOffset(adjustedX, adjustedY)
                }
                .onGloballyPositioned { coordinates ->
                    // 只有當寬度改變時才觸發更新，以減少不必要的重組
                    if (tooltipMeasuredWidthPx != coordinates.size.width.toFloat()) {
                        tooltipMeasuredWidthPx = coordinates.size.width.toFloat()
                    }

                    if (tooltipMeasuredHeightPx != coordinates.size.height.toFloat()) {
                        tooltipMeasuredHeightPx = coordinates.size.height.toFloat()
                    }
                },
            cornerRadius = 10.mdp,
            triangleHeight = toolTipArrowWidth,
            radiusTriangle = true,
            pointerPosition = if (toolTipAtLeftSide) {
                PointerPosition.RIGHT
            } else {
                PointerPosition.LEFT
            },
            contentPadding = PaddingValues(vertical = 15.mdp, horizontal = 10.mdp),
            backgroundColor = LocalCustomColors.current.darkBlue800
        ) {
            Text(
                text = tooltipText,
                style = ChartToolTipStyle,
            )
        }
    }
}
