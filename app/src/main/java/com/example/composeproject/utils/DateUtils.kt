package com.example.composeproject.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import java.util.Calendar


/**
 * 限制只能選系統日期前的日期
 * */
@OptIn(ExperimentalMaterial3Api::class)
fun getCancelFutureSelectableDates(): SelectableDates {
    return object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis <= System.currentTimeMillis()
        }

        override fun isSelectableYear(year: Int): Boolean {
            val calendar = Calendar.getInstance()
            return year <= calendar.weekYear
        }
    }
}

