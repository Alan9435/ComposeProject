package com.example.composeproject.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.composeproject.R
import com.example.composeproject.data.BuffLabelTip.Companion.ALERT_BUFF_FLAG
import com.example.composeproject.data.BuffLabelTip.Companion.ANDROID_BUFF_FLAG
import com.example.composeproject.data.BuffLabelTip.Companion.CLOUD_BUFF_FLAG
import com.example.composeproject.data.BuffLabelTip.Companion.getAllBuffFlag
import com.example.composeproject.ui.theme.Colors
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.math.min
import kotlin.random.Random

data class LineChartScreenState(
    val totalChartPage: Int = 0,
    val chartViewList: List<String> = listOf(),
    val chartApplyOrTalkList: List<String> = listOf(),
    val chartDateList: List<String> = listOf(),
    val androidLineChartList: List<Float> = listOf(),
    val cloudLineChartList: List<Float> = listOf(),
    val alertLineChartList: List<Float> = listOf(),
    val dateForHint: List<String> = listOf(),
    val pointInfoResult: List<PointInfoResult> = listOf()
)


fun <T> List<T>.getRangeData(currentPage: Int, perPage: Int): List<T> {
    // 檢查每頁數量是否有效
    if (perPage <= 0) return emptyList()

    // 1. 計算起始索引
    val startIndex = currentPage * perPage

    // 2. 邊界檢查：如果起始索引已經超出列表範圍，直接返回空列表
    if (startIndex >= this.size) {
        return emptyList()
    }

    // 3. 計算結束索引（使用 min() 確保不超出列表邊界）
    val endIndex = min(startIndex + perPage, this.size)

    // 4. 使用 subList 進行切割
    return this.subList(startIndex, endIndex)
}


data class PointInfoResult(
    val date: String = "",
    val android: String = "",
    val cloud: String = "",
    val alert: String = "",
    val buffs: List<String> = listOf()
)

sealed class BuffLabelTip(
    @DrawableRes val buffLabelIconRes: Int,
    val flag: String
) {
    companion object {
        const val ANDROID_BUFF_FLAG = "ANDROID_BUFF_FLAG"
        const val CLOUD_BUFF_FLAG = "CLOUD_BUFF_FLAG"
        const val ALERT_BUFF_FLAG = "ALERT_BUFF_FLAG"

        private val ALL_STATUS: List<BuffLabelTip> = listOf(
            AndroidBuff,
            CloudBuff,
            AlertBuff,
        )

        private val valuesMap: Map<String, BuffLabelTip> =
            ALL_STATUS.associateBy { it.flag }

        fun getAllBuffFlag() = listOf(ANDROID_BUFF_FLAG, CLOUD_BUFF_FLAG, ALERT_BUFF_FLAG)

        fun getBuffLabelByFlag(str: String): BuffLabelTip? {
            return valuesMap[str]
        }

        fun getBuffLabelByFlagList(list: List<String>): List<BuffLabelTip> {
            return list.mapNotNull { flagString ->
                getBuffLabelByFlag(flagString)
            }
        }
    }

    data object AndroidBuff : BuffLabelTip(
        buffLabelIconRes = R.drawable.ic_baseline_android_filled_24,
        flag = ANDROID_BUFF_FLAG
    )

    data object CloudBuff : BuffLabelTip(
        buffLabelIconRes = R.drawable.ic_baseline_cloud_24,
        flag = CLOUD_BUFF_FLAG
    )

    data object AlertBuff : BuffLabelTip(
        buffLabelIconRes = R.drawable.ic_baseline_add_alert_filled_24,
        flag = ALERT_BUFF_FLAG
    )

}

enum class ChartPointDataInfo(
    val flag: String,
    val lineColor: Color
) {
    Android(flag = ANDROID_BUFF_FLAG, lineColor = Colors().pinkRed700),
    CLOUD(flag = CLOUD_BUFF_FLAG, lineColor = Colors().blue700),
    ALERT(flag = ALERT_BUFF_FLAG, lineColor = Colors().green),
}

val fakeData = generateFakeData(20)

fun generateFakeData(count: Int): ArrayList<PointInfoResult> {
    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    val calendar = Calendar.getInstance().apply {
        set(2026, Calendar.OCTOBER, 24)
    }
    val allBuffs = getAllBuffFlag()
    val list = ArrayList<PointInfoResult>()
    for (i in 0 until count) {
        val takeCount = Random.nextInt(1, 4)
        val randomBuffs = allBuffs.shuffled().take(takeCount)

        list.add(
            PointInfoResult(
                date = sdf.format(calendar.time),
                android = Random.nextInt(5000, 30001).toString(),
                cloud = Random.nextInt(0, 1001).toString(),
                alert = Random.nextInt(0, 1001).toString(),
                buffs = randomBuffs
            )
        )
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }
    return list
}

//LineChartScreenState(
//    chartDateList = listOf(
//        "10/24",
//        "10/25",
//        "10/26",
//        "10/27",
//        "10/28",
//        "10/29",
//        "10/30",
//        "10/31",
//        "11/01",
//        "11/02",
//        "11/03",
//        "11/04",
//        "11/05",
//        "11/06",
//        "11/07",
//        "11/08",
//        "11/09",
//        "11/10",
//        "11/11",
//        "11/12"
//    ),
//    androidLineChartList = List(20) { Random.nextFloat() * 100f },
//    cloudLineChartList = List(20) { Random.nextFloat() * 100f },
//    alertLineChartList = List(20) { Random.nextFloat() * 100f },
//    dateForHint =,
//    pointInfoResult =,
//)

