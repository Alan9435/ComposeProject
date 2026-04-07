package com.example.composeproject.ui.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.example.composeproject.ui.modifier.notRippleClickable
import com.example.composeproject.ui.theme.Gray70
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


const val DateFormat_yyyyMMdd_Slash = "yyyy/MM/dd"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    selectableDates: SelectableDates = DatePickerDefaults.AllDates,
    cancelText: String = "t1",
    confirmText: String = "t2",
    onCancelClick: () -> Unit = {},
    onConfirmClick: (date: String) -> Unit = {},
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = selectableDates // getCancelFutureSelectableDates()
    )

    val currentDate by remember {
        derivedStateOf {
            getDateMillisToFormattedString(
                datePickerState.selectedDateMillis,
                DateFormat_yyyyMMdd_Slash
            )
        }
    }

    // 背後遮罩
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Gray70)
        .zIndex(4f)
        .notRippleClickable {
            Log.d("******", "click: in")
        }) {
        Box(
            modifier = Modifier
                .scale(0.9f)
                .clip(RoundedCornerShape(16.sdp))
                .background(color = Color.White)
                .padding(top = 10.sdp, bottom = 15.sdp)
                .zIndex(5f)
                .align(Alignment.Center)
                .notRippleClickable {
                    Log.d("******", "內: in")
                }
        ) {
            Text(
                modifier = Modifier
                    .zIndex(3f)
                    .fillMaxWidth()
                    .padding(bottom = 20.sdp, start = 20.sdp),
                text = currentDate,
                fontSize = 19.ssp
            )

            DatePicker(
                modifier = Modifier.padding(top = 20.sdp, bottom = 27.sdp),
                state = datePickerState,
                title = null,
                headline = null,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White
                )
            )

            Row(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 19.sdp), verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    modifier = Modifier
                        .notRippleClickable {
                            onCancelClick.invoke()
                        }
                        .padding(end = 10.sdp)
                        .padding(horizontal = 10.sdp),
                    text = cancelText,
                    fontSize = 13.ssp,
                )

                Text(
                    modifier = Modifier
                        .notRippleClickable {
                            onConfirmClick.invoke(currentDate)
                        }
                        .padding(horizontal = 10.sdp),
                    text = confirmText,
                    fontSize = 13.ssp,
                )
            }
        }
    }

}

fun getDateMillisToFormattedString(millis: Long?, formatType: String): String {
    return if (millis == null) {
        "請選擇日期"
    } else {
        SimpleDateFormat(formatType, Locale.getDefault()).format(Date(millis))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewDate() {
    CustomDatePicker()
}