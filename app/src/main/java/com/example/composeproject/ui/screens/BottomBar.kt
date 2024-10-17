package com.example.composeproject.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.composeproject.R
import com.example.composeproject.ui.modifier.unread
import com.example.composeproject.ui.theme.Blue60
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Preview(
    showBackground = true
)
@Composable
private fun TabItem(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int = R.drawable.ic_baseline_blind_filled_24,
    title: String = "AlanTest",
    tint: Color = Blue60,
) {
    Column(
        modifier = modifier.padding(vertical = 6.sdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .size(19.sdp)
                .unread(show = true, color = Color.Red),
            painter = painterResource(id = iconId),
            tint = tint,
            contentDescription = title
        )
        Text(text = title, fontSize = 11.ssp, color = tint)
    }
}


@Composable
fun BottomBar(selected: Int = 0, onSelectedChange: (Int) -> Unit) {
    Row(
        Modifier.background(Color.White)
    ) {
        TabItem(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    onSelectedChange.invoke(0)
                },
            iconId = if (selected == 0) R.drawable.ic_baseline_blind_filled_24 else R.drawable.ic_baseline_blind_outline_24,
            title = "蝦子",
            tint = if (selected == 0) Blue60 else Color.Black,

            )
        TabItem(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    onSelectedChange.invoke(1)
                },
            iconId = if (selected == 1) R.drawable.ic_baseline_android_filled_24 else R.drawable.ic_baseline_android_outline_24,
            title = "Android",
            tint = if (selected == 1) Blue60 else Color.Black,

            )
        TabItem(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    onSelectedChange.invoke(2)
                },
            iconId = if (selected == 2) R.drawable.ic_baseline_add_alert_filled_24 else R.drawable.ic_baseline_add_alert_outline_24,
            title = "通知",
            tint = if (selected == 2) Blue60 else Color.Black,

            )
        TabItem(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    onSelectedChange.invoke(3)
                },
            iconId = if (selected == 3) R.drawable.ic_baseline_airline_seat_flat_angled_filled_24 else R.drawable.ic_baseline_airline_seat_flat_angled_outline_24,
            title = "我的",
            tint = if (selected == 3) Blue60 else Color.Black,

            )
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun BottomBarPreview() {
    var selectedTab by remember {
        mutableStateOf(0)
    }
    BottomBar(selected = selectedTab, onSelectedChange = {
        selectedTab = it
    })
}