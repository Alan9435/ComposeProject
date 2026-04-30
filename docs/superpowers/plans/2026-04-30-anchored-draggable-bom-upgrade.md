# AnchoredDraggableState 完整遷移 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 升級 Compose BOM 至含 Foundation 1.8.x 的版本，完全消除 `AnchoredDraggableState` deprecated warning，並改用 `anchoredDraggableFlingBehavior` 正確分離 state 與飛行行為。

**Architecture:** 先升級 BOM 確認編譯，再將 `SwipeExampleScreen` 的 deprecated 5-param 建構子替換為新 2-param 建構子（`initialValue` + `anchors`），移除過渡期的 `.also { updateAnchors }` 寫法，以 `state.anchoredDraggableFlingBehavior(...)` 建立 fling behavior 並傳入 `SwipeItem`。

**Tech Stack:** Kotlin、Compose BOM（Foundation 1.8.x）、`AnchoredDraggableState`、`anchoredDraggableFlingBehavior`

---

## 檔案異動範圍

| 動作 | 路徑 |
|---|---|
| Modify | `gradle/libs.versions.toml` |
| Modify | `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt` |

---

### Task 1：找出可用的 Foundation 1.8.x BOM 版本並升級

**Files:**
- Modify: `gradle/libs.versions.toml`

- [ ] **Step 1：嘗試升級至 BOM 2025.05.01**

開啟 `gradle/libs.versions.toml`，將：

```toml
composeBom = "2025.01.01"
```

改為：

```toml
composeBom = "2025.05.01"
```

- [ ] **Step 2：確認 Foundation 版本並編譯**

```bash
cd C:\Users\20519\Desktop\ComposeProject
./gradlew compileDebugKotlin 2>&1 | grep -E "(FAILED|SUCCESSFUL|foundation)"
```

預期：若 BUILD SUCCESSFUL 且無 `anchoredDraggableFlingBehavior` unresolved 錯誤，代表版本正確。

若 FAILED 且顯示「Could not resolve androidx.compose:compose-bom:2025.05.01」，表示該版本不存在，進行 Step 3。

- [ ] **Step 3（若 2025.05.01 不存在）：改試 2025.04.01**

```toml
composeBom = "2025.04.01"
```

再執行：

```bash
./gradlew compileDebugKotlin 2>&1 | grep -E "(FAILED|SUCCESSFUL)"
```

若仍失敗，繼續遞增月份直到找到可用版本（2025.06.01、2025.07.01…）。

- [ ] **Step 4：Commit BOM 升級**

```bash
git add gradle/libs.versions.toml
git commit -m "build: upgrade Compose BOM to <實際版本> for Foundation 1.8.x"
```

---

### Task 2：替換 `AnchoredDraggableState` 建構子並加入 fling behavior

**Files:**
- Modify: `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt:213-231`

- [ ] **Step 1：替換 state 建立方式**

將 `items(50) { index ->` 內的 state 建立區塊（目前第 214–231 行）：

```kotlin
            val state = itemStates.getOrPut(index) {
                AnchoredDraggableState(
                    initialValue = DragAnchors.Center,
                    // 滑動到一半時作為臨界點 決定DragAnchors的狀態
                    positionalThreshold = { totalDistance: Float -> totalDistance * 0.5f },
                    // 滑動的速度為多少可以切換DragAnchors的狀態 而不需要滑動到臨界點
                    velocityThreshold = { velocityThreshold },
                    snapAnimationSpec = spring(),
                    decayAnimationSpec = decayAnimationSpec,
                ).also { newState ->
                    // 1.7.x 新 API：anchors 從建構子移出，改由 updateAnchors 設定
                    newState.updateAnchors(DraggableAnchors {
                        DragAnchors.Start at -startAnchor
                        DragAnchors.Center at 0f
                        DragAnchors.End at endAnchor
                    })
                }
            }
```

替換為：

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
            // 飛行行為：手指放開後的位移衰減與 snap 動畫，與狀態分離以符合新 API 設計
            val flingBehavior = state.anchoredDraggableFlingBehavior(
                positionalThreshold = { totalDistance -> totalDistance * 0.5f },
                velocityThreshold = { velocityThreshold },
                snapAnimationSpec = spring(),
                decayAnimationSpec = decayAnimationSpec
            )
```

- [ ] **Step 2：在 `SwipeItem(...)` 呼叫補上 `flingBehavior`**

找到 `SwipeItem(` 呼叫（`endAnchor = endAnchor,` 後面），插入：

```kotlin
                    flingBehavior = flingBehavior,
```

結果：

```kotlin
                SwipeItem(
                    state = state,
                    key = index,
                    startAnchor = startAnchor,
                    endAnchor = endAnchor,
                    flingBehavior = flingBehavior,
                    startContent = {
```

- [ ] **Step 3：確認 deprecated warning 已消除**

```bash
./gradlew compileDebugKotlin 2>&1 | grep -i "deprecated\|AnchoredDraggableState"
```

預期：**不出現** `AnchoredDraggableState` 相關的 deprecated warning。

- [ ] **Step 4：確認完整編譯成功**

```bash
./gradlew compileDebugKotlin 2>&1 | tail -5
```

預期：`BUILD SUCCESSFUL`

- [ ] **Step 5：Commit**

```bash
git add app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt
git commit -m "refactor: use anchoredDraggableFlingBehavior with Foundation 1.8.x"
```
