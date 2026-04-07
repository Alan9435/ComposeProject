package com.example.composeproject.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

fun Modifier.inVisible(isInvisible: Boolean) = this.alpha(
    if (isInvisible) 0f else 1f
)