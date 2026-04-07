package com.example.composeproject.ui.textStyle

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import com.example.composeproject.utils.msp
import com.example.composeproject.ui.theme.LocalCustomColors

val ChartTextStyle: TextStyle
    @Composable
    get() = TextStyle(
        fontSize = 10.msp,
        color = LocalCustomColors.current.darkBlue500,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        lineHeight = (1.5).em,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None
        ),
    )

val ChartToolTipStyle: TextStyle
    @Composable
    get() = TextStyle(
        fontSize = 14.msp,
        color = LocalCustomColors.current.white,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Start,
        lineHeight = (1.5).em,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None
        ),
    )