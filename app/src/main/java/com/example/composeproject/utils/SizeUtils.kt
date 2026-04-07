package com.example.composeproject.utils

import android.content.Context
import androidx.annotation.DimenRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


fun Context.getDpBySdp(@DimenRes res: Int): Dp {
    return (resources.getDimension(res) / resources.displayMetrics.density).dp
}

private const val REFERENCE_SCREEN_WIDTH_DP = 375f // 看設計搞件的螢幕寬度是多少

/**
 * 確保在各裝置顯示比例沒落差太大
 * */
val Int.mdp: Dp
    @Composable
    get() = with(LocalDensity.current) {
        val configuration = LocalConfiguration.current
        val currentScreenWidthDp = configuration.screenWidthDp.toFloat()

        // 計算縮放因子：當前螢幕寬度 / 參考螢幕寬度
        val scaleFactor = currentScreenWidthDp / REFERENCE_SCREEN_WIDTH_DP
        (this@mdp * scaleFactor).dp
    }

/**
 * 確保在各裝置顯示比例沒落差太大
 * */
val Int.msp: TextUnit
    @Composable
    get() = with(LocalDensity.current) {
        // 獲取當前裝置的配置資訊 (包括螢幕寬度)
        val configuration = LocalConfiguration.current
        val currentScreenWidthDp = configuration.screenWidthDp.toFloat()

        // 計算縮放因子：當前螢幕寬度 / 參考螢幕寬度
        val scaleFactor = currentScreenWidthDp / REFERENCE_SCREEN_WIDTH_DP
        (this@msp * scaleFactor).sp
    }

/**
 * 確保在各裝置顯示比例沒落差太大
 * */
val Double.mdp: Dp
    @Composable
    get() = with(LocalDensity.current) {
        val configuration = LocalConfiguration.current
        val currentScreenWidthDp = configuration.screenWidthDp.toFloat()

        // 計算縮放因子：當前螢幕寬度 / 參考螢幕寬度
        val scaleFactor = currentScreenWidthDp / REFERENCE_SCREEN_WIDTH_DP
        (this@mdp * scaleFactor).dp
    }

/**
 * 確保在各裝置顯示比例沒落差太大
 * */
val Double.msp: TextUnit
    @Composable
    get() = with(LocalDensity.current) {
        // 獲取當前裝置的配置資訊 (包括螢幕寬度)
        val configuration = LocalConfiguration.current
        val currentScreenWidthDp = configuration.screenWidthDp.toFloat()

        // 計算縮放因子：當前螢幕寬度 / 參考螢幕寬度
        val scaleFactor = currentScreenWidthDp / REFERENCE_SCREEN_WIDTH_DP
        (this@msp * scaleFactor).sp
    }