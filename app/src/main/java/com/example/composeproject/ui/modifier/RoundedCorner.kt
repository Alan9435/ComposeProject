package com.example.composeproject.ui.modifier

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp

fun Modifier.corner(radius: Dp) =
    this.clip(
        RoundedCornerShape(radius)
    )
