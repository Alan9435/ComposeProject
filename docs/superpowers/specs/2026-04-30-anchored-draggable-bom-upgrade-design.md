# AnchoredDraggableState 完整遷移（升級 BOM + anchoredDraggableFlingBehavior）

**日期：** 2026-04-30  
**檔案：** `gradle/libs.versions.toml`、`app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt`

---

## 背景

Foundation 1.8.x 將 `anchoredDraggableFlingBehavior` 正式公開。升級 BOM 後可完全消除目前 `AnchoredDraggableState` 的 deprecated warning，並回歸 Compose 設計意圖：**state 只記錄位置，fling behavior 獨立管理飛行物理規格**。

---

## 目標

1. 升級 `composeBom` 至包含 Foundation 1.8.x 的版本。
2. `SwipeExampleScreen` 的 `AnchoredDraggableState` 改用新的簡單建構子（`initialValue` + `anchors`）。
3. 移除過渡期的 `.also { updateAnchors(...) }` 寫法。
4. 以 `state.anchoredDraggableFlingBehavior(...)` 建立 per-item 飛行行為並傳入 `SwipeItem`。
5. 所有 deprecated warning 消除，編譯正常。

---

## 變更內容

### `libs.versions.toml`

```toml
composeBom = "2025.05.01"   # 或更新版本，含 Foundation 1.8.x
```

### `SwipeExampleScreen`（`SwipeItem.kt` 內）

**State 建立（移除 `.also { updateAnchors }` 過渡寫法，改用新建構子）：**

```kotlin
val state = itemStates.getOrPut(index) {
    AnchoredDraggableState(
        initialValue = DragAnchors.Center,
        anchors = DraggableAnchors {
            DragAnchors.Start at -startAnchor
            DragAnchors.Center at 0f
            DragAnchors.End at endAnchor
        }
    )
}
```

**Fling behavior（新增，緊接 state 之後）：**

```kotlin
val flingBehavior = state.anchoredDraggableFlingBehavior(
    positionalThreshold = { total -> total * 0.5f },
    velocityThreshold = { velocityThreshold },
    snapAnimationSpec = spring(),
    decayAnimationSpec = decayAnimationSpec
)
```

**SwipeItem 呼叫端補上 flingBehavior：**

```kotlin
SwipeItem(
    state = state,
    key = index,
    startAnchor = startAnchor,
    endAnchor = endAnchor,
    flingBehavior = flingBehavior,
    ...
)
```

---

## 不受影響的部分

| 項目 | 狀態 |
|---|---|
| `SwipeItem` 的 `flingBehavior: FlingBehavior?` 參數 | 不變（已在上一次遷移加入） |
| `decayAnimationSpec`（`rememberSplineBasedDecay`） | 不變 |
| `velocityThreshold` 計算 | 不變 |
| `startContent` / `endContent` lambda | 不變 |
| `itemStates` map 與收起邏輯 | 不變 |

---

## 檔案異動範圍

| 檔案 | 類型 |
|---|---|
| `gradle/libs.versions.toml` | 修改：`composeBom` 版本升級 |
| `ui/common/SwipeItem.kt` | 修改：`AnchoredDraggableState` 建構子 + 新增 fling behavior + SwipeItem 呼叫端 |
