package com.example.composeproject.example

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeproject.utils.mdp
import com.example.composeproject.data.ScaleExampleData
import com.example.composeproject.extension.safeGetData
import com.example.composeproject.ui.theme.LocalCustomColors
import com.example.composeproject.viewmodel.HorizontalPagerScaleViewModel
import kotlin.math.absoluteValue

@Composable
fun HorizontalPagerScaleExampleScreen(
    modifier: Modifier = Modifier,
    viewModel: HorizontalPagerScaleViewModel = viewModel()
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { screenState.pageCount }
    )

    val density = LocalDensity.current
    val screenWidthDp = LocalWindowInfo.current.containerSize.width
    // 項目左右2邊的padding 比例 (根據螢幕寬度)
    val paddingPx = screenWidthDp * 0.225f
    val paddingDistance = with(density) { paddingPx.toDp()}
    Log.d("********", "HorizontalPagerScaleExampleScreen: ${paddingDistance} || ${80.mdp}")
    Column(
        modifier = modifier
    ) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalCustomColors.current.blue50),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = paddingDistance) //todo 改成依比例 抓整個螢幕的寬度
        ) { page ->
            val pageData = screenState.itemList.safeGetData(page)

            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue.coerceIn(0f, 1f)

            val scale = lerp(
                start = 0.625f,
                stop = 1f,
                fraction = 1f - pageOffset.absoluteValue
            )

            PageItemWeight(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                data = pageData
            )
        }
    }
}

@Composable
fun PageItemWeight(
    modifier: Modifier = Modifier,
    data: ScaleExampleData?,
) {
    data?.run {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(iconRes),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

            Text(
                modifier = Modifier.padding(top = 15.mdp),
                text = stringResource(data.stringRes)
            )
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun PreviewPageItemWeight() {
    PageItemWeight(
        data = ScaleExampleData.ExampleAddAlert
    )
}