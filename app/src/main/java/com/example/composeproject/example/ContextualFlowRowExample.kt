package com.example.composeproject.example

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.ContextualFlowRowOverflow
import androidx.compose.foundation.layout.ContextualFlowRowOverflowScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composeproject.utils.mdp
import com.example.composeproject.ui.modifier.delayClick
import com.example.composeproject.ui.theme.LocalCustomColors

/**
 * 標籤流顯示
 * 如果外層為可滾動元件則會因為無法測量導致閃退 內容如下
 *   java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true)
 *   or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.
 *   可用heightIn(0.mdp, 5000.mdp)解決 -> 限定高度
 * */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContextualFlowRowExampleScreen(modifier: Modifier = Modifier) {
    val totalCount = 400
    var maxLines by remember {
        mutableIntStateOf(2)
    }

    val moreOrCollapseIndicator = @Composable { scope: ContextualFlowRowOverflowScope ->
        val remainingItems = totalCount - scope.shownItemCount

        Text(
            modifier = Modifier
                .border(
                    shape = RoundedCornerShape(4.mdp),
                    color = LocalCustomColors.current.blue200,
                    width = 1.mdp
                )
                .padding(vertical = 3.mdp, horizontal = 6.mdp)
                .delayClick {
                    if (remainingItems == 0) {
                        maxLines = 2
                    } else {
                        maxLines += 5
                    }
                },
            text = if (remainingItems == 0) "Less" else "看更多",
            color = LocalCustomColors.current.pinkRed400
        )
    }

    LabelContainer(
        modifier = modifier
            .fillMaxWidth(1f)
            .padding(horizontal = 13.mdp)
            .wrapContentHeight(align = Alignment.Top)
            .verticalScroll(rememberScrollState())
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        maxLines = maxLines,
        defaultSkillLabelLine = 4,
        moreOrCollapseIndicator = moreOrCollapseIndicator,
        itemCount = totalCount
    ) { index ->
        Text(
            modifier = Modifier
                .border(
                    shape = RoundedCornerShape(4.mdp),
                    color = LocalCustomColors.current.blue200,
                    width = 1.mdp
                )
                .padding(vertical = 3.mdp, horizontal = 6.mdp),
            text = "Item $index"
        )
    }
}

/**
 * 自訂義標籤流組件
 * */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LabelContainer(
    modifier: Modifier = Modifier,
    maxLines: Int,
    defaultSkillLabelLine: Int,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    itemCount: Int,
    moreOrCollapseIndicator: @Composable (ContextualFlowRowOverflowScope) -> Unit,
    labelWeight: @Composable (index: Int) -> Unit
) {
    ContextualFlowRow(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        maxLines = maxLines,
        overflow = ContextualFlowRowOverflow.expandOrCollapseIndicator(
            minRowsToShowCollapse = defaultSkillLabelLine,
            expandIndicator = moreOrCollapseIndicator,
            collapseIndicator = moreOrCollapseIndicator
        ),
        itemCount = itemCount
    ) { index ->
        labelWeight(index)
    }
}