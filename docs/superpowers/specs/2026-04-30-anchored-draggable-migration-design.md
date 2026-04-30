# AnchoredDraggableState 遷移至新 API 設計

**日期：** 2026-04-30  
**檔案：** `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt`

---

## 背景

Compose Foundation 1.7（BOM 2024.09.03）將原本的 `AnchoredDraggableState` 六參數建構子標記為 deprecated，要求將 `positionalThreshold`、`velocityThreshold`、`snapAnimationSpec`、`decayAnimationSpec` 移至 `anchoredDraggableFlingBehavior(...)` 並傳給 `Modifier.anchoredDraggable`。

**設計理念：** 位置狀態（現在停在哪）與物理行為規格（放手後怎麼動）職責分離，讓 `AnchoredDraggableState` 只負責記錄位置，`FlingBehavior` 負責飛行邏輯。

---

## 目標

1. 移除 `AnchoredDraggableState` 建構子中的四個 deprecated 參數。
2. 以 `anchoredDraggableFlingBehavior(...)` 建立 per-item 的飛行行為。
3. `SwipeItem` 新增 `flingBehavior: FlingBehavior?` 參數，傳入 `anchoredDraggable`。
4. 現有功能（滑動、收起、動畫效果）完全不變。

---

## API 變更

### SwipeItem 函式簽名

新增一個參數，其餘不變：

```kotlin
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeItem(
    state: AnchoredDraggableState<DragAnchors>,
    key: Int,
    withItemMode: Boolean = false,
    swipeEnabled: Boolean = true,
    startAnchor: Float = 0f,
    endAnchor: Float = 0f,
    startContent: (@Composable BoxScope.() -> Unit)? = null,
    endContent: (@Composable BoxScope.() -> Unit)? = null,
    flingBehavior: FlingBehavior? = null,        // ← 新增
    contentItemWeight: @Composable BoxScope.() -> Unit,
    onStateChange: (key: Int, itemState: DragAnchors) -> Unit = { _, _ -> }
)
```

### SwipeItem 內部：anchoredDraggable 補上 flingBehavior

```kotlin
.anchoredDraggable(
    state = state,
    orientation = Orientation.Horizontal,
    enabled = swipeEnabled,
    reverseDirection = true,
    interactionSource = null,
    flingBehavior = flingBehavior    // ← 傳入
)
```

---

## SwipeExampleScreen 變更

### 狀態建立（移除 deprecated 參數）

```kotlin
AnchoredDraggableState(
    initialValue = DragAnchors.Center,
    anchors = DraggableAnchors {
        DragAnchors.Start at -startAnchor
        DragAnchors.Center at 0f
        DragAnchors.End at endAnchor
    }
)
```

### Fling behavior 建立（per-item，緊跟在 state 後）

```kotlin
val flingBehavior = anchoredDraggableFlingBehavior(
    state = state,
    positionalThreshold = { totalDistance -> totalDistance * 0.5f },
    velocityThreshold = { velocityThreshold },
    snapAnimationSpec = spring(),
    decayAnimationSpec = decayAnimationSpec
)
```

### SwipeItem 呼叫端補上 flingBehavior

```kotlin
SwipeItem(
    state = state,
    key = index,
    startAnchor = startAnchor,
    endAnchor = endAnchor,
    flingBehavior = flingBehavior,
    startContent = { ... },
    endContent = { ... },
    contentItemWeight = { ... },
    onStateChange = { ... }
)
```

---

## 不受影響的部分

| 項目 | 狀態 |
|---|---|
| `itemStates` map 與收起邏輯 | 不變 |
| `startContent` / `endContent` lambda | 不變 |
| `withItemMode` offset 邏輯 | 不變 |
| `velocityThreshold` 計算（`100.dp.toPx()`） | 不變 |
| `decayAnimationSpec`（`rememberSplineBasedDecay`） | 不變 |
| `LazyColumn` 捲動收起邏輯 | 不變 |

---

## 檔案異動範圍

| 檔案 | 類型 |
|---|---|
| `ui/common/SwipeItem.kt` | 修改：`SwipeItem` 新增 `flingBehavior` 參數，`anchoredDraggable` 補上 flingBehavior |
| `ui/common/SwipeItem.kt`（`SwipeExampleScreen`） | 修改：`AnchoredDraggableState` 移除 deprecated 參數，新增 fling behavior 建立，傳入 `SwipeItem` |
