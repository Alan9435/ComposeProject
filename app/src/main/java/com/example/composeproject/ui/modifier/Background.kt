package com.example.composeproject.ui.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.composeproject.R
import com.example.composeproject.utils.mdp
import com.example.composeproject.ui.theme.LocalCustomColors

enum class PointerPosition {
    LEFT, RIGHT, TOP, BOTTOM
}

/**
 * @param triangleBottomWidth 底度的寬度
 * @param triangleHeight 長的高度
 * @param cornerRadius 提示框圓角
 * @param distanceFromStart 箭頭距離起始點偏移dp 由上/左開始算起
 * @param pointerPosition 箭頭出現的位置
 * */
/**
 * @param triangleBottomWidth 底度的寬度
 * @param triangleHeight 長的高度
 * */
@Composable
fun PointedBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = LocalCustomColors.current.white,
    triangleBottomWidth: Dp = 14.mdp,
    triangleHeight: Dp = 9.mdp,
    cornerRadius: Dp = 12.mdp,
    radiusTriangle: Boolean = false,
    distanceFromStart: Dp? = null,
    contentPadding: PaddingValues = PaddingValues(),
    pointerPosition: PointerPosition = PointerPosition.LEFT,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .drawBehind {
                val triangleBottomWidthPx = triangleBottomWidth.toPx()
                val triangleHeightPx = triangleHeight.toPx()
                val width = size.width
                val height = size.height
                val distancePadding = 1f // 避免框線出現在箭頭底部
                val customDistanceWidth = distanceFromStart?.toPx() ?: (width / 2f)
                val customDistanceHeight = distanceFromStart?.toPx() ?: (height / 2f)
                val triangleRadius = 2f

                val path = Path().apply {
                    when (pointerPosition) {
                        PointerPosition.LEFT -> {
                            moveTo(
                                distancePadding,
                                customDistanceHeight - triangleBottomWidthPx / 2f
                            )
                            if (radiusTriangle) {
                                arcTo(
                                    rect = Rect(
                                        left = -triangleHeightPx - triangleRadius,
                                        top = customDistanceHeight - triangleRadius,
                                        bottom = customDistanceHeight + triangleRadius,
                                        right = -triangleHeightPx
                                    ),
                                    startAngleDegrees = 270f,
                                    sweepAngleDegrees = -180f,
                                    forceMoveTo = false
                                )
                            } else {
                                lineTo(-triangleHeightPx, customDistanceHeight)
                            }

                            lineTo(
                                distancePadding,
                                customDistanceHeight + triangleBottomWidthPx / 2f
                            )
                            close()
                        }

                        PointerPosition.RIGHT -> {
                            moveTo(
                                width - distancePadding,
                                customDistanceHeight - triangleBottomWidthPx / 2f
                            )
                            if (radiusTriangle) {
                                arcTo(
                                    rect = Rect(
                                        left = width + triangleHeightPx - triangleRadius,
                                        top = customDistanceHeight - triangleRadius,
                                        bottom = customDistanceHeight + triangleRadius,
                                        right = width + triangleHeightPx
                                    ),
                                    startAngleDegrees = 270f,
                                    sweepAngleDegrees = 180f,
                                    forceMoveTo = false
                                )
                            } else {
                                lineTo(width + triangleHeightPx, customDistanceHeight)
                            }
                            lineTo(
                                width - distancePadding,
                                customDistanceHeight + triangleBottomWidthPx / 2f
                            )
                            close()
                        }

                        PointerPosition.TOP -> {
                            moveTo(
                                customDistanceWidth - triangleBottomWidthPx / 2f,
                                distancePadding
                            )
                            if (radiusTriangle) {
                                arcTo(
                                    rect = Rect(
                                        left = customDistanceWidth - triangleRadius,
                                        top = -triangleHeightPx - triangleRadius,
                                        bottom = -triangleHeightPx,
                                        right = customDistanceWidth + triangleRadius
                                    ),
                                    startAngleDegrees = -180f,
                                    sweepAngleDegrees = 180f,
                                    forceMoveTo = false
                                )
                            } else {
                                lineTo(customDistanceWidth, -triangleHeightPx)
                            }
                            lineTo(
                                customDistanceWidth + triangleBottomWidthPx / 2f,
                                distancePadding
                            )
                            close()
                        }

                        // width / 2f - triangleBottomWidthPx / 2f
                        PointerPosition.BOTTOM -> {
                            moveTo(
                                customDistanceWidth - triangleBottomWidthPx / 2f,
                                height - distancePadding
                            )
                            if (radiusTriangle) {
                                arcTo(
                                    rect = Rect(
                                        left = customDistanceWidth - triangleRadius,
                                        top = height + triangleHeightPx - triangleRadius,
                                        bottom = height + triangleHeightPx,
                                        right = customDistanceWidth + triangleRadius
                                    ),
                                    startAngleDegrees = 180f,
                                    sweepAngleDegrees = -180f,
                                    forceMoveTo = false
                                )
                            } else {
                                lineTo(customDistanceWidth, height + triangleHeightPx)
                            }

                            lineTo(
                                customDistanceWidth + triangleBottomWidthPx / 2f,
                                height - distancePadding
                            )
                            close()
                        }
                    }
                }
                drawPath(path, backgroundColor)
            }
            .background(backgroundColor, RoundedCornerShape(cornerRadius))
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Preview()
@Composable
private fun PreviewPointedBackground() {
    Box(modifier = Modifier.padding(16.mdp)) {
        PointedBackground(
            modifier = Modifier,
            backgroundColor = LocalCustomColors.current.darkBlue700,
            distanceFromStart = 19.mdp,
            radiusTriangle = true,
            contentPadding = PaddingValues(14.mdp),
            pointerPosition = PointerPosition.LEFT
        ) {
            Row {
                Text(
                    "內容文字"
                )

                Icon(
                    painter = painterResource(R.drawable.ic_baseline_celebration_24),
                    contentDescription = ""
                )
            }

        }
    }
}