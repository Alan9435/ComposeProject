package com.example.composeproject.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.composeproject.R
import com.example.composeproject.data.ScaleExampleData.Companion.getDataList

data class HorizontalPagerScaleScreenState(
    val itemList: List<ScaleExampleData> = emptyList(),
    val pageCount: Int = 0
)

sealed class ScaleExampleData(
    @DrawableRes val iconRes: Int,
    @StringRes val stringRes: Int
) {
    companion object {
        private val valuesList by lazy {
            listOf(ExampleAddAlert, ExampleAirlineSeatFlat, ExampleAndroid, ExampleBlind, ExampleCloud)
        }

        fun getDataList(): List<ScaleExampleData> {
            return valuesList
        }
    }

    data object ExampleAddAlert : ScaleExampleData(
        iconRes = R.drawable.ic_baseline_add_alert_filled_24,
        stringRes = R.string.horizontal_pager_scale_item_add_alert
    )

    data object ExampleAirlineSeatFlat : ScaleExampleData(
        iconRes = R.drawable.ic_baseline_airline_seat_flat_angled_filled_24,
        stringRes = R.string.horizontal_pager_scale_item_airline_seat_flat
    )

    data object ExampleAndroid : ScaleExampleData(
        iconRes = R.drawable.ic_baseline_android_filled_24,
        stringRes = R.string.horizontal_pager_scale_item_android
    )

    data object ExampleBlind : ScaleExampleData(
        iconRes = R.drawable.ic_baseline_blind_filled_24,
        stringRes = R.string.horizontal_pager_scale_item_blind
    )

    data object ExampleCloud : ScaleExampleData(
        iconRes = R.drawable.ic_baseline_cloud_24,
        stringRes = R.string.horizontal_pager_scale_item_cloud
    )
}