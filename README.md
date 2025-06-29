## 貸衣装管理システム
Java最終課題の貸衣装管理システムのソースコードです。

## 要件定義
- ログイン画面  
- 未定
---  
# 🎯 現在の開発状況

## ✅ 完成済み

- **ログイン画面**：`LoginFrame.java`
- **新規登録画面**：`RegistrationFrame.java`
- **データ管理**：`FileIO.java`

## 🔄 次に開発すべきページ（フローチャート順）

### 3. メインページ画面 `MainFrame.java`
- ログイン成功後に表示される画面  
- 各機能へのナビゲーションメニュー  
- 衣装検索、レンタル、返却、予約などへのアクセス  

### 4. 衣装検索画面 `CostumeSearchFrame.java`
- 衣装の検索・閲覧機能  
- サイズ、料金、期間などの条件検索  

### 5. レンタル画面 `RentalFrame.java`
- 衣装のレンタル手続き  
- レンタル期間設定  
- 料金計算  

### 6. 自分のレンタル状況画面 `MyRentalFrame.java`
- 現在借りている衣装の一覧表示  
- レンタル期限の確認  

### 7. 返却画面 `ReturnFrame.java`
- 衣装の返却手続き  
- 延滞金計算（期限超過時）  

### 8. 予約画面 `ReservationFrame.java`
- 他の会員が借りている衣装の予約  
- 予約待ちリスト管理  

### 9. 完了画面 `CompletionFrame.java`
- 各種手続き完了時の確認画面  

---

## 📦 追加で必要なデータクラス

### 👗 衣装関連
- `Costume.java`：衣装情報クラス  
- `CostumeService.java`：衣装管理ロジック  

### 📅 レンタル関連
- `Rental.java`：レンタル情報クラス  
- `RentalService.java`：レンタル管理ロジック  

### 📝 予約関連
- `Reservation.java`：予約情報クラス  
- `ReservationService.java`：予約管理ロジック

