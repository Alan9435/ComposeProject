package com.example.composeproject.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import com.example.composeproject.R
import com.example.composeproject.utils.mdp
import com.example.composeproject.utils.msp
import com.example.composeproject.ui.theme.LocalCustomColors
import kotlin.math.roundToInt

/**
 * 重要 -> verticalItemSpacing 加上後導致下拉刷新極難觸發 先改用內部item自己padding距離
 * @param pullToRefreshState 下拉刷新的狀態 e.g. val pullToRefreshState = rememberPullToRefreshState()
 * @param gridState e.g. val gridState = rememberLazyStaggeredGridState()
 * @param columnsItem 每行顯示幾個
 * @param contentPadding 整個列表的內容Padding
 * @param horizontalArrangement 每個區塊的水平距離
 * @param reloadWeight 下拉刷新的元件 any @Composable, 被包裹在Box置中
 * @param isReloading 當前是否為下拉刷新中的狀態 e.g. var onListLoading = mutableStateOf(false)
 * @param onReload 當下拉刷新發生時要做的動作 e.g. viewModel.reloadMyData()
 * @param dataItems 列表元件 any item {}, items(){}
 * @param loadMoreWeight 載入更多時的原件 any @Composable
 * @param isLoadingMore 當前是否為載入更多的狀態
 * */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CommonLazyVerticalStaggeredGrid(
    modifier: Modifier = Modifier,
    pullToRefreshState: PullToRefreshState,
    enabledPullToRefresh: Boolean = true,
    gridState: LazyStaggeredGridState,
    columnsItem: Int = 2,
    contentPadding: PaddingValues = PaddingValues(0.mdp, 0.mdp),
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(8.mdp),
    reloadWeight: @Composable (mPullToRefreshState: PullToRefreshState, isRefreshing: Boolean) -> Unit = { mPullToRefreshState, isRefreshing ->
        DefaultReloadScreen(Modifier, mPullToRefreshState, isRefreshing)
    },
    isReloading: Boolean,
    onReload: () -> Unit = {},
    loadMoreWeight: @Composable () -> Unit = { DefaultLoadMoreScreen() },
    isLoadingMore: Boolean,
    dataItems: LazyStaggeredGridScope.() -> Unit = { exampleItems() }, // exampleItems() , LazyStaggeredGridScope.()
) {
    // 要滑動多少才會觸發下拉刷新
    val reLoadThreshold = 90.mdp

    LazyVerticalStaggeredGrid(
        modifier = modifier
            .pullToRefresh(
                enabled = enabledPullToRefresh,
                state = pullToRefreshState,
                threshold = reLoadThreshold,
                isRefreshing = isReloading,
                onRefresh = {
                    onReload.invoke()
                }
            ),
        state = gridState,
        columns = StaggeredGridCells.Fixed(columnsItem),
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement
    ) {
        // ReloadWeight
        item(span = StaggeredGridItemSpan.FullLine) {
            Box(
                Modifier
                    .height(
                        (pullToRefreshState.distanceFraction * 90).roundToInt().mdp
                    )
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                reloadWeight(
                    pullToRefreshState, isReloading
                )
            }
        }

        // data items
        dataItems()

        // LoadMoreWeight
        if (isLoadingMore) {
            item(span = StaggeredGridItemSpan.FullLine) {
                loadMoreWeight()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultReloadScreen(modifier: Modifier, pullToRefreshState: PullToRefreshState, isRefreshing: Boolean) {
    val thresholdReached = pullToRefreshState.distanceFraction >= 1f

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (isRefreshing) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.mdp),
                color = LocalCustomColors.current.pinkRed600,
                strokeCap = StrokeCap.Round // 尖端的形狀
            )
        }

        // 間隔
        Spacer(Modifier.width(20.mdp))

        Text(
            modifier = Modifier.padding(start = 4.mdp),
            text = if(thresholdReached) "放開就更新瞜 (觸發onReload())" else "在往下拉一點",
            style = TextStyle(
                fontSize = 16.msp,
                color = Color.LightGray,
                fontWeight = FontWeight.Normal,
                lineHeight = 1.5.em,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None
                ),
                textAlign = TextAlign.Start
            )
        )
    }
}

@Composable
fun DefaultLoadMoreScreen(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(40.mdp)
            .fillMaxWidth()
            .background(color = Color.Red),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.padding(start = 8.mdp),
            text = "讀取中",
            fontSize = 18.msp,
        )
    }
}

fun LazyStaggeredGridScope.exampleItems(modifier: Modifier = Modifier) {
    for (i in 1..30) {
        item {
            Column(
                modifier = modifier
                    .padding(
                        vertical = 3.mdp
                    )
                    .clip(RoundedCornerShape(6.mdp))
                    .background(
                        color = when (i % 3) {
                            0 -> {
                                LocalCustomColors.current.pinkRed200
                            }

                            1 -> {
                                LocalCustomColors.current.pinkRed50
                            }

                            2 -> {
                                LocalCustomColors.current.blueGray300
                            }

                            else -> {
                                LocalCustomColors.current.darkBlue200
                            }
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(50.mdp),
                    painter = painterResource(id = R.drawable.ic_baseline_celebration_24),
                    contentDescription = ""
                )

                for (i in 0..i) {
                    Text("測試123")
                }
            }
        }
    }
}