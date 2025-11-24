package com.example.composeproject.data

data class HomeScreenState(
    val currentScreenFlag: ScreenFlag = ScreenFlag.HomeScreen,
    val homeListData: List<ScreenFlag> = listOf()
)
