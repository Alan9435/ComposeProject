package com.example.composeproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.composeproject.data.HorizontalPagerScaleScreenState
import com.example.composeproject.data.ScaleExampleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HorizontalPagerScaleViewModel: ViewModel() {
    private val _screenState = MutableStateFlow(HorizontalPagerScaleScreenState())
    val screenState = _screenState.asStateFlow()

    init {
        val data = ScaleExampleData.getDataList()
        _screenState.update {
            it.copy(
                itemList = data,
                pageCount = data.size
            )
        }
    }
}