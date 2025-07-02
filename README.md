## 貸衣装管理システム
Java最終課題の貸衣装管理システムのソースコードです。
## 要件定義
- 会員登録・ログイン機能
- 衣装の検索・閲覧・レンタル機能
- レンタル履歴管理・返却・延滞金処理
- 予約システム（貸出中衣装の予約待ち）
---  
# 🎯 現在の開発状況
## ✅ 完成済み
- **ログイン画面**：`LoginFrame.java`
- **新規登録画面**：`RegistrationFrame.java`
- **データ管理**：`FileIO.java`
- **メインページ**: `MainFrame.java`
- **アカウント設定画面**: `AccountSettingsFrame.java`
- **衣装検索画面**: `CostumeSearchFrame.java` 

## 📦 完成済みデータクラス
### 👗 衣装関連
- `Costume.java`：衣装情報クラス ✅
- `CostumeEvent.java`：イベント分類enum ✅
- `CostumeDataManager.java`：衣装データ管理 ✅

---
## 🔄 次に開発すべきページ（優先順位順） 
### 1. **レンタル画面** `RentalFrame.java` 🚀 **次の実装対象**
- 衣装のレンタル手続き画面
- レンタル期間設定・料金計算
- 会員情報確認・レンタル確定

### 2. **レンタル履歴画面** `MyRentalsFrame.java`
- 現在借りている衣装の一覧表示
- レンタル期限の確認・延滞状況表示

### 3. **返却画面** `ReturnFrame.java`
- 衣装の返却手続き
- 延滞金計算（期限超過時）
- 返却確認・在庫更新

### 4. **予約画面** `ReservationFrame.java`
- 他の会員が借りている衣装の予約
- 予約待ちリスト管理

### 5. **完了画面** `CompletionFrame.java`
- 各種手続き完了時の確認画面

---
## 📦 次に実装が必要なデータクラス
### 📅 レンタル関連（最優先）
- `Rental.java`：レンタル情報クラス
- `RentalService.java`：レンタル管理ロジック

### 📝 予約関連
- `Reservation.java`：予約情報クラス
- `ReservationService.java`：予約管理ロジック


