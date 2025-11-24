package com.example.composeproject.ui.common.inputbox

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.coerceIn
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeproject.R
import com.example.composeproject.ui.theme.LocalCustomColors
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @param title 標題
 * @param isError 是否為錯誤狀態 true會顯示errorMessage
 * @param isRequest 是否為必填欄位 true顯示*號
 * @param errorMessage 錯誤訊息
 * @param placeHolderText hint文字
 * @param inputBoxPadding 文字離框線的padding
 * @param textFieldValue 文字狀態
 * @param textMaxLength 輸入框文字上限 預設無限
 * @param onValueChange 當文字變化時的動作
 * */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TypingInputBox(
    modifier: Modifier = Modifier,
    title: String = "",
    isError: Boolean = false,
    isRequest: Boolean = false,
    errorMessage: String = "",
    placeHolderText: String = "",
    inputBoxPadding: PaddingValues = PaddingValues(top = 18.sdp),
    textFieldValue: TextFieldValue,
    textMaxLength: Int = Int.MAX_VALUE,
    onValueChange: (textFieldValue: TextFieldValue) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    var focused by remember { mutableStateOf(false) }

    Column(
        modifier
    ) {
        if (title.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 16.ssp,
                        color = LocalCustomColors.current.darkBlue800,
                        fontWeight = FontWeight.Normal,
                        lineHeight = (24).ssp,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        ),
                        textAlign = TextAlign.Start
                    )
                )

                if (isRequest) {
                    Text(
                        modifier = Modifier.padding(start = 4.sdp),
                        text = stringResource(R.string.star),
                        style = TextStyle(
                            fontSize = 16.ssp,
                            color = LocalCustomColors.current.darkBlue800,
                            fontWeight = FontWeight.Normal,
                            lineHeight = (24).ssp,
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.None
                            ),
                            textAlign = TextAlign.Start
                        )
                    )
                }
            }
        }

        BasicTextField(
            modifier = Modifier
                .height(120.sdp)
                .padding(inputBoxPadding)
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusEvent { state ->
                    focused = state.isFocused
                    if (state.isFocused) {
                        coroutineScope.launch {
                            delay(200)
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }
                .background(
                    shape = RoundedCornerShape(8.sdp),
                    color = Color.White
                )
                .border(
                    width = 1.dp,
                    color = if (isError) {
                        LocalCustomColors.current.pinkRed700
                    } else if (focused) {
                        LocalCustomColors.current.darkBlue800
                    } else {
                        LocalCustomColors.current.darkBlue30
                    },
                    shape = RoundedCornerShape(8.sdp)
                )
                .padding(12.sdp),
            value = textFieldValue,
            onValueChange = { fieldValue ->
                val newText = fieldValue.text

                if (newText.length > textMaxLength) {
                    // 如果超過只取前textMaxLength 個字元
                    val trimmedText = newText.substring(0, textMaxLength)
                    val newSelection = fieldValue.selection.coerceIn(0, textMaxLength) // 確保游標在有效範圍內

                    onValueChange(fieldValue.copy(text = trimmedText, selection = newSelection))
                } else {
                    onValueChange(fieldValue)
                }
            },
            textStyle = TextStyle(
                fontSize = 16.ssp,
                color = LocalCustomColors.current.darkBlue800,
                fontWeight = FontWeight.Normal,
                lineHeight = (24).ssp,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None
                ),
                textAlign = TextAlign.Start
            ),
            cursorBrush = SolidColor(LocalCustomColors.current.pinkRed700),
            decorationBox = { innerTextField ->
                if (textFieldValue.text.isEmpty()) {
                    Text(
                        modifier = Modifier,
                        text = placeHolderText,
                        style = TextStyle(
                            fontSize = 16.ssp,
                            color = LocalCustomColors.current.darkBlue800,
                            fontWeight = FontWeight.Normal,
                            lineHeight = (24).ssp,
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.None
                            ),
                            textAlign = TextAlign.Start
                        ),
                    )
                }
                innerTextField()
            }
        )

        Row(
            modifier = Modifier.padding(top = 6.sdp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isError) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = errorMessage,
                    style = TextStyle(
                        fontSize = 13.ssp,
                        color = LocalCustomColors.current.pinkRed700,
                        fontWeight = FontWeight.Normal,
                        lineHeight = (19).ssp,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        ),
                        textAlign = TextAlign.Start
                    ),
                    maxLines = 1
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (textMaxLength != Int.MAX_VALUE) {
                Text(
                    modifier = Modifier
                        .bringIntoViewRequester(bringIntoViewRequester),
                    text = "${textFieldValue.text.length}/$textMaxLength",
                    style = TextStyle(
                        fontSize = 13.ssp,
                        color = LocalCustomColors.current.blueGray300,
                        fontWeight = FontWeight.Normal,
                        lineHeight = (19).ssp,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        ),
                        textAlign = TextAlign.Start
                    ),
                    color = if (focused || isError) {
                        LocalCustomColors.current.darkBlue800
                    } else {
                        LocalCustomColors.current.darkBlue30
                    }
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun PreviewTypingInputBox() {
    var text by remember {
        mutableStateOf(TextFieldValue(""))
    }

    TypingInputBox(
        modifier = Modifier.padding(18.sdp),
        title = "AlanTest",
        isError = true,
        errorMessage = "測試錯誤測試錯誤測試錯誤測試錯誤測試錯誤",
        textMaxLength = 300,
        placeHolderText = "我是預覽文字",
        textFieldValue = text,
        onValueChange = { textFieldValue ->
            text = textFieldValue
        }
    )
}