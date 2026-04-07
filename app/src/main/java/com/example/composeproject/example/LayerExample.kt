package com.example.composeproject.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.example.composeproject.ui.theme.LocalCustomColors

@Composable
fun LayerScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) { }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(
    showBackground = true
)
@Composable
fun CustomText(modifier: Modifier = Modifier) {
    val colors = LocalCustomColors.current
//    Text(
//        modifier = Modifier,
//        text = "1",
//        style = androidx.compose.ui.text.TextStyle(
//            fontSize = 50.msp,
//            drawStyle = Stroke(
//                miter = 1000f,
//                width = 7f,
//                cap = StrokeCap.Round,
//                join = StrokeJoin.Round
//            )
//        )
//    )

    OutlinedText(
        text = "1",
        fillColor = LocalCustomColors.current.white,
        outlineColor = LocalCustomColors.current.pinkRed700,
        outlineDrawStyle = Stroke(
            width = 7f,
            join = StrokeJoin.Round
        )
    )
}

//@Preview
//@Composable
//fun DrawImage() {
//
//    val image = ImageBitmap.imageResource(R.drawable.img_test)
//    Canvas(
//        Modifier.size(100.mdp)
//    ) {
//        drawImage(
//            image = image,
//            dstSize = IntSize(size.height.roundToInt(), size.width.roundToInt() )
//        )
//    }
////    Canvas(
////        Modifier.size(180.mdp)
////    ) {
////        drawImage(image)
////    }
//}

//@Preview
//@Composable
//private fun Test() {
//    val backgroundRes = SkillMateBadgeBackgroundStyle.Level3.bgStyleRes
//    val foregroundRes = SKillMateBadgeIcon.Loyalty.iconRes
//    val imgBg = ImageBitmap.imageResource(backgroundRes)
//    val imgFg = ImageBitmap.imageResource(foregroundRes)
//    val camera by remember { mutableStateOf(Camera()) }.apply {
//        value.rotateX(45f)
//    }
//
//    val paint by remember {
//        mutableStateOf(Paint())
//    }
//
//    Box(Modifier.padding(50.skillDp)) {
//        Canvas(
//            modifier = Modifier.size(150.skillDp)
//        ) {
//            drawIntoCanvas {
//                it.translate(size.width / 2, size.height / 2)
//                it.rotate(-45f) // 沿著平面在做旋轉 不然轉Y軸時 是已經被X軸轉過的數值
//                camera.applyToCanvas(it.nativeCanvas)
//                it.rotate(45f)
//                it.translate(- size.width / 2, -size.height / 2)
//                it.drawImageRect(
//                    image = imgBg,
//                    dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
//                    paint = paint)
//            }
//        }
//    }
//}

@Composable
fun OutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    fillColor: Color = Color.Unspecified,
    outlineColor: Color,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    outlineDrawStyle: Stroke = Stroke(),
) {
    Box(modifier = modifier) {
        Text(
            text = text,
            modifier = Modifier.semantics { hideFromAccessibility() },
            color = outlineColor,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = null,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style.copy(
                shadow = null,
                drawStyle = outlineDrawStyle,
            ),
        )

        Text(
            text = text,
            color = fillColor,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style,
        )
    }
}