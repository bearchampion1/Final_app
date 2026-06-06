# 自動化測試說明 (Automated Testing Report)

本文件詳述了針對 `AiTodoStarter` 系統核心邏輯所撰寫的自動化測試案例。

### 1. 測試環境 (Testing Environment)
- **測試框架**：JUnit 4
- **受測對象**：`TodoController` (邏輯層)
- **模擬組件**：`FakeTodoRepository` (模擬資料存取)

### 2. 測試案例清單 (Test Cases)

#### 代辦事項基礎操作 (Requirement 3)
- **`addTodo_withNonBlankInput_addsTodoAndClearsInput`**
    - **內容**：新增一筆非空白事項。
    - **預期**：輸入框清空，且事項出現在清單中。
- **`toggleTodo_toDoneAndThenToUndone_worksCorrectly`**
    - **內容**：將事項標記為 Done 後再標記為 Undone。
    - **預期**：成功切換狀態並通過斷言驗證。
- **`deleteTodo_afterMarkingDone_removesItem`**
    - **內容**：新增事項，標記完成後執行刪除。
    - **預期**：該事項完全從清單中移除。
- **`deleteTodo_withMultipleItems_onlyRemovesTargetItem`**
    - **內容**：在有多筆事項的情況下刪除其中一筆。
    - **預期**：僅選定項目消失，其餘項目保留。

#### 搜尋與篩選功能 (Requirement 4)
- **`search_withQuery_filtersList`**
    - **內容**：輸入特定關鍵字並按下 Search。
    - **預期**：清單僅顯示標題相符的項目。
- **`search_withBlankQuery_showsAll`**
    - **內容**：清空搜尋欄並按下 Search。
    - **預期**：清單恢復顯示所有資料。
- **`search_isCaseInsensitive`**
    - **內容**：使用不同的大小寫進行搜尋。
    - **預期**：搜尋結果不受字母大小寫影響。

#### 緊急事項功能驗證 (Urgent Feature)
- **`addTodo_asUrgent_appearsInFilteredList`**
    - **內容**：新增一筆標記為「緊急」的事項。
    - **預期**：資料模型中的 `isUrgent` 屬性為 `true`。
- **`setShowUrgentOnly_filtersListCorrectly`**
    - **內容**：切換至「僅顯示緊急事項」模式。
    - **預期**：清單只出現標記為緊急的項目，過濾掉一般事項。

### 3. 如何執行測試
1. 在 Android Studio 中開啟 `TodoControllerTest.kt`。
2. 點擊類別名稱旁的 **Run** 圖示執行所有測試。
3. 觀察 **Run 視窗** 確認所有綠色勾選皆已通過。
