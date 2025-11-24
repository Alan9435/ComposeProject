package com.example.composeproject.example

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.ContextualFlowRowOverflow
import androidx.compose.foundation.layout.ContextualFlowRowOverflowScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CustomScrollableTabRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.layoutId
import com.example.composeproject.R
import com.example.composeproject.ui.modifier.delayClick
import com.example.composeproject.ui.modifier.notRippleClickable
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.launch
import kotlin.math.min


@Composable
fun TabRowExampleScreen(modifier: Modifier = Modifier) {
    //wrapContent測試
    var tabs = listOf(
        "Tab-1",
        "Tab-2 Tab-2",
        "Tab3",
        "Tab-4 Tab-4",
        "Tab-5 Tab-5",
        "Tab-6",
        "Tab-7",
        "Tab-8",
        "Tab-9 Tab-9",
        "Tab-10"
    )

    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState {
        tabs.size
    }

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Column(modifier = modifier) {
        CustomScrollableTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage,
            contentColor = Color.Blue,
            containerColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        .height(2.sdp)
                        .requiredWidth(10.sdp),
                    color = Color.Red
                )
            },
            divider = {},
            edgePadding = 0.dp,
        ) {
            tabs.forEachIndexed { index, tabStr ->
                MyTabWeight(
                    modifier = Modifier
                        .delayClick {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                        .padding(horizontal = 8.sdp, vertical = 5.sdp),
                    tabText = tabStr,
                    selected = pagerState.currentPage == index
                )
            }
        }

        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState
        ) { page ->
            Text(text = "這裡是 page = ${pagerState.currentPage}")
        }
    }
}

@Composable
fun TestContextualFlowRowScreen(modifier: Modifier = Modifier) {
    var tabs = listOf(
        "Tab-1",
        "Tab-2 Tab-2",
        "Tab3",
        "Tab-4 Tab-4",
        "Tab-5 Tab-5",
        "Tab-6",
        "Tab-7",
        "Tab-8",
        "Tab-9 Tab-9",
        "Tab-10"
    )

    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.height(100.sdp),
            text = "上面Pin住一點東西"
        )

        Column(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                modifier = Modifier.height(200.sdp),
                text = "有在列表一點東西"
            )

            for (i in 1..10) {
                TestLabelContainer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(0.sdp, 5000.sdp),
                    dataItem = tabs
                )
            }
        }

        Text(
            modifier = Modifier.height(100.sdp),
            text = "下面pin住一點東西"
        )
    }
}


@Composable
fun MyTabWeight(modifier: Modifier = Modifier, tabText: String, selected: Boolean) {
    Column(
        modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = tabText,
            color = Color.Black,
            fontWeight = if (selected) FontWeight.Bold else null
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TestLabelContainer(
    modifier: Modifier = Modifier,
    dataItem: List<String>,
) {
    var maxLine by remember {
        mutableIntStateOf(2)
    }

    val moreOrCollapseIndicator = @Composable { scope: ContextualFlowRowOverflowScope ->
        val remainingItems = dataItem.size - scope.shownItemCount

        if (remainingItems != 0) {
            Text(
                modifier = Modifier.delayClick {
                    maxLine = Int.MAX_VALUE
                },
                text = "測試更多view"
            )
        }
    }

    Column(
        modifier
    ) {
        LabelContainer(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            defaultSkillLabelLine = 30,
            itemCount = dataItem.size,
            maxLines = maxLine,
            moreOrCollapseIndicator = moreOrCollapseIndicator
        ) { index ->
            Text(
                text = dataItem[index]
            )
        }
    }

}

