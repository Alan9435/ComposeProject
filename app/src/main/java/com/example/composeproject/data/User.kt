package com.example.composeproject.data

import androidx.annotation.DrawableRes
import com.example.composeproject.R

data class User(
    val id: String = "",
    val name: String = "",
    @DrawableRes val avatar: Int = 0
) {
    companion object {
        val Me: User = User(
            id = "AlanId",
            name = "Alan",
            avatar = R.drawable.ic_baseline_blind_filled_24
        )
    }
}