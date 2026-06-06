# 專案修改說明

根據需求與系統優化計畫，我已對專案進行了以下調整並加入了詳細的中文註解：

### 1. 資料庫架構遷移 (Requirement 1)
- **Room 架構實現**：已將記憶體儲存遷移至 Room 資料庫。
- **程式碼位置與註解**：
    - `data/TodoItem.kt`：定義資料庫表格與 `isUrgent` 欄位。
    - `data/TodoDao.kt`：定義 CRUD 資料操作介面。
    - `data/TodoDatabase.kt`：資料庫實例管理 (Singleton)。

### 2. 刪除功能實作 (Requirement 2)
- **實作內容**：在 `TodoItemMenu` 中加入 Delete 選項，並透過 Repository 執行資料庫刪除。
- **註解說明**：相關邏輯已在 `TodoController.kt` 與 `RoomTodoRepository.kt` 中標註。

### 3. 搜尋與 UI 遷移 (Requirement 4)
- **搜尋功能**：
    - 按鈕更名為 「Search」。
    - 實作不區分大小寫的標題篩選邏輯。
- **FAB 按鈕**：
    - 將新增事項功能遷移至右下角 `FloatingActionButton`。
- **緊急項目標記**：
    - 在輸入框加入警告圖示，支援在新增時標記事項為「緊急」。

### 4. 穩定性與效能優化
- **螢幕旋轉處理**：使用 `rememberSaveable` 確保旋轉手機時 App 停留在當前頁面，不遺失操作狀態。
- **深色模式優化**：修正搜尋按鈕在深色模式下的文字顏色為白色，提升易讀性。
- **代碼註解**：對 `TodoApp.kt` 與 `TodoController.kt` 進行了全面的中文化邏輯註解。

### 5. 功能指導
- **使用說明頁面**：新增了 `InstructionsScreen` 頁面，引導使用者如何進行新增、搜尋、設定及測試通知。
