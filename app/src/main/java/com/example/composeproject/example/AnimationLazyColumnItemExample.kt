package com.example.composeproject.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.composeproject.R
import com.example.composeproject.utils.mdp
import com.example.composeproject.viewmodel.MainActivityViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationLazyColumnItemExampleScreen(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = viewModel()
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    // 定義Lottie組件及監聽狀態
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_running))

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        /*
            listState.firstVisibleItemIndex -> 畫面中可見的第一項index
            listState.layoutInfo.visibleItemsInfo.size -> 畫面中顯示的項目數
            2者相加 = 最底 開始loadmore
         */

        Button(
            onClick = {
            viewModel.fetchData()
        }) {
            Text("點我看效果")
        }

        /*
        * threshold 域值 -> 下拉多少距離後會被當作進入刷新 本例相當於60 mdp時刷新 未超過就彈回去
        * isRefreshing -> 控制該組件是否正在刷新
        * onRefresh -> 刷新時執行
        * */
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .pullToRefresh(
                    state = pullToRefreshState,
                    threshold = 60.mdp,
                    isRefreshing = viewModel.isListReLoading,
                    onRefresh = {
                        viewModel.fetchDataByReload()
                    }
                ), state = listState) {
            // reLoad View
            item {
                /*
                * 高度取下拉狀態(pullToRefreshState) 隨著變化
                * distanceFraction 會介於0f~2f 之間 1f時相當於進入刷新觸發 onRefresh
                * */
                Column(
                    modifier = Modifier
                        .height(
                            (pullToRefreshState.distanceFraction * 120).roundToInt().mdp
                        )
                        .fillMaxWidth()
                        .background(Color.Gray),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LottieAnimation(
                        modifier = Modifier.size(80.mdp),
                        composition = composition,
                        isPlaying = viewModel.isListReLoading,
                        iterations = LottieConstants.IterateForever
                    )

                    // 判斷還未達到1f時顯示下拉提示文字
                    Text(
                        text = if (pullToRefreshState.distanceFraction >= 1f) "放開就刷新了歐" else "在下拉一點點",
                        textAlign = TextAlign.Center
                    )
                }
            }

            itemsIndexed(viewModel.listData) { index, item ->
                AnimatedVisibility(
                    visible = viewModel.loadingFinish,
                    enter = slideInHorizontally(
                        animationSpec = tween(200 * (index + 1)),
                    ) { fullWidth ->
                        fullWidth
                    },
                    exit = slideOutHorizontally(
                        animationSpec = tween(200)
                    ) { fullWidth ->
                        fullWidth
                    }
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.mdp)
                            .padding(16.mdp)
                            .background(Color.Red)
                            .clickable {

                            },
                        text = item.title
                    )
                }
            }
        }
    }
}