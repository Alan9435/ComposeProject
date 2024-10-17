package com.example.composeproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeproject.data.Chat
import com.example.composeproject.ui.modifier.unread
import com.example.composeproject.viewmodel.MainActivityViewModel
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun ChatList(chatList: List<Chat>) {
    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(chatList) { index, chat ->
            ChatListItem(chat)
            if (index < chatList.lastIndex) {
                HorizontalDivider(
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun ChatListItem(chat: Chat) {
    val viewModel: MainActivityViewModel = viewModel()
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(9.sdp)
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = {
                    viewModel.startChat(chat = chat)
                }
            )
    ) {
        Image(
            modifier = Modifier
                .size(48.sdp)
                .unread(show = !chat.msgs.last().read, color = Color.Red)
                .clip(RoundedCornerShape(6.sdp))
                .background(Color.Black),
            painter = painterResource(id = chat.friend.avatar),
            contentDescription = chat.friend.name
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(4.sdp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                fontSize = 13.ssp,
                text = chat.friend.name
            )
            Text(
                modifier = Modifier.fillMaxHeight(),
                fontSize = 11.ssp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = chat.msgs.last().text
            )
        }

        Text(
            modifier = Modifier.padding(top = 4.sdp),
            text = chat.msgs.last().time,
            color = Color.Gray,
            fontSize = 11.ssp
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
fun ChatListPreview() {
    val viewModel: MainActivityViewModel = viewModel()
    ChatList(viewModel.chats)
}
