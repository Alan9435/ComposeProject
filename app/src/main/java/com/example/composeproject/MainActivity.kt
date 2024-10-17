package com.example.composeproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import com.example.composeproject.ui.screens.ChatPage
import com.example.composeproject.ui.screens.HomeScreen
import com.example.composeproject.ui.theme.ComposeProjectTheme
import com.example.composeproject.viewmodel.MainActivityViewModel

/* Compose中越常重刷新的元件
* 建議寫在越下面 效能會比較好
* Compose 多重嵌套不影響性能
*
* 抽離Composeable時 盡量別超過1個layout
* 否則外部呼叫時 很難控制內部組件
* */
class MainActivity : ComponentActivity() {
    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeProjectTheme(
                darkTheme = viewModel.isDarkTheme, //viewModel.isDarkTheme
                dynamicColor = false
            ) {
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    HomeScreen(viewModel)
                    ChatPage()
                }
            }
        }

        // override onBackPressed 已被棄用
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 如果正在聊天中 執行compose設計好的動作(淡出/滑出) 不然就是正常系統backPressed
                if (!viewModel.endChat()) {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }
}
