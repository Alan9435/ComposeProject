# SwipeItem Content Slots Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 將 `SwipeItem` 的 start / end 區塊 UI 抽為可自訂的 content lambda，同時保持 `withItemMode` 偏移邏輯封裝於元件內部。

**Architecture:** 在 `SwipeItem` 函式簽名新增 `startContent` 和 `endContent` 兩個可選的 `@Composable BoxScope.() -> Unit` 參數；將原本寫死的 `Column`（start）和 `Row`（end）替換為持有 size + offset 的 `Box` 容器，並將 `BoxScope` 傳給 lambda；最後在 `SwipeExampleScreen` 以 lambda 形式傳入原有 UI 內容。

**Tech Stack:** Kotlin、Jetpack Compose、`AnchoredDraggableState`（`androidx.compose.foundation`）

---

## 檔案異動範圍

| 動作 | 路徑 |
|---|---|
| Modify | `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt` |

---

### Task 1：在 `SwipeItem` 函式簽名新增兩個 content 參數

**Files:**
- Modify: `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt:75-83`

- [ ] **Step 1：修改函式簽名**

將第 75–83 行的函式宣告由：

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
    contentItemWeight: @Composable BoxScope.() -> Unit,
    onStateChange: (key: Int, itemState: DragAnchors) -> Unit = { _, _ -> }
) {
```

改為：

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
    contentItemWeight: @Composable BoxScope.() -> Unit,
    onStateChange: (key: Int, itemState: DragAnchors) -> Unit = { _, _ -> }
) {
```

- [ ] **Step 2：確認編譯無誤（此時呼叫端尚未傳入新參數，使用預設 null，行為暫時與原本一致）**

```bash
cd C:\Users\20519\Desktop\ComposeProject
./gradlew assembleDebug
```

預期：BUILD SUCCESSFUL

- [ ] **Step 3：Commit**

```bash
git add app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt
git commit -m "refactor: add startContent/endContent params to SwipeItem signature"
```

---

### Task 2：將 start 區塊從寫死的 `Column` 改為 `Box` + lambda

**Files:**
- Modify: `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt:102-134`

- [ ] **Step 1：替換 start 區塊**

將目前第 102–134 行的整個 `// startWeight` Column 區塊：

```kotlin
// startWeight
Column(
    modifier = Modifier
        .fillMaxHeight()
        .width(startBlockWidth)
        .offset {
            if (withItemMode) {
                IntOffset(
                    x = (-state
                        .requireOffset() - startAnchor)
                        .roundToInt(),
                    y = 0
                )
            } else {
                IntOffset(0, 0)
            }
        }
        .background(Color.Gray)
        .delayClick {
            Log.d("*******", "click隨便: in")
        },
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Image(
        painter = painterResource(id = R.drawable.ic_baseline_cloud_24),
        contentDescription = ""
    )
    Text(
        modifier = Modifier,
        text = "隨便"
    )
}
```

替換為：

```kotlin
// startContent
if (startContent != null) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(startBlockWidth)
            .offset {
                if (withItemMode) {
                    IntOffset(
                        x = (-state
                            .requireOffset() - startAnchor)
                            .roundToInt(),
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

- [ ] **Step 2：確認編譯無誤**

```bash
./gradlew assembleDebug
```

預期：BUILD SUCCESSFUL（start 區塊目前因 null 而不渲染，end 區塊仍為舊寫死 Row）

- [ ] **Step 3：Commit**

```bash
git add app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt
git commit -m "refactor: replace SwipeItem start block with Box + startContent lambda"
```

---

### Task 3：將 end 區塊從寫死的 `Row` 改為 `Box` + lambda

**Files:**
- Modify: `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt`（原 end Row 區段）

- [ ] **Step 1：替換 end 區塊**

將目前的整個 `// endWeight` Row 區塊：

```kotlin
// endWeight
Row(
    modifier = Modifier
        .fillMaxHeight()
        .width(endBlockWidth)
        .offset {
            if (withItemMode) {
                IntOffset(
                    x = (-state
                        .requireOffset() + endAnchor)
                        .roundToInt(),
                    y = 0
                )
            } else {
                IntOffset(0, 0)
            }
        }
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(Color.Green)
            .delayClick {
                Log.d("*******", "click編輯: in")
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_celebration_24),
            contentDescription = ""
        )
        Text(
            modifier = Modifier,
            text = "編輯"
        )
    }

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(Color.Yellow)
            .delayClick {
                Log.d("*******", "click刪除: in")
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_celebration_24),
            contentDescription = ""
        )
        Text(
            modifier = Modifier,
            text = "刪除"
        )
    }
}
```

替換為：

```kotlin
// endContent
if (endContent != null) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(endBlockWidth)
            .offset {
                if (withItemMode) {
                    IntOffset(
                        x = (-state
                            .requireOffset() + endAnchor)
                            .roundToInt(),
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

- [ ] **Step 2：確認編譯無誤**

```bash
./gradlew assembleDebug
```

預期：BUILD SUCCESSFUL（start / end 區塊目前因 null 而不渲染，畫面只剩主體 item）

- [ ] **Step 3：Commit**

```bash
git add app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt
git commit -m "refactor: replace SwipeItem end block with Box + endContent lambda"
```

---

### Task 4：更新 `SwipeExampleScreen` — 傳入 `startContent` 與 `endContent` lambda

**Files:**
- Modify: `app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt`（`SwipeExampleScreen` 內的 `SwipeItem(...)` 呼叫）

- [ ] **Step 1：在 `SwipeItem(...)` 呼叫中加入 `startContent` 與 `endContent`**

找到 `SwipeExampleScreen` 內的 `SwipeItem(` 呼叫，在 `startAnchor = startAnchor` 與 `contentItemWeight = {` 之間插入兩個 lambda：

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
                .delayClick {
                    Log.d("*******", "click隨便: in")
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_cloud_24),
                contentDescription = ""
            )
            Text(
                modifier = Modifier,
                text = "隨便"
            )
        }
    },
    endContent = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Green)
                    .delayClick {
                        Log.d("*******", "click編輯: in")
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_celebration_24),
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier,
                    text = "編輯"
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Yellow)
                    .delayClick {
                        Log.d("*******", "click刪除: in")
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_celebration_24),
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier,
                    text = "刪除"
                )
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
                    scope.launch {
                        itemState.animateTo(DragAnchors.Center)
                    }
                }
            }
        }
    }
)
```

- [ ] **Step 2：確認編譯並安裝至裝置**

```bash
./gradlew installDebug
```

預期：BUILD SUCCESSFUL，App 安裝完成

- [ ] **Step 3：手動驗證**

開啟 App → 進入 SwipeExampleScreen → 確認：
1. 向右滑動 item → 左側出現灰色「隨便」區塊，點擊有 Log 輸出
2. 向左滑動 item → 右側出現綠色「編輯」+ 黃色「刪除」區塊，各自點擊有 Log 輸出
3. 列表捲動時所有展開的 item 自動收起
4. 同時只允許一個 item 處於展開狀態

- [ ] **Step 4：Commit**

```bash
git add app/src/main/java/com/example/composeproject/ui/common/SwipeItem.kt
git commit -m "feat: pass startContent/endContent lambdas in SwipeExampleScreen"
```
