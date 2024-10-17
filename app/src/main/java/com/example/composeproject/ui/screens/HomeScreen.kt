package com.example.composeproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeproject.viewmodel.MainActivityViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: MainActivityViewModel) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column() {
        //todo 被棄用 查詢替代方案
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            count = 4,
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> {
                    Column {
                        TopBar(
                            onRightClick = {
                                viewModel.isDarkTheme = !viewModel.isDarkTheme
                            }
                        )
                        ChatList(viewModel.chats)
                    }
                }

                1 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Green)
                    ) {
                        Text(text = "經費不足畫面2")
                    }
                }

                2 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Gray)
                    ) {
                        Text(text = "經費不足畫面3")
                    }
                }

                3 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.DarkGray)
                    ) {
                        Text(text = "經費不足畫面4")
                    }
                }
            }
        }

        BottomBar(selected = viewModel.selectedTab, onSelectedChange = { position ->
            viewModel.selectedTab = position
            coroutineScope.launch {
                pagerState.animateScrollToPage(position)
            }
        })
    }
}

@Preview(
    showBackground = true
)
@Composable
fun HomeScreenPreview() {
    val viewModel: MainActivityViewModel = viewModel()
    HomeScreen(viewModel = viewModel)
}