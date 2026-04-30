# AnchoredDraggableState Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 將 `AnchoredDraggableState` 的 deprecated 六參數建構子遷移至新 API，把 threshold 和 animation spec 移入 `anchoredDraggableFlingBehavior` 並透過 `Modifier.anchoredDraggable` 的 `flingBehavior` 參數傳入。

**Architecture:** `SwipeItem` 新增 `flingBehavior: FlingBehavior?` 參數並傳給 `anchoredDraggable`；`SwipeExampleScreen` 改用只含 `initialValue` 和 `anchors` 的新建構子，再以 `state.anchoredDraggableFlingBehavior(...)` 建立 per-item 的飛行行為後傳入 `SwipeItem`。

**Tech Stack:** Kotlin、Jetpack Compose Foundation 1.7.x（BOM 2024.09.03）、`AnchoredDraggableState`、`anchoredDraggableFlingBehavior`

---

## 檔案異動範圍

| 動作 | 路徑 |
|---|---|
| Modify | `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt` |

---

### Task 1：`SwipeItem` 新增 `flingBehavior` 參數並傳入 `anchoredDraggable`

**Files:**
- Modify: `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt:78-88`（函式簽名）
- Modify: `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt:167-173`（anchoredDraggable 呼叫）

- [ ] **Step 1：在 KDoc 補上 `flingBehavior` 的說明**

找到第 62–75 行的 KDoc 區塊，在 `@param contentItemWeight` 前插入：

```kotlin
 * @param flingBehavior 手指放開後的飛行行為（位移衰減、snap 動畫曲線），由呼叫方以 anchoredDraggableFlingBehavior 建立後傳入；傳 null 則使用 Compose 預設行為
```

完整 KDoc 結果：

```kotlin
/**
 * https://canopas.com/how-to-implement-swipe-to-action-using-anchoreddraggable-in-jetpack-compose-cccb22e44dff
 * 可左右滑動的itemWeight
 * @param state 滑動的狀態, 如果不用 可直接在SwipeItem內
 * @param key 用來記錄item的唯一值 便於onStateChange時判斷更新 (相當於給這個weight命名)
 * @param withItemMode 後層按鈕是否隨著item滑動而跟著位移出現
 * @param swipeEnabled 是否允許左右滑動
 * @param startAnchor 左邊區塊的可滑動距離（單位：px），同時決定該區塊的寬度
 * @param endAnchor 右邊區塊的可滑動距離（單位：px），同時決定該區塊的寬度
 * @param startContent 左側滑出區塊的自訂 UI，寬度由 startAnchor 決定，offset 動畫由元件內部處理；傳 null 則不顯示左側區塊
 * @param endContent 右側滑出區塊的自訂 UI，寬度由 endAnchor 決定，offset 動畫由元件內部處理；傳 null 則不顯示右側區塊
 * @param flingBehavior 手指放開後的飛行行為（位移衰減、snap 動畫曲線），由呼叫方以 anchoredDraggableFlingBehavior 建立後傳入；傳 null 則使用 Compose 預設行為
 * @param contentItemWeight 列表組件
 * @param onStateChange 當item被滑動時的callback 回傳key(weight唯一值), DragAnchors狀態
 * */
```

- [ ] **Step 2：在函式簽名新增 `flingBehavior` 參數**

將第 78–88 行函式簽名：

```kotlin
fun SwipeItem(
    state: AnchoredDraggableState<DragAnchors>,
    key: Int,
    withItemMode: Boolean = false,
    swipeEnabled: Boolean = true,
    startAnchor: Float = 0f,
    endAnchor: Float = 0f,
    startContent: (@Composable BoxScope.() -> Unit)? = null,
    endContent: (@Composable BoxScope.() -> Unit)? = null,
    contentItemWeight: @Composable BoxScope.() -> Unit,
    onStateChange: (key: Int, itemState: DragAnchors) -> Unit = { _, _ -> }
)
```

改為：

```kotlin
fun SwipeItem(
    state: AnchoredDraggableState<DragAnchors>,
    key: Int,
    withItemMode: Boolean = false,
    swipeEnabled: Boolean = true,
    startAnchor: Float = 0f,
    endAnchor: Float = 0f,
    startContent: (@Composable BoxScope.() -> Unit)? = null,
    endContent: (@Composable BoxScope.() -> Unit)? = null,
    flingBehavior: FlingBehavior? = null,
    contentItemWeight: @Composable BoxScope.() -> Unit,
    onStateChange: (key: Int, itemState: DragAnchors) -> Unit = { _, _ -> }
)
```

- [ ] **Step 3：在 `anchoredDraggable` 呼叫補上 `flingBehavior`**

將第 167–173 行：

```kotlin
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal, // 拖曳內容的方向
                    enabled = swipeEnabled, // 啟用/停用手勢
                    reverseDirection = true, // 反轉拖曳方向
                    interactionSource = null // 互動
                ),
```

改為：

```kotlin
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal, // 拖曳內容的方向
                    enabled = swipeEnabled, // 啟用/停用手勢
                    reverseDirection = true, // 反轉拖曳方向
                    interactionSource = null, // 互動
                    flingBehavior = flingBehavior // 飛行行為（位移衰減 + snap 動畫）
                ),
```

- [ ] **Step 4：新增 `FlingBehavior` import（若 IDE 未自動補入）**

在 import 區塊加入：

```kotlin
import androidx.compose.foundation.gestures.FlingBehavior
```

- [ ] **Step 5：確認編譯無誤**

```bash
cd C:\Users\20519\Desktop\ComposeProject
./gradlew assembleDebug
```

預期：BUILD SUCCESSFUL

- [ ] **Step 6：Commit**

```bash
git add app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt
git commit -m "refactor: add flingBehavior param to SwipeItem for new anchoredDraggable API"
```

---

### Task 2：`SwipeExampleScreen` — 更新 `AnchoredDraggableState` 建構子並建立 fling behavior

**Files:**
- Modify: `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt:209-224`（state 建立）
- Modify: `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt:226-320`（SwipeItem 呼叫）

- [ ] **Step 1：移除 `AnchoredDraggableState` 的 deprecated 參數**

將 `items(50)` 區塊內的 state 建立（第 209–224 行）：

```kotlin
            val state = itemStates.getOrPut(index) {
                AnchoredDraggableState(
                    initialValue = DragAnchors.Center,
                    anchors = DraggableAnchors {
                        DragAnchors.Start at -startAnchor
                        DragAnchors.Center at 0f
                        DragAnchors.End at endAnchor
                    },
                    // 滑動到一半時作為臨界點 決定DragAnchors的狀態
                    positionalThreshold = { totalDistance: Float -> totalDistance * 0.5f },
                    // 滑動的速度為多少可以切換DragAnchors的狀態 而不需要滑動到臨界點
                    velocityThreshold = { velocityThreshold },
                    snapAnimationSpec = spring(),
                    decayAnimationSpec = decayAnimationSpec,
                )
            }
```

改為：

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

在 `SwipeItem(` 的 `endAnchor = endAnchor,` 之後、`startContent = {` 之前插入：

```kotlin
                    flingBehavior = flingBehavior,
```

完整 `SwipeItem` 呼叫頭幾行：

```kotlin
                SwipeItem(
                    state = state,
                    key = index,
                    startAnchor = startAnchor,
                    endAnchor = endAnchor,
                    flingBehavior = flingBehavior,
                    startContent = {
                        // ... 不變
```

- [ ] **Step 3：確認 `@OptIn` 標注**

若編譯時出現 `anchoredDraggableFlingBehavior` 的 `@ExperimentalFoundationApi` 警告，在 `SwipeExampleScreen` 上加入：

```kotlin
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeExampleScreen(modifier: Modifier = Modifier) {
```

- [ ] **Step 4：確認編譯並安裝至裝置**

```bash
./gradlew installDebug
```

預期：BUILD SUCCESSFUL，App 安裝完成

- [ ] **Step 5：手動驗證**

開啟 App → 進入 SwipeExampleScreen → 確認：
1. 向右滑動 item → 左側灰色「隨便」區塊正常彈出，滑到一半鬆手可 snap
2. 向左滑動 item → 右側「編輯」+「刪除」區塊正常彈出，滑到一半鬆手可 snap
3. 快速滑動（大於 `velocityThreshold`）可直接切換狀態而不需拖到一半
4. 列表捲動時所有展開 item 自動收起
5. IDE 中 `AnchoredDraggableState(...)` 建構子呼叫不再顯示 deprecated 警告

- [ ] **Step 6：Commit**

```bash
git add app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt
git commit -m "refactor: migrate AnchoredDraggableState to new flingBehavior API"
```
