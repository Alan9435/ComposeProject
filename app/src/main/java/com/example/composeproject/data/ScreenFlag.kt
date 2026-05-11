package com.example.composeproject.data

import androidx.annotation.StringRes
import com.example.composeproject.R

sealed class ScreenFlag(@StringRes val titleRes: Int) {
    data object HomeScreen: ScreenFlag(R.string.home_screen_list_home_title)
    data object LazyGridExampleScreen: ScreenFlag(R.string.home_screen_list_grid_title)
    data object AnimationLazyColumnItemExampleScreen: ScreenFlag(R.string.home_screen_list_animation_lazy_column_title)
    data object ContextualFlowRowExampleScreen: ScreenFlag(R.string.home_screen_list_label_row_title)
    data object MultipleAnimationExampleScreen: ScreenFlag(R.string.home_screen_list_multiple_animation_title)
    data object BottomSheetExampleScreen: ScreenFlag(R.string.home_screen_list_persistent_bottom_sheet_title)
    data object ModalBottomSheetExampleScreen: ScreenFlag(R.string.home_screen_list_modal_bottom_sheet_title)
    data object HorizontalPagerExampleScreen: ScreenFlag(R.string.home_screen_list_horizontal_pager_scale_title)
    data object LineChartExampleScreen: ScreenFlag(R.string.home_screen_list_line_chart)
    data object SwipeItemScreen: ScreenFlag(R.string.home_screen_list_swipe_actions_item)
    data object OverlayBadgeExampleScreen: ScreenFlag(R.string.home_screen_list_overlay_badge)
    data object NestedScrollingExampleScreen: ScreenFlag(R.string.home_screen_list_nested_scrolling)
}