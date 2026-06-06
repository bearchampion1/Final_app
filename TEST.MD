# 測試文件 (Testing Documentation)

本文件說明專案中自動化測試的配置、檔案位置以及測試邏輯。

## 1. 測試檔案位置
`app/src/test/java/tw/edu/example/aitodostarter/ui/TodoControllerTest.kt`

## 2. 測試架構說明
測試採用 JUnit 4 框架，並透過自定義的 `FakeTodoRepository` 來模擬資料儲存層。這使得測試不需要依賴真實的 Room 資料庫或 Android 環境，能夠在 JVM 上快速執行。

### 模擬類別：FakeTodoRepository
實作 `TodoRepository` 介面，使用內建的 `MutableList` 儲存測試資料，支援：
- `getTodos()`: 獲取清單。
- `addTodo(title)`: 新增項目。
- `toggleTodo(id)`: 切換完成狀態。
- `deleteTodo(id)`: 刪除項目。

## 3. 測試案例 (Test Cases)

### A. 新增項目測試 (`addTodo_withNonBlankInput_addsTodoAndClearsInput`)
- **動作**：輸入非空白文字並執行新增。
- **驗證**：輸入框應被清空，且待辦清單中應包含該項目。

### B. 狀態切換邏輯測試 (`toggleTodo_toDoneAndThenToUndone_worksCorrectly`)
- **動作**：新增項目 -> 執行切換 (Toggle) -> 再次執行切換。
- **驗證**：
  1. 第一次切換後，項目狀態應為 `isDone = true`。
  2. 第二次切換後，項目狀態應回到 `isDone = false`。

### C. 刪除功能測試 (`deleteTodo_afterMarkingDone_removesItem`)
- **動作**：新增項目 -> 標記完成 -> 執行刪除。
- **驗證**：該項目應從 `controller.state.todos` 清單中完全移除。

### D. 多項目隔離刪除測試 (`deleteTodo_withMultipleItems_onlyRemovesTargetItem`)
- **動作**：在擁有多個項目的清單中，指定刪除其中一個 ID。
- **驗證**：
  1. 清單總數應減 1。
  2. 目標 ID 的項目應不存在。
  3. 非目標 ID 的項目應繼續保留在清單中。

## 4. 如何執行測試
在 Android Studio 中，您可以對著 `TodoControllerTest` 類別按右鍵，選擇 **"Run 'TodoControllerTest'"**。
或是在終端機執行：
```bash
./gradlew :app:testDebugUnitTest
```
