# 設定頁面優化說明 (Setting Interface Optimization)

本文件說明了針對「設定 (Settings)」頁面所進行的介面優化、功能擴充，以及目前正在進行中的「緊急項目」功能規劃。

### 1. 介面設計更新 (UI Redesign)
- **清單式佈局**：將原本零散的按鈕改為現代化的清單式設計（參考 Instagram 風格），使用 `LazyColumn` 實作。
- **自定義清單項 (SettingsListItem)**：每個項目包含左側功能圖示、中間功能名稱及狀態描述。
- **區塊化管理**：使用 `SettingHeader` 將設定分為七大類別，結構更清晰。
- **底部導航按鈕**：新增「切換個人檔案」（藍色）與「登出」（紅色）按鈕，並以分隔線區隔。

### 2. 功能類別與項目實作 (Implemented Categories)

#### 一、帳號與個人資料
- **帳號建立功能**：新增了「使用 Google 建立帳號」（藍色品牌色）與「使用 Email 建立帳號」入口。

#### 二、通知設定
- **每日提醒時間**：整合原本功能，點擊後彈出時間選擇器，清單即時顯示當前設定（例如：09:00）。
- **立即測試通知**：保留測試通知觸發功能。

#### 四、顯示與外觀
- **主題色調整 (Primary Color)**：點擊可在五種顏色（紫、紅、綠、藍、橘）間循環切換。
- **深色模式 (Dark Mode)**：點擊即可即時切換深色/淺色主題。

#### 七、說明與支援
- **使用說明**：新增專屬跳轉頁面 (`InstructionsScreen`)，指導使用者如何操作系統（新增、搜尋、設定等）。

---

### 3. 「緊急項目」功能規劃 (Urgent Feature - Planned)

目前正計畫將「已說讚」更名為「緊急」，並實作相關連動功能。以下為修改位置與內容：

#### 資料層 (Data Layer)
- **`TodoItem.kt`**：預計新增 `val isUrgent: Boolean = false` 欄位。
- **`TodoDao.kt`**：預計新增 `getUrgentAll()` 查詢方法。
- **`TodoRepository.kt` & `RoomTodoRepository.kt`**：
    - 預計新增 `getUrgentTodos()` 方法。
    - 預計修改 `addTodo()` 簽名以支援 `isUrgent` 參數。

#### 邏輯層 (Controller Layer)
- **`TodoController.kt`**：
    - `TodoUiState` 中預計新增 `isUrgentInput` 與 `showUrgentOnly` 狀態。
    - 預計新增 `toggleUrgentInput()` 與 `setShowUrgentOnly(Boolean)` 方法。
    - 預計修改 `refreshList()` 邏輯，支援僅顯示緊急項目的篩選。

#### 介面層 (UI Layer)
- **`TodoApp.kt`**：
    - **新增清單畫面**：預計在輸入框加入警告圖示，讓使用者切換是否為緊急事項。
    - **代辦事項列**：預計若事項為緊急，在標題前顯示紅色警告圖示。
    - **設定頁面**：預計將「已說讚」改為「緊急」，點擊後觸發緊急篩選並跳轉。
    - **主畫面提示**：預計在篩選模式時，顯示「Urgent Items」標題與「Show All」恢復按鈕。

#### 測試層 (Test Layer)
- **`TodoControllerTest.kt`**：預計新增針對緊急功能新增與篩選的測試案例。
