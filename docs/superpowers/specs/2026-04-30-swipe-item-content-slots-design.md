# SwipeItem 開放 startContent / endContent 自訂 UI 設計

**日期：** 2026-04-30  
**檔案：** `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt`

---

## 背景

`SwipeItem` 元件的左側（start）與右側（end）區塊目前為寫死的 UI（灰色「隨便」按鈕、綠色「編輯」與黃色「刪除」按鈕）。目標是讓呼叫方（如 `SwipeExampleScreen`）能自訂這兩個區塊的 UI 內容，同時保持 `withItemMode` 的偏移動畫邏輯完全封裝在元件內部。

---

## 目標

1. `startContent` 和 `endContent` 由呼叫方提供，可完全自訂 UI。
2. `.offset { }` 的 `withItemMode` 偏移計算邏輯留在 `SwipeItem` 內部，呼叫方不需知道、也不能改動它。
3. 兩個參數皆為可選（nullable），傳入 `null` 時對應區塊不渲染。
4. 現有的 `contentItemWeight`、`onStateChange`、`DraggableAnchors`、`LaunchedEffect` 邏輯完全不變。

---

## API 變更

### SwipeItem 函式簽名

新增兩個參數，其餘參數不變：

```kotlin
@Composable
fun SwipeItem(
    state: AnchoredDraggableState<DragAnchors>,
    key: Int,
    withItemMode: Boolean = false,
    swipeEnabled: Boolean = true,
    startAnchor: Float = 0f,
    endAnchor: Float = 0f,
    startContent: (@Composable BoxScope.() -> Unit)? = null,   // ← 新增
    endContent: (@Composable BoxScope.() -> Unit)? = null,     // ← 新增
    contentItemWeight: @Composable BoxScope.() -> Unit,
    onStateChange: (key: Int, itemState: DragAnchors) -> Unit = { _, _ -> }
)
```

- 型別為 `(@Composable BoxScope.() -> Unit)?`，讓呼叫方可直接使用 `Alignment`、`align()` 等 `BoxScope` 提供的能力。
- 預設值為 `null`，代表省略時不渲染該區塊，向後相容。

---

## 內部結構調整

原本的 start `Column` 與 end `Row` 改為兩個 `Box`，容器本身持有 **尺寸 + offset**，僅將 `BoxScope` 開放給 content lambda。

### Start 區塊

```kotlin
if (startContent != null) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(startBlockWidth)
            .offset {
                if (withItemMode) {
                    IntOffset(
                        x = (-state.requireOffset() - startAnchor).roundToInt(),
                        y = 0
                    )
                } else {
                    IntOffset(0, 0)
                }
            }
    ) {
        startContent()
    }
}
```

### End 區塊

```kotlin
if (endContent != null) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(endBlockWidth)
            .offset {
                if (withItemMode) {
                    IntOffset(
                        x = (-state.requireOffset() + endAnchor).roundToInt(),
                        y = 0
                    )
                } else {
                    IntOffset(0, 0)
                }
            }
    ) {
        endContent()
    }
}
```

offset 邏輯與原本完全相同，只是從 `Column`/`Row` 搬到 `Box`。

---

## SwipeExampleScreen 呼叫範例（調整後）

```kotlin
SwipeItem(
    state = state,
    key = index,
    startAnchor = startAnchor,
    endAnchor = endAnchor,
    startContent = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
                .delayClick { Log.d("*******", "click隨便: in") },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_cloud_24),
                contentDescription = ""
            )
            Text(text = "隨便")
        }
    },
    endContent = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Green)
                    .delayClick { Log.d("*******", "click編輯: in") },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_celebration_24),
                    contentDescription = ""
                )
                Text(text = "編輯")
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Yellow)
                    .delayClick { Log.d("*******", "click刪除: in") },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_celebration_24),
                    contentDescription = ""
                )
                Text(text = "刪除")
            }
        }
    },
    contentItemWeight = {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.sdp)
                .background(LocalCustomColors.current.pinkRed100)
                .align(Alignment.Center),
            text = "這好像是IOS的元件",
            textAlign = TextAlign.Center
        )
    },
    onStateChange = { key, mState ->
        if (mState != DragAnchors.Center) {
            itemStates.forEach { (itemKey, itemState) ->
                if (key != itemKey) {
                    scope.launch { itemState.animateTo(DragAnchors.Center) }
                }
            }
        }
    }
)
```

---

## 不受影響的部分

| 項目 | 狀態 |
|---|---|
| `contentItemWeight` 主體 item 的 offset 邏輯 | 不變 |
| `onStateChange` / `LaunchedEffect` 邏輯 | 不變 |
| `DraggableAnchors` anchor 計算 | 不變（另一個 px/dp 修正議題） |
| `withItemMode` 的偏移行為語意 | 封裝在 Box 內，行為與原來一致 |
| `LazyColumn` 捲動時收起的邏輯 | 不變 |

---

## 檔案異動範圍

| 檔案 | 類型 |
|---|---|
| `ui/common/SwipeItem.kt` | 修改：新增 `startContent`、`endContent` 參數，重構 start/end 區塊為 `Box` |
| `ui/common/SwipeItem.kt`（`SwipeExampleScreen`） | 修改：呼叫端傳入 `startContent` 與 `endContent` lambda |
