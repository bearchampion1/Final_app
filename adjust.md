# 專案修改紀錄 (Adjustments)

## 1. 資料儲存架構遷移：從記憶體暫存 (In-memory) 遷移至 Room 資料庫

為了確保待辦事項資料在 App 重新啟動後仍能保留，我將資料儲存層從原本的記憶體變數遷移到了 Room 持久化架構。

### 變更項目：
- **依賴管理**：
  - 在 `gradle/libs.versions.toml` 定義 Room 版本 (2.6.1)。
  - 在 `app/build.gradle.kts` 引入 `androidx.room:room-runtime`, `room-ktx` 並使用 `annotationProcessor` 進行處理。
- **資料模型 (Data Model)**：
  - 更新 `TodoItem.kt`：加上 `@Entity` 註解並設定 `@PrimaryKey(autoGenerate = true)`。
- **資料存取層 (Data Access Layer)**：
  - 新增 `TodoDao.kt`：定義 SQL 查詢語句 (getAll, insert, update, getById)。
  - 新增 `TodoDatabase.kt`：實作 RoomDatabase 單例模式，並提供 `allowMainThreadQueries` 以支援現有同步邏輯。
- **存儲庫 (Repository)**：
  - 移除 `InMemoryTodoRepository.kt`。
  - 新增 `RoomTodoRepository.kt`：串接 `TodoDao` 實現持久化儲存介面。
- **初始化 (Initialization)**：
  - 修改 `MainActivity.kt`：在 `onCreate` 中實例化資料庫，並將 `RoomTodoRepository` 傳遞給 `TodoController`。
- **預覽與測試 (Preview & Test)**：
  - 更新 `TodoApp.kt` 的 Preview 區塊，使用私有的 FakeRepository 替代原本被刪除的實體類別。
  - 更新 `TodoControllerTest.kt`，新增了針對「新增、標記完成/未完成」以及「刪除功能」的自動化測試，確保邏輯正確性。

## 2. 新增刪除功能 (Delete Functionality)

新增了刪除待辦事項的功能，允許使用者從 UI 觸發並從 Room 資料庫中移除資料。

### 變更項目：
- **資料存取層 (Data Access Layer)**：
  - 在 `TodoDao.kt` 新增 `deleteById(id: Int)` 查詢。
- **存儲庫 (Repository)**：
  - 在 `TodoRepository` 介面新增 `deleteTodo(id: Int)`。
  - 在 `RoomTodoRepository.kt` 實作該刪除邏輯。
- **邏輯控制 (Controller)**：
  - 在 `TodoController.kt` 新增 `deleteTodo(id: Int)` 方法，執行刪除並更新 UI 狀態。
- **介面 (UI)**：
  - 修改 `TodoItemMenu`：在下拉選單中新增「Delete」選項。
  - 修改 `TodoRow`, `TodoScreen` 及 `TodoApp`：串接刪除事件的回呼函式 (Callback)。

## 3. 專案維護與效能優化 (Maintenance & Optimization)

根據編譯警告與效能提示進行了以下調整：
- **修復棄用警告 (Deprecation Fixes)**：
  - 在 `TodoApp.kt` 中將 `Icons.Filled.ArrowBack` 更換為支援鏡像顯示的 `Icons.AutoMirrored.Filled.ArrowBack`。
  - 將 `Divider` 組件更新為 Material 3 建議使用的 `HorizontalDivider`。
- **編譯效能與穩定性優化**：
  - 新增 `gradle.properties` 檔案，提升 Gradle 記憶體分配 (`Xmx2048m`)。
  - **解決閃退問題**：修正了 Room 註解處理器 (Annotation Processor) 的設定。從不支援 Kotlin 的 `annotationProcessor` 遷移至 **KSP (Kotlin Symbol Processing)**，解決了 `TodoDatabase_Impl does not exist` 導致的啟動閃退。
  - 調整 Kotlin 版本至 `2.1.0` 並匹配相應的 KSP 版本以確保與 AGP 9.2.1 的相容性。
  - 在 `gradle.properties` 加入 `android.disallowKotlinSourceSets=false` 以解決 KSP 與 AGP 9.0+ 內建 Kotlin 支援的原始碼路徑衝突。
  - **修復 SDK 版本衝突**：移除了要求 Android API 37 的 `androidx.core:core-ktx:1.19.0` 重複依賴，統一使用與當前環境相容的版本。
