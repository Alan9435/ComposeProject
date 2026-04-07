package com.example.composeproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.composeproject.data.LineChartScreenState
import com.example.composeproject.data.PointInfoResult
import com.example.composeproject.data.fakeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.round
import kotlin.math.roundToInt

class LineChartViewModel: ViewModel() {

    companion object {
        // 圖表中每頁顯示多少筆資料
        const val CHART_ITEMS_PER_PAGE = 7
    }

    private val _screenState = MutableStateFlow(LineChartScreenState())
    val screenState = _screenState.asStateFlow()

    init {
        calculateCharData(
            fakeData
        )
    }

    private fun calculateCharData(chartDataList: ArrayList<PointInfoResult>? = null) {
        // 獲取列表，如果為 null 則返回空列表
        val list = chartDataList ?: arrayListOf()

        // 如果列表為空，則返回所有最大值為 0 的結果
        if (list.isEmpty()) {
            return
        }

        val defaultMinValue = 4 // 怕數值不夠分這麼多階段 所以最低預設為4

        // 計算總共有幾頁圖表
        val quotient = (list.size) / CHART_ITEMS_PER_PAGE
        val remainder = (list.size) % CHART_ITEMS_PER_PAGE
        val totalPage = if (remainder > 0) {
            quotient + 1
        } else {
            quotient
        }

        // format圖表底部日期
        val dateList = list.map { formatDateToMMDD(it.date) }

        // 計算圖表左右的數值
        val maxAndroidCount = list.maxOfOrNull {
            (it.android.toIntOrNull() ?: defaultMinValue).coerceAtLeast(defaultMinValue)
        } ?: defaultMinValue
        val cloudValues = list.map {
            (it.cloud.toIntOrNull() ?: defaultMinValue).coerceAtLeast(defaultMinValue)
        }
        val alertValues =
            list.map { (it.alert.toIntOrNull() ?: defaultMinValue).coerceAtLeast(defaultMinValue) }
        // 合併 cloud 和 alert 的值列表
        val combinedValues = cloudValues + alertValues
        // 從合併後的列表中找到最大值，如果列表為空則為 4
        val maxApplyOrChat = combinedValues.maxOrNull() ?: defaultMinValue

        val androidDataList =
            list.map { it.android.toIntOrNull() ?: 0 }.calculatePercentages(maxValue = maxAndroidCount)
        val cloudDataList =
            list.map { it.cloud.toIntOrNull() ?: 0 }.calculatePercentages(maxValue = maxApplyOrChat)
        val alertDataList =
            list.map { it.alert.toIntOrNull() ?: 0 }.calculatePercentages(maxValue = maxApplyOrChat)
        val dateForHint = list.map { it.date }

        _screenState.update {
            it.copy(
                chartViewList = calculatePercentageSteps(maxAndroidCount),
                chartApplyOrTalkList = calculatePercentageSteps(maxApplyOrChat),
                totalChartPage = totalPage,
                chartDateList = dateList,
                androidLineChartList = androidDataList,
                cloudLineChartList = cloudDataList,
                alertLineChartList = alertDataList,
                dateForHint = dateForHint,
                pointInfoResult = list
            )
        }
    }

    private fun calculatePercentageSteps(maxValue: Int): List<String> {
        // 確保輸入值非負
        val value = if (maxValue < 0) 0 else maxValue

        // 定義百分比列表並計算對應的數值
        // 使用 .toFloat() 進行浮點數運算，並使用 roundToInt() 確保四捨五入
        val percentages = listOf(1.0f, 0.75f, 0.50f, 0.25f, 0.0f)

        return percentages.map { percent ->
            (value * percent).roundToInt().toString()
        }
    }

    private fun List<Int>.calculatePercentages(maxValue: Int): List<Float> {
        // 1. 處理最大值為 0 或負數的邊界情況
        if (maxValue <= 0) {
            // 如果最大值無效，返回一個與輸入列表大小相同，但所有值為 0.0f 的列表
            return this.map { 0.0f }
        }

        // 2. 使用 map 迭代並計算每個元素
        return this.map { compareValue ->
            // 處理單個元素的計算邏輯

            // 處理比較值為負數的情況
            val safeCompareValue = compareValue.coerceAtLeast(0).coerceAtMost(maxValue)

            // 計算比例 (0.0 - 1.0)
            // 必須使用 toFloat() 進行浮點數除法
            val ratio = safeCompareValue.toFloat() / maxValue.toFloat()

            // 轉換為百分比 (0.0 - 100.0)
            val percentage = ratio * 100.0f

            // 四捨五入到最接近的整數，並轉換為 Float (例如 75.0f, 23.0f)
            round(percentage)
        }
    }

    private fun formatDateToMMDD(dateString: String): String {
        return dateString
            .substringBefore(' ') // 取得 "YYYY/MM/DD" 部分
            .split('/')          // 拆分成 ["YYYY", "MM", "DD"]
            .let { parts ->
                // 檢查是否至少有三部分（年、月、日）
                if (parts.size >= 3) {
                    // 組合 "MM/DD" (parts[1] 是月, parts[2] 是日)
                    "${parts[1]}/${parts[2]}"
                } else {
                    dateString // 如果格式不正確，則保持原樣
                }
            }
    }
}