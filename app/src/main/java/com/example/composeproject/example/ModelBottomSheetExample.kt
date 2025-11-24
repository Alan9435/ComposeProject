package com.example.composeproject.example

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.composeproject.R
import com.example.composeproject.Utils.mdp
import com.example.composeproject.ui.common.inputbox.TypingInputBox
import com.example.composeproject.ui.modifier.delayClick
import com.example.composeproject.ui.theme.LocalCustomColors
import kotlinx.coroutines.launch


/**
 * 原理是繪製sheetContent在content之下 再用動畫效果達到bottomSheet的感覺，
 * 所以當你外層有navigationPadding時 會導致bottomSheet被繪製在navigationBar上,
 * 方便展示先改為個別navigationPadding 理論上拉到最外層即可正常顯示
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheetExampleScreen(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden, // bottomSheet初始狀態
            skipHiddenState = false, // false 才可使用scaffoldState.hide()隱藏
            confirmValueChange = { sheetValue ->
                true // 是否傳回狀態的變更 ex: 設為false你下拉關閉會一直被阻止
            }
        )
    )

    CommonPersistentBottomSheet(
        scaffoldState = scaffoldState,
        shape = RoundedCornerShape(topStart = 15.mdp, topEnd = 15.mdp),
        bottomSheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalCustomColors.current.white)
                    .padding(horizontal = 12.mdp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(50.mdp),
                    painter = painterResource(R.drawable.ic_baseline_android_filled_24),
                    tint = Color.Unspecified,
                    contentDescription = ""
                )

                Text(
                    text = "通常放一些資訊類的東西或是可互動的版面 Ex:Content是Google Map \nsheetContent是店家資訊等..."
                )

                Text(
                    modifier = Modifier.padding(top = 12.mdp),
                    text = "很多描述很多描述很多描述很多描述很多描述很多描述很多描述很多描述很多描述很多描述很多描述很多描述很多描述很多描述"
                )
            }
        },
        baseContent = { innerPadding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .background(LocalCustomColors.current.blue50)
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                ) {
                    Text(text = "Open")
                }

                Button(
                    onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.hide()
                        }
                    }
                ) {
                    Text(text = "close")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonPersistentBottomSheet(
    modifier: Modifier = Modifier,
    scaffoldState: BottomSheetScaffoldState,
    sheetPeekHeightWeight: Float = 0f,
    widthWeight: Float = 1f,
    heightWeight: Float = 0.5f,
    topBar: @Composable (() -> Unit)? = null,
    sheetSwipeEnabled: Boolean = false,
    shape: Shape = RoundedCornerShape(topStart = 12.mdp, topEnd = 12.mdp),
    bottomSheetContent: @Composable () -> Unit = {},
    baseContent: @Composable (innerPadding: PaddingValues) -> Unit = {},
) {
    BoxWithConstraints {
        val sheetExpandWidth = maxWidth * widthWeight
        val sheetExpandHeight = maxHeight * heightWeight
        val peekHeight = maxHeight * sheetPeekHeightWeight

        BottomSheetScaffold(
            modifier = modifier,
            scaffoldState = scaffoldState,
            sheetPeekHeight = peekHeight, // PartiallyExpanded 時的高度
            sheetMaxWidth = sheetExpandWidth,
            sheetShape = shape,
            topBar = topBar, // 程式外層的topBar 看需求+入 會自定義繪製在content上方
            sheetSwipeEnabled = sheetSwipeEnabled, // 是否能滑動關閉
            sheetDragHandle = null, // 最上面預設的橫條拖曳bar,
            sheetContent = {
                Box(Modifier.height(sheetExpandHeight)) {
                    bottomSheetContent()
                }
            }
        ) { innerPadding ->
            baseContent(innerPadding)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetExampleScreen(
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    var showBottomSheet by remember { mutableStateOf(false) }
    var textFieldVale by remember { mutableStateOf(
        TextFieldValue()
    ) }

    Column(modifier) {
        Button(
            onClick = {
                scope.launch {
                    showBottomSheet = true
                }
            }
        ) {
            Text(text = "open")
        }
    }

    CommonModalBottomSheet(
        sheetState = sheetState,
        visible = showBottomSheet,
        widthWeight = 0.9f,
        heightWeight = 0.5f,
        containerColor = Color.Transparent,
        dragCloseEnable = false,
        extendScreenToNavigationBar = true,
        onDismissRequest = {
            showBottomSheet = false
        },
        bottomSheetContent = {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(LocalCustomColors.current.white)
                    .padding(horizontal = 13.mdp, vertical = 13.mdp)
                    .verticalScroll(rememberScrollState())
            ) {
                TypingInputBox(
                    textFieldValue = textFieldVale,
                    onValueChange = { tf ->
                        textFieldVale = tf
                    }
                )

                repeat(30) { index ->
                    Text(
                        modifier = Modifier.delayClick {
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        },
                        text = "測試顯示$index"
                    )
                }

                Spacer(
                    Modifier.navigationBarsPadding()
                )
            }
        }
    )
}

/**
 * @param widthWeight bottomSheet寬度
 * @param heightWeight bottomSheet高度
 * @param visible bottomSheet是否顯示
 * @param dragCloseEnable 是否支援拖曳關閉
 * @param shouldDismissOnClickOutside 點遮罩是否關閉
 * @param shouldDismissOnBackPress 點back鍵是否關閉
 * @param shape bottomSheet雄壯
 * @param onDismissRequest 當拖曳/點遮罩 關閉時
 * @param bottomSheetContent bottomSheet內容composable
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonModalBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    visible: Boolean = false,
    containerColor: Color = LocalCustomColors.current.white,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragCloseEnable: Boolean = true,
    shouldDismissOnClickOutside: Boolean = false,
    shouldDismissOnBackPress: Boolean = false,
    widthWeight: Float = 1f,
    heightWeight: Float = 0.9f,
    extendScreenToNavigationBar: Boolean = false,
    shape: Shape = RoundedCornerShape(topStart = 12.mdp, topEnd = 12.mdp),
    onDismissRequest: () -> Unit = {},
    bottomSheetContent: @Composable BoxScope.() -> Unit = {}
) {

    BoxWithConstraints {
        val sheetExpandWidth = maxWidth * widthWeight
        val sheetExpandHeight = maxHeight * heightWeight

        if (visible) {
            ModalBottomSheet(
                modifier = modifier,
                sheetState = sheetState,
                containerColor = containerColor,
                sheetGesturesEnabled = dragCloseEnable,
                sheetMaxWidth = sheetExpandWidth,
                dragHandle = null,
                shape = shape,
                contentWindowInsets = {
                    if(extendScreenToNavigationBar) {
                        WindowInsets.ime
                    } else {
                        BottomSheetDefaults.windowInsets
                    }
                },
                scrimColor = scrimColor,
                properties = ModalBottomSheetProperties(
                    // 按返回鍵是否能關閉
                    shouldDismissOnBackPress = shouldDismissOnBackPress,
                    /*
                        防止點擊遮罩時關閉
                        Material3 最低版本 androidx.compose.material3:material3-*:1.4.0-alpha18 才支援
                        不升版本 -> 將BottomSheet變成滿版 自己手動做一層遮罩蓋上去 防止點擊到系統遮罩
                     */
                    shouldDismissOnClickOutside = shouldDismissOnClickOutside,
//                    isAppearanceLightNavigationBars = false,
//                    isAppearanceLightStatusBars = true
                ),
                onDismissRequest = {
                    onDismissRequest()
                }
            ) {
                Box(
                    Modifier.height(height = sheetExpandHeight)
                ) {
                    bottomSheetContent()
                }
            }
        }
    }
}