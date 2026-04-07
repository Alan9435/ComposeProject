package com.example.composeproject.example

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.composeproject.utils.getDpBySdp
import ir.kaaveh.sdpcompose.sdp

@Composable
private fun DatePickerExampleScreen() {
    val context = LocalContext.current
    val monthList = (1..12).toList()
    /*
    * 31 -> 1,3,5,7,8,10,12
    * 28,29 -> 2
    * 30 -> 4,6,9,11
    * */
    var dateList by remember {
        mutableStateOf((1..31).toList())
    }

    var currentMonth by remember {
        mutableIntStateOf(2)
    }

    var currentDate by remember {
        mutableIntStateOf(31)
    }

    val monthListState = rememberLazyListState()
    val dateListState = rememberLazyListState()
    val currentMonthIndex by remember { derivedStateOf { monthListState.firstVisibleItemIndex + 1 } }
    var isAnimating by remember { mutableStateOf(false) }

    val itemHeight = 30.sdp

    LaunchedEffect(monthList) {
        // 靠近無限數中間
        val targetIndex =
            (currentMonth - 2) + monthList.size * (Int.MAX_VALUE / (monthList.size * monthList.size / 2))

        monthListState.scrollToItem(targetIndex)
    }

    LaunchedEffect(dateList) {
        Log.d("**********", "TestDatePicker: ${currentDate} || ${dateList.last()}")
        val targetDateIndex =
            if (0 > dateList.last()) {
                (dateList.last() - 2) + dateList.size * (Int.MAX_VALUE / (dateList.size * dateList.size / 2))
            } else {
                (currentDate - 2) + dateList.size * (Int.MAX_VALUE / (dateList.size * dateList.size / 2))
            }

        dateListState.scrollToItem(targetDateIndex)
    }

    LaunchedEffect(monthListState.isScrollInProgress) {
        if (!monthListState.isScrollInProgress) {
            monthListState.animateScrollToItem(monthListState.firstVisibleItemIndex)

            currentMonth =
                monthList[(monthListState.firstVisibleItemIndex + 1) % monthList.size]

            dateList = when (currentMonth) {
                1, 3, 5, 7, 8, 10, 12 -> {
                    (1..31).toList()
                }

                4, 6, 9, 11 -> {
                    (1..30).toList()
                }

                2 -> {
//                        (1..29).toList()
                    (1..28).toList()
                }

                else -> {
                    listOf()
                }
            }
        }
    }

    LaunchedEffect(dateListState.isScrollInProgress) {
        if (!dateListState.isScrollInProgress && !isAnimating) {
            dateListState.animateScrollToItem(dateListState.firstVisibleItemIndex)
            currentDate = dateList[(dateListState.firstVisibleItemIndex + 1) % dateList.size]
        }
    }

    Row {
        Column {
            ConstraintLayout(
                modifier = Modifier
                    .height(90.sdp)
                    .width(50.sdp)
                    .background(Color.Red)
            ) {
                val (monthLazyColum, monthDiv) = createRefs()

                LazyColumn(
                    modifier = Modifier
                        .constrainAs(monthLazyColum) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .fillMaxHeight()
                        .width(50.sdp),
                    state = monthListState,
                ) {
                    items(count = Int.MAX_VALUE) { index: Int ->
                        val item = monthList[index % monthList.size]
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeight)
                                .alpha(
                                    if (currentMonthIndex == index) {
                                        1f
                                    } else {
                                        0.5f
                                    }
                                )
                                .graphicsLayer {
                                    rotationX = when {
                                        currentMonthIndex > index -> {
                                            -50f
                                        }

                                        currentMonthIndex < index -> {
                                            50f
                                        }

                                        else -> {
                                            0f
                                        }
                                    }
                                },
                            text = "$item",
                            textAlign = TextAlign.Center,
                            color = if (currentMonthIndex == index) {
                                Color.Black
                            } else {
                                Color.Gray
                            }
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .constrainAs(monthDiv) {
                            top.linkTo(
                                parent.top,
                                margin = itemHeight - context.getDpBySdp(com.intuit.sdp.R.dimen._10sdp)
                            )
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .width(50.sdp)
                        .clip(shape = RoundedCornerShape(8.sdp)),
                    thickness = 5.sdp,
                    color = Color.Green
                )
            }

            Text(text = currentMonth.toString())
        }

        // 日
        Column {
            ConstraintLayout(
                modifier = Modifier
                    .height(90.sdp)
                    .width(50.sdp)
                    .background(Color.Red)
            ) {
                val (dateLazyColum, dateDiv) = createRefs()

                LazyColumn(
                    modifier = Modifier
                        .constrainAs(dateLazyColum) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                        }
                        .fillMaxHeight()
                        .width(50.sdp),
                    state = dateListState,
                ) {
                    items(count = Int.MAX_VALUE) { index: Int ->
                        val item = dateList[index % dateList.size]
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeight),
                            text = "$item",
                            textAlign = TextAlign.Center
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .constrainAs(dateDiv) {
                            top.linkTo(
                                parent.top,
                                margin = itemHeight - context.getDpBySdp(com.intuit.sdp.R.dimen._10sdp)
                            )
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .width(50.sdp)
                        .clip(shape = RoundedCornerShape(8.sdp)),
                    thickness = 5.sdp,
                    color = Color.Green
                )
            }

            Text(text = currentDate.toString())
        }
    }
}