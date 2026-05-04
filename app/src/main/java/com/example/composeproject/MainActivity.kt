package com.example.composeproject

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composeproject.utils.mdp
import com.example.composeproject.utils.msp
import com.example.composeproject.data.HomeScreenState
import com.example.composeproject.data.ScreenFlag
import com.example.composeproject.example.AnimationLazyColumnItemExampleScreen
import com.example.composeproject.example.CommonLazyVerticalStaggeredGrid
import com.example.composeproject.example.ContextualFlowRowExampleScreen
import com.example.composeproject.example.CustomBottomSheetExampleScreen
import com.example.composeproject.example.HorizontalPagerScaleExampleScreen
import com.example.composeproject.example.LineChartExampleScreen
import com.example.composeproject.example.ModalBottomSheetExampleScreen
import com.example.composeproject.example.MultipleAnimationExampleScreen
import com.example.composeproject.ui.common.LoadingMask
import com.example.composeproject.ui.common.SwipeExampleScreen
import com.example.composeproject.ui.modifier.delayClick
import com.example.composeproject.ui.screens.TopBar
import com.example.composeproject.ui.theme.CustomTheme
import com.example.composeproject.ui.theme.LocalCustomColors
import com.example.composeproject.viewmodel.MainActivityViewModel

/* Compose中越常重刷新的元件
* 建議寫在越下面 效能會比較好
* Compose 多重嵌套不影響性能
*
* 抽離Composable時 盡量別超過1個layout
* 否則外部呼叫時 很難控制內部組件
* */
class MainActivity : ComponentActivity() {
    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // android 15會預設開啟 navigation bar 對比
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            CustomTheme {
                val gridState = rememberLazyStaggeredGridState()
                val pullToRefreshState = rememberPullToRefreshState()
                val homeScreenState by viewModel.homeScreenState.collectAsStateWithLifecycle()

                LoadingMask(show = viewModel.isListLoading)

                Column(
                    Modifier.fillMaxSize()
                        .background(LocalCustomColors.current.white)
                        .statusBarsPadding()
                ) {
                    TopBar(
                        title = stringResource(homeScreenState.currentScreenFlag.titleRes),
                        leftIcon = R.drawable.ic_baseline_arrow_back_ios_new_24,
                        onLeftClick = if(homeScreenState.currentScreenFlag == ScreenFlag.HomeScreen) {
                            null
                        } else {
                            {
                                viewModel.setScreenFlag(ScreenFlag.HomeScreen)
                            }
                        }
                    )

                    AnimatedContent(
                        modifier = Modifier.fillMaxSize(),
                        targetState = homeScreenState.currentScreenFlag,
                        transitionSpec = {
                            if(homeScreenState.currentScreenFlag == ScreenFlag.HomeScreen) {
                                // 從 OtherScreen 返回 Home：新畫面從左滑入=向右退出
                                (slideInHorizontally { fullWidth -> -fullWidth } + fadeIn())
                                    .togetherWith(slideOutHorizontally { fullWidth -> fullWidth } + fadeOut())
                            } else {
                                // 從 Home 到 OtherScreen：新畫面從右滑入
                                (slideInHorizontally { fullWidth -> fullWidth } + fadeIn())
                                    .togetherWith(slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut())
                            }
                        }
                    ) { screenFlag ->
                        when(screenFlag) {
                            is ScreenFlag.HomeScreen -> {
                                HomeScreen(
                                    modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                                    homeScreenState = homeScreenState,
                                    onItemClick = { flag: ScreenFlag ->
                                        viewModel.setScreenFlag(flag)
                                    }
                                )
                            }

                            is ScreenFlag.LazyGridExampleScreen -> {
                                CommonLazyVerticalStaggeredGrid(
                                    modifier = Modifier.navigationBarsPadding(),
                                    gridState = gridState,
                                    pullToRefreshState = pullToRefreshState,
                                    isReloading = false,
                                    isLoadingMore = false
                                )
                            }

                            is ScreenFlag.AnimationLazyColumnItemExampleScreen -> {
                                AnimationLazyColumnItemExampleScreen(
                                    modifier = Modifier.navigationBarsPadding()
                                )
                            }

                            is ScreenFlag.ContextualFlowRowExampleScreen -> {
                                ContextualFlowRowExampleScreen(
                                    modifier = Modifier.navigationBarsPadding()
                                )
                            }

                            is ScreenFlag.MultipleAnimationExampleScreen -> {
                                MultipleAnimationExampleScreen(
                                    modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                                )
                            }

                            is ScreenFlag.BottomSheetExampleScreen -> {
                                CustomBottomSheetExampleScreen(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            is ScreenFlag.ModalBottomSheetExampleScreen -> {
                                ModalBottomSheetExampleScreen()
                            }

                            is ScreenFlag.HorizontalPagerExampleScreen -> {
                                HorizontalPagerScaleExampleScreen(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            is ScreenFlag.LineChartExampleScreen -> {
                                LineChartExampleScreen(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            is ScreenFlag.SwipeItemScreen -> {
                                SwipeExampleScreen(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .statusBarsPadding()
//                        .navigationBarsPadding()
//                        .verticalScroll(rememberScrollState())
//                ) {
                    //todo SharedTransitionLayout
//                    Text("待施工 做個入口導到個別範例去")

//                    CameraPreview(
//                        modifier = Modifier.fillMaxSize()
//                    )

//                    ContextualFlowRowExampleScreen(
//                        modifier = Modifier
//                    )
//                    HomeScreen(viewModel)
//                    ChatPage()
//                    AnimationScreen()
//                    TestDrawModifier()
//                    TestDatePicker()
//                    CustomDatePicker()
//                    TestPointer()

//                }
            }
        }

        // override onBackPressed 已被棄用
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 如果正在聊天中 執行compose設計好的動作(淡出/滑出) 不然就是正常系統backPressed
//                if (!viewModel.endChat()) {
//                    onBackPressedDispatcher.onBackPressed()
//                }
//                onBackPressedDispatcher.onBackPressed()
            }
        })
    }

    @Composable
    fun Custom(modifier: Modifier = Modifier) {
        /*
        *  measurable -> 用於測量最終顯示範圍的對象 =我們想修飾的組件但不能直接修改到Text
        *  constraints -> 外部給Text的尺寸限制
        * 適用場景給組件在位置和尺寸方面增加裝飾效果 (不干涉這個組件的繪製規則 只增加外觀)
        * */
        Box(Modifier.background(color = Color.Yellow)) {
            Text(modifier = Modifier.layout { measurable, constraints -> //測量
                val a = 10.dp.roundToPx()
                val placeable = measurable.measure(
                    constraints.copy(
                        maxWidth = constraints.maxWidth - a * 2
                    )
                )
                val size = kotlin.math.max(placeable.width + a * 2, placeable.height)
                // 替我們實現了 placeChildren 介面不發生任何變化
                layout(
                    width = size, // 尺寸
                    height = size,
                ) { // 進入布局才執行的區塊 = 擺放的行為
                    placeable.placeRelative(0 + a, 0) // 位置偏移 會自動RTL布局
                }
            }, text = "AlanTest")
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenState: HomeScreenState,
    onItemClick: (flag: ScreenFlag) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(homeScreenState.homeListData) { index, item ->
            HomeListItem(
                modifier = Modifier.fillMaxWidth().background(
                    color = LocalCustomColors.current.blueGray300
                ).delayClick {
                    onItemClick(item)
                },
                title = "${index+1}." + stringResource(item.titleRes)
            )
        }
    }
}

@Composable
fun HomeListItem(
    modifier: Modifier = Modifier,
    title: String,
) {
    Column(
        modifier
    ) {
        Text(
            modifier = Modifier.padding(vertical = 10.mdp, horizontal = 10.mdp),
            text = title,
            style = TextStyle(
                color = LocalCustomColors.current.darkBlue700,
                fontSize = 20.msp
            )
        )

        HorizontalDivider(
            color = LocalCustomColors.current.blueGray500
        )
    }

}





