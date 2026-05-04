---
name: figma-to-android
description: "分析 Figma 設計稿並輸出 Android 開發需求 JSON。觸發時機：(1) 用戶提供 Figma URL 要求分析設計稿，(2) 用戶說「幫我看這個稿」「解析 Figma」「Before/After 比較」「設計稿改了什麼」，(3) 需要將 Figma 設計轉換為 Android 實作規格。支援 Before/After 視覺比對 + annotations 文字解析雙管齊下。關鍵字：Figma、設計稿、Before/After、annotations、需求分析、Android、UI 規格。"
---
 
# Figma 設計稿分析 Skill
 
分析 Figma 設計稿，結合圖片視覺比對與文字 annotations 解析，輸出精確需求 JSON。
 
## 工作流程
 
```
步驟 0：詢問是否有 Before URL
    ↓
有 Before → 步驟 1A：Before + After 雙截圖視覺比對
無 Before → 步驟 1B：僅 After 截圖
    ↓
步驟 2：get_design_context 文字解析（After）
    ↓
步驟 3：交叉驗證 → Markdown 報告
步驟 4：儲存 JSON
```
 
---
 
## ⛔ 絕對不要做
 
- **NEVER** 忽略回傳末尾的 `These styles are contained in the design:` 區塊 → 缺少此區塊會導致所有 Token 名稱憑空捏造，輸出的 JSON styleSpec 全部錯誤
- **NEVER** 直接使用 URL 中的 `node-id=1-2` 格式呼叫 API → 必須轉為 `1:2`（`-` → `:`），否則 API 回傳 404 且錯誤訊息不明確，難以察覺
- **NEVER** 把 Before 的紅色/粉紅色色塊當作 UI 差異 → 這些是設計師的標示工具，After 不會有，直接忽略
- **NEVER** 在 node 太大時繼續 retry `get_design_context` → 每次 retry 耗費大量 token 且不會成功，立即停止並告知用戶
- **NEVER** 在視覺與 annotation 衝突時取平均值或折衷 → 必須以 annotation 為準，並在 JSON 的 `notes` 欄位明確記錄衝突內容
- **NEVER** 在 `styleSpec` 中使用 rgba 格式 → 一律轉為 8 碼 hex（`rgba(0,0,0,0.7)` → `#000000B3`）
- **NEVER** 省略 `confidence` 欄位 → 這是讓開發者知道哪些需求需要再確認的關鍵資訊
 
---
 
## 步驟 0：確認輸入
 
主動詢問：
 
```
我看到你提供了 After 的 Figma 網址。
請問是否有 Before 的 Figma 網址？有的話視覺差異會更準確。
```
 
- 有 Before URL → 步驟 1A
- 沒有 → 步驟 1B
 
**URL 解析**：`node-id=1-2` → nodeId `1:2`（`-` 轉 `:`）
 
---
 
## 步驟 1A：Before + After 視覺比對
 
平行呼叫兩個截圖：
 
```
get_screenshot(fileKey: beforeFileKey, nodeId: beforeNodeId)
get_screenshot(fileKey: afterFileKey, nodeId: afterNodeId)
```
 
逐一比對：新增元素、移除元素、文字變更、樣式變更、佈局變更、狀態變更、圖示變更。
 
**Before 紅色/粉紅色背景色塊 = 設計師標示，直接忽略，不列為差異。**
 
輸出內部記錄（供步驟 3 交叉驗證用）：
```
視覺差異觀察：
- [新增] xxx
- [移除] xxx
- [修改-樣式] xxx
```
 
---
 
## 步驟 1B：僅 After 截圖
 
```
get_screenshot(fileKey: afterFileKey, nodeId: afterNodeId)
```
 
---
 
## 步驟 2：After 文字解析
 
```
get_design_context(
  fileKey: afterFileKey,
  nodeId: afterNodeId,
  clientLanguages: "kotlin",
  clientFrameworks: "android",
  excludeScreenshot: true
)
```
 
**重點提取順序**：
 
1. **`data-annotations`** — 設計師標注，最高優先
2. **Node 命名** — 透露功能意圖
3. **末尾 `These styles are contained in the design:` 區塊** — 提取 Token 名稱（**必讀，不可略過**）
4. **元件 variants** — 列舉所有 `type=` 差異
5. **條件渲染** — `{isBtn && ...}` 等邏輯
 
### 樣式 Token 對應規則
 
從 `These styles are contained in the design:` 提取，對應到每個樣式元素：
 
| 顏色值 | Token 名稱 | hex 輸出 |
|--------|-----------|---------|
| `#000000` | `black/100` | `#000000` |
| `rgba(0,0,0,0.7)` | `black/70` | `#000000B3` |
| `rgba(0,0,0,0.5)` | `black/50` | `#00000080` |
| `rgba(0,0,0,0.3)` | `black/30` | `#0000004D` |
| `rgba(0,0,0,0.1)` | `black/10` | `#0000001A` |
| `#333333` | `gray/notification` | `#333333` |
 
JSON styleSpec 格式：
```json
{
  "textStyleToken": "文字/Header 2",
  "fontWeight": "Semibold",
  "fontSize": "18sp",
  "textColorToken": "black/100",
  "textColor": "#000000"
}
```
 
**Node 太大時**：立即停止，不要 retry，告知用戶：
```
此 Figma node 內容太大，無法一次解析。
請在 Figma 中選取較小的區塊，右鍵 → Copy link，提供小區塊的網址。
```
 
---
 
## 步驟 3：交叉驗證並輸出報告
 
| 狀況 | 處理 |
|------|------|
| 視覺 + annotation 一致 | confidence: high |
| 只有視覺 | confidence: medium，標注「需確認」 |
| 只有 annotation | confidence: medium，以 annotation 為準 |
| 兩者衝突 | **以 annotation 為準**，notes 記錄衝突細節，confidence: low |
 
**需求類型判斷**：
- 🗑️ 移除：Before 有 After 沒有，或 annotation 含「移除/刪除/隱藏」
- ➕ 新增：Before 沒有 After 有，或含「新增/添加」
- 🔧 修改：樣式/文字/佈局變更
 
### Markdown 報告格式
 
```markdown
# Figma 設計稿分析報告
 
## 設計稿資訊
- **After URL**: [URL]
- **Before URL**: [URL 或 無]
- **功能標題**: [從 node 名稱提取]
- **分析時間**: [YYYY-MM-DD HH:mm]
 
---
 
## 需求摘要
共 X 項：🗑️ 移除 X 項 / ➕ 新增 X 項 / 🔧 修改 X 項
 
---
 
## 詳細需求
 
### 1. [需求標題]
- **類型**：🔧 修改
- **來源**：annotation / 視覺比對 / 兩者一致
- **說明**：
  - [具體說明]
```
 
---
 
## 步驟 4：儲存 JSON
 
**檔案命名**：
 
1. 從 `.git/HEAD` 讀取分支名，提取 SPEC ID：
   - `feature/pt[NUMBER]_*` → `SPEC-[NUMBER]`
   - `feature/SPEC-[NUMBER]-*` → `SPEC-[NUMBER]`
   - 無法偵測 → `SPEC-XXX`
2. 路徑：`figma-analysis/figma-SPEC-{id}-{slug}.json`
 
**JSON 結構**：
 
```json
{
  "figmaUrl": {
    "after": "[After URL]",
    "before": "[Before URL 或 null]"
  },
  "featureName": "[功能名稱]",
  "featureType": "[UI調整/列表/表單/認證/即時/其他]",
  "dataStrategy": "[僅線上/離線優先/混合模式/不涉及資料]",
  "analysisMethod": "[before-after-comparison / after-only]",
  "requirements": [
    {
      "id": "REQ-001",
      "type": "removed | added | modified",
      "title": "[需求標題]",
      "description": "[詳細說明]",
      "source": "annotation | visual | both",
      "confidence": "high | medium | low",
      "notes": "[衝突細節或需確認事項，可為空]"
    }
  ],
  "summary": {
    "removed": "[移除摘要]",
    "added": "[新增摘要]",
    "modified": "[修改摘要]"
  },
  "visualDiff": {
    "newElements": [],
    "removedElements": [],
    "changedStyles": [],
    "changedLayout": []
  },
  "screens": {
    "after": { "nodeId": "[nodeId]", "description": "[說明]" },
    "before": { "nodeId": "[nodeId 或 null]", "description": "[說明 或 null]" }
  },
  "componentVariants": [
    "[ComponentName]: Variant1（說明）、Variant2（說明）"
  ],
  "analysisTime": "[YYYY-MM-DD HH:mm:ss]"
}
```
