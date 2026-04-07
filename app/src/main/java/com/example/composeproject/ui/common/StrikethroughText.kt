package com.example.composeproject.ui.common

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.example.composeproject.utils.mdp

// todo 實驗+入動畫參數是不是能產生出慢慢畫刪除線的效果 記得考慮多行時的情境
@Composable
fun StrikethroughText(
    text: String,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Black,
    lineHeight: Dp = 1.mdp,
    style: TextStyle = LocalTextStyle.current
) {
    // 儲存文字佈局結果的狀態
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    // 將 Dp 轉換為繪圖時使用的像素值
    val strokePx = with(LocalDensity.current) { lineHeight.toPx() }

    Text(
        text = text,
        modifier = modifier.drawBehind {
            // 只有在 textLayoutResult 不為 null 時才進行繪製
            textLayoutResult?.let { layoutResult ->
                // 迭代每一行
                for (i in 0 until layoutResult.lineCount) {
                    // 1. 獲取當前行的頂部和底部 Y 座標
                    val lineTop = layoutResult.getLineTop(i)
                    val lineBottom = layoutResult.getLineBottom(i)

                    // 2. 計算刪除線的 Y 座標（通常是該行的中心點）
                    val strikeY = lineTop + (lineBottom - lineTop) / 2

                    // 3. 獲取當前行的起始和結束的 X 座標
                    val lineStart = layoutResult.getLineLeft(i)
                    val lineEnd = layoutResult.getLineRight(i)

                    // 4. 繪製刪除線
                    drawLine(
                        color = lineColor,
                        start = Offset(lineStart, strikeY),
                        end = Offset(lineEnd, strikeY),
                        strokeWidth = strokePx
                    )
                }
            }
        },
        style = style,
        // 關鍵：每次文字重新佈局時，更新 textLayoutResult 狀態
        onTextLayout = { result ->
            textLayoutResult = result
        }
    )
}