package com.example.composeproject.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeproject.R
import com.example.composeproject.data.Chat
import com.example.composeproject.data.Msg
import com.example.composeproject.data.User.Companion.Me
import com.example.composeproject.ui.modifier.corner
import com.example.composeproject.ui.modifier.offsetPercent
import com.example.composeproject.ui.theme.Gray70
import com.example.composeproject.viewmodel.MainActivityViewModel
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


/*
        想像socket回來後 更新viewModel資料
        狀態改變所以->畫面更新
 */
//todo 可優化項目 鍵盤彈出時 不要把整個畫面往上帶
@Composable
fun ChatPage() {
    val viewModel: MainActivityViewModel = viewModel()
    // 如果正在聊天->不偏移, 否則偏移頁面的width (百分比)
    val offsetPercentX =
        animateFloatAsState(
            targetValue = if (viewModel.chatting) 0f else 1f,
            label = "animateFloatAsState"
        )

    // 晃動偏移
    var shakingTime by remember {
        mutableIntStateOf(0)
    }

    val shakingLevel by remember {
        mutableIntStateOf(0)
    }

    val shakingOffset = remember {
        Animatable(0f)
    }
    //計算動畫的協程
    LaunchedEffect(key1 = shakingTime) { //key -> 標記 當這個標記改變時 協程就會重啟一次
        // 避免一進來就觸發
        if (shakingTime != 0) {
            /*
            * targetValue(目標結果) 最終停留的位置 0f = 不變
            * dampingRatio(阻尼比) 越低震幅衰減越慢, 抖動時間越長
            * stiffness(剛度) 彈性, 數值越大彈性越大, 抖動頻率越高
            * initialVelocity(初始速度) 越大越快 負數=左上開始 正數=右下開始
            * */
            // 執行動畫
            shakingOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.3f, stiffness = 600f),
                initialVelocity = -5000f
            ) {
                // 每個動畫幀上調用
            }
        }
    }

    Column(
        modifier = Modifier
            .offsetPercent(offsetPercentX = offsetPercentX.value)
            .background(Gray70)
            .fillMaxSize()
    ) {
        // Toolbar
        TopBar(
            title = viewModel.currentChat?.friend?.name ?: "",
            onLeftClick = {
                viewModel.endChat()
            }
        )

        // 聊天區塊
        viewModel.currentChat?.let { chat ->
            ChatContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = Gray70)
                    .offset {
                        IntOffset(
                            x = shakingOffset.value.roundToInt(),
                            y = shakingOffset.value.roundToInt()
                        )
                    },
                chat = chat,
                shakingTime = shakingTime,
                shakingLevel = shakingLevel,
            )
        }

        // 發送訊息欄
        ChatBottomBar(
            text = viewModel.chatInputText,
            onValueChange = {
                viewModel.chatInputText = it
            },
            onSurpriseClick = {
                viewModel.surprise(viewModel.currentChat)
                shakingTime++
            },
            onSendClick = {
                //todo 模擬發API
            }
        )
    }
}

@Composable
fun ChatContent(
    modifier: Modifier,
    chat: Chat,
    shakingTime: Int,
    shakingLevel: Int
) {
    val shakingAngleBubble = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = shakingTime) {
        if (shakingTime != 0) {
            delay(shakingLevel.toLong() * 30)
            shakingAngleBubble.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.4f, stiffness = 500f),
                initialVelocity = 1200f / (1 + shakingLevel * 0.4f)
            )
        }
    }

    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(chat.msgs) { index, msgData ->
            if (msgData.from.id == Me.id) {
                ChatContentItemByRight(
                    avatar = Me.avatar,
                    msgData = msgData,
                    shakingAngleBubble = shakingAngleBubble
                )
            } else {
                ChatContentItemByLeft(
                    avatar = chat.friend.avatar,
                    msgData = msgData,
                    shakingAngleBubble = shakingAngleBubble
                )
            }
        }
    }
}

//@Preview(
//    showBackground = true
//)
//@Composable
//fun ChatContentPreview() {
//    ChatContent()
//}

@Composable
fun ChatContentItemByLeft(
    @DrawableRes avatar: Int,
    msgData: Msg,
    shakingAngleBubble: Animatable<Float, AnimationVector1D>
) {
    Row(
        modifier = Modifier
            .background(color = Color.Transparent)
            .fillMaxWidth()
            .padding(vertical = 2.sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .graphicsLayer(
                    rotationZ = -shakingAngleBubble.value * 0.6f,
                    transformOrigin = TransformOrigin(pivotFractionX = 0f, pivotFractionY = 0f)
                )
                .padding(8.sdp)
                .corner(2.sdp)
                .background(color = Color.Black)
                .padding(top = 3.sdp, start = 3.sdp, end = 3.sdp, bottom = 3.sdp),
            painter = painterResource(id = avatar),
            contentDescription = ""
        )
        Text(
            text = msgData.text,
            modifier = Modifier
                .graphicsLayer(
                    rotationZ = -shakingAngleBubble.value * 0.6f,
                    transformOrigin = TransformOrigin(pivotFractionX = 0f, pivotFractionY = 0f)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(
                        topStart = 15.sdp,
                        bottomStart = 1.sdp,
                        bottomEnd = 6.sdp,
                        topEnd = 6.sdp
                    )
                )
                .padding(top = 5.sdp, start = 8.sdp, end = 5.sdp, bottom = 5.sdp)

        )
    }
}

@Composable
fun ChatContentItemByRight(
    @DrawableRes avatar: Int,
    msgData: Msg,
    shakingAngleBubble: Animatable<Float, AnimationVector1D>
) {
    Row(
        modifier = Modifier
            .background(color = Color.Transparent)
            .fillMaxWidth()
            .padding(vertical = 2.sdp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = msgData.text,
            modifier = Modifier
                .graphicsLayer(
                    rotationZ = -shakingAngleBubble.value,
                    transformOrigin = TransformOrigin(pivotFractionX = 1f, pivotFractionY = 0f)
                )
                .background(
                    color = Color.Green,
                    shape = RoundedCornerShape(
                        topStart = 6.sdp,
                        bottomStart = 6.sdp,
                        topEnd = 15.sdp,
                        bottomEnd = 1.sdp,
                    )
                )
                .padding(top = 5.sdp, start = 5.sdp, end = 8.sdp, bottom = 5.sdp),
        )

        Image(
            modifier = Modifier
                .graphicsLayer(
                    rotationZ = -shakingAngleBubble.value * 0.6f,
                    transformOrigin = TransformOrigin(pivotFractionX = 1f, pivotFractionY = 0f)
                )
                .padding(8.sdp)
                .corner(2.sdp)
                .background(color = Color.Black)
                .padding(top = 3.sdp, start = 3.sdp, end = 3.sdp, bottom = 3.sdp),
            painter = painterResource(id = avatar),
            contentDescription = ""
        )
    }
}

@Preview
@Composable
fun ChatContentItemPreview() {
//    ChatContentItemByLeft(R.drawable.ic_baseline_airline_seat_flat_angled_filled_24, Msg())
}

@Preview
@Composable
fun ChatContentItemByRightPreview() {
//    ChatContentItemByRight(R.drawable.ic_baseline_airline_seat_flat_angled_filled_24, Msg())
}

@Composable
fun ChatBottomBar(
    text: String = "",
    onSurpriseClick: () -> Unit = {},
    onSendClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(8.sdp)
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = {
                        onSurpriseClick.invoke()
                    }
                ),
            painter = painterResource(id = R.drawable.ic_baseline_celebration_24),
            contentDescription = ""
        )

        TextField(
            modifier = Modifier
                .weight(1f),
            value = text,
            singleLine = true,
            shape = RoundedCornerShape(6.sdp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Gray70,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Gray70,
                cursorColor = Color.Red,
                selectionColors = TextSelectionColors(
                    backgroundColor = Color.Transparent,
                    handleColor = Color.Red
                )
            ),
            placeholder = {
                Text(text = "i am placeholder")
            }, onValueChange = { input ->
                onValueChange.invoke(input)
            })

        Text(
            modifier = Modifier
                .padding(8.sdp)
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = {
                        onSendClick.invoke()
                    }
                ),
            text = "傳送"
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
fun ChatBottomBarPreview() {
    ChatBottomBar()
}
