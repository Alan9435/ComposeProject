package com.example.composeproject.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeproject.R
import com.example.composeproject.ui.modifier.scaleOnPress
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun TopBar(
    title: String = "標題",
    backgroundColor: Color = Color.White,
    @DrawableRes leftIcon: Int = R.drawable.ic_baseline_arrow_back_ios_new_24,
    onLeftClick: (() -> Unit)? = {},
    @DrawableRes rightIcon: Int = R.drawable.ic_baseline_cloud_24,
    onRightClick: (() -> Unit)? = {},
) {
    val buttonInteractionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .background(color = backgroundColor)
            .height(45.sdp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier.size(36.sdp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (onLeftClick != null) {
                Image(
                    modifier = Modifier
                        .clickable(
                            //如果這邊改buttonInteractionSource 可以達到按左邊 右邊一起縮放的效果
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null,
                            onClick = {
                                onLeftClick.invoke()
                            }
                        )
                        .size(24.sdp),
                    painter = painterResource(id = leftIcon),
                    contentDescription = "back"
                )
            }
        }

        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            text = title,
            fontSize = 16.ssp,
        )

        Box(modifier = Modifier.size(36.sdp), contentAlignment = Alignment.Center) {
            if (onRightClick != null) {
                Image(
                    modifier = Modifier
                        .clickable(
                            interactionSource = buttonInteractionSource,
                            indication = null,
                            onClick = {
                                onRightClick.invoke()
                            }
                        )
                        .size(24.sdp)
                        .scaleOnPress(
                            buttonInteractionSource
                        ),
                    painter = painterResource(id = rightIcon),
                    contentDescription = "",
                )
            }
        }
    }
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 0.5.dp,
        color = Color.Gray
    )
}

@Preview(
    showBackground = true
)
@Composable
fun TopBarPreview() {
    TopBar()
}