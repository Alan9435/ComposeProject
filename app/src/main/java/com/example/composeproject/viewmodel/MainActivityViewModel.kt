package com.example.composeproject.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.composeproject.R
import com.example.composeproject.data.Chat
import com.example.composeproject.data.Msg
import com.example.composeproject.data.User

class MainActivityViewModel : ViewModel() {
    // 選了哪個tab item
    var selectedTab by mutableIntStateOf(0)

    // 主題變量
    var isDarkTheme by mutableStateOf(false)

    //當前聊天
    var currentChat: Chat? by mutableStateOf(null)

    // 用戶輸入的文字
    var chatInputText by mutableStateOf("")

    // 正在聊天 為了滑入動畫而增加的狀態 如果用currentChat當基準判斷 相當於一個白畫面滑出(不合理)
    var chatting by mutableStateOf(false)

    val friendOne = User(
        id = "9527",
        name = "低等下人",
        avatar = R.drawable.ic_baseline_android_filled_24
    )
    val friendTwo = User(
        id = "1234",
        name = "隔壁老王",
        avatar = R.drawable.ic_baseline_add_alert_filled_24
    )

    var lastOne = with(Msg(from = User.Me, text = "吃虧在眼前", time = "21:55")) {
        read = true
        this
    }

    val chats by mutableStateOf(
        listOf(
            Chat(
                friend = friendOne,
                msgs = mutableStateListOf(
                    Msg(from = friendOne, text = "有朋自遠方來", time = "21:50"),
                    Msg(from = User.Me, text = "雖遠必誅", time = "21:51"),
                    Msg(from = friendOne, text = "學海無涯", time = "21:52"),
                    Msg(from = User.Me, text = "回頭是岸", time = "21:53"),
                    Msg(from = friendOne, text = "做人留一線", time = "21:54"),
                    lastOne,
                )
            ),
            Chat(
                friend = friendTwo,
                msgs = mutableStateListOf(
                    Msg(from = friendTwo, text = "1234", time = "20:50"),
                    Msg(from = User.Me, text = "5678", time = "21:50"),
                    Msg(from = friendTwo, text = "abcd", time = "22:50"),
                    Msg(from = User.Me, text = "efgh", time = "23:50"),
                    Msg(from = friendTwo, text = "1357", time = "00:50"),
                    Msg(from = User.Me, text = "2468", time = "01:50"),
                )
            ),
        )
    )

    fun startChat(chat: Chat) {
        chatting = true
        currentChat = chat
    }

    fun endChat(): Boolean {
        if (chatting) {
            chatting = false
            return true
        } else {
            return false
        }
    }

    fun surprise(chat: Chat?) {
        chat?.msgs?.add(Msg(User.Me, "\u0830\uDCA3", "15:10"))
    }
}