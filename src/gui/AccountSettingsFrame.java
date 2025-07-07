package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AccountSettingsFrame extends JFrame {
    private String currentMemberId;
    private FileIO fileIO;
    private FileIO.MemberData currentMemberData;
    private MainFrame parentMainFrame; // 親のMainFrameの参照を追加
    
    // 情報表示用フィールド
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JLabel memberIdLabel;
    private JLabel registrationDateLabel;
    
    // パスワード変更用フィールド
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    
    // ボタン
    private JButton updateInfoButton;
    private JButton changePasswordButton;
    private JButton backButton;
    
    // タブパネル
    private JTabbedPane tabbedPane;
    
    public AccountSettingsFrame(String memberId, MainFrame parentFrame) {
        this.currentMemberId = memberId;
        this.parentMainFrame = parentFrame; // 親フレームの参照を保存
        this.fileIO = new FileIO();
        loadMemberData();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
    }
    
    private void loadMemberData() {
        // FileIOから会員データを取得
        try {
            currentMemberData = fileIO.getMemberData(currentMemberId);
            
            if (currentMemberData == null) {
                showErrorMessage("Failed to load member data.");
                this.dispose();
            }
        } catch (Exception e) {
            showErrorMessage("Error loading member data: " + e.getMessage());
            this.dispose();
        }
    }

    
    private void initializeComponents() {
        // 情報表示用フィールドの初期化（幅を拡大）
        nameField = new JTextField(30);
        emailField = new JTextField(30);
        phoneField = new JTextField(30);
        addressArea = new JTextArea(4, 30);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        
        memberIdLabel = new JLabel();
        registrationDateLabel = new JLabel();
        
        // パスワード変更用フィールド（幅を拡大）
        currentPasswordField = new JPasswordField(30);
        newPasswordField = new JPasswordField(30);
        confirmPasswordField = new JPasswordField(30);
        
        // ボタン
        updateInfoButton = new JButton("Update Information");
        changePasswordButton = new JButton("Change Password");
        backButton = new JButton("Back to Main");
        
        // ボタンスタイル設定（文字を黒色に変更）
        updateInfoButton.setBackground(new Color(60, 179, 113));
        updateInfoButton.setForeground(Color.BLACK);
        updateInfoButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        changePasswordButton.setBackground(new Color(255, 165, 0));
        changePasswordButton.setForeground(Color.BLACK);
        changePasswordButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.BLACK);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        // タブパネル（フォントを改善）
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // フィールドに現在のデータを設定
        populateFields();
    }
    
    private void populateFields() {
        if (currentMemberData != null) {
            nameField.setText(currentMemberData.getName());
            emailField.setText(currentMemberData.getEmail());
            phoneField.setText(currentMemberData.getPhone());
            addressArea.setText(currentMemberData.getAddress());
            
            // ラベルのフォントを改善
            memberIdLabel.setText("Member ID: " + currentMemberData.getMemberId());
            memberIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            registrationDateLabel.setText("Registration Date: 2025-06-30");
            registrationDateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            registrationDateLabel.setForeground(Color.GRAY);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // タイトルパネル
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(245, 245, 245));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Account Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 130, 180));
        titlePanel.add(titleLabel);
        
        // 基本情報タブ
        JPanel infoTab = createInfoTab();
        tabbedPane.addTab("Personal Information", infoTab);
        
        // パスワード変更タブ
        JPanel passwordTab = createPasswordTab();
        tabbedPane.addTab("Change Password", passwordTab);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(backButton);
        
        // フレームに追加
        add(titlePanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInfoTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 会員ID（変更不可）
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(memberIdLabel, gbc);
        
        // 登録日（変更不可）
        gbc.gridy = 1;
        panel.add(registrationDateLabel, gbc);
        
        // 名前
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);
        
        // メール
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(emailField, gbc);
        
        // 電話番号
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(phoneField, gbc);
        
        // 住所
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(addressLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3; // 住所フィールドに適度な縦幅を確保
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        addressScrollPane.setPreferredSize(new Dimension(300, 120)); // 高さを増加
        addressScrollPane.setMinimumSize(new Dimension(300, 120));
        panel.add(addressScrollPane, gbc);
        
        // 更新ボタン - weightを調整してボタンが潰れないようにする
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;
        gbc.weighty = 0; // weightを0に戻してボタンが潰れないようにする
        gbc.insets = new Insets(30, 10, 20, 10); // 上の余白を増加
        updateInfoButton.setPreferredSize(new Dimension(200, 40));
        updateInfoButton.setMinimumSize(new Dimension(200, 40)); // 最小サイズを明示的に設定
        panel.add(updateInfoButton, gbc);
        
        return panel;
    }
    
    private JPanel createPasswordTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 説明ラベル
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("Enter your current password and new password:");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel, gbc);
        
        // 現在のパスワード
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel currentPasswordLabel = new JLabel("Current Password:");
        currentPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(currentPasswordLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(currentPasswordField, gbc);
        
        // 新しいパスワード
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(newPasswordLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(newPasswordField, gbc);
        
        // パスワード確認
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        JLabel confirmPasswordLabel = new JLabel("Confirm New Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(confirmPasswordField, gbc);
        
        // パスワード変更ボタン
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 10, 10, 10);
        changePasswordButton.setPreferredSize(new Dimension(200, 40));
        panel.add(changePasswordButton, gbc);
        
        return panel;
    }
    
    private void setupEventListeners() {
        updateInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdateInfo();
            }
        });
        
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleChangePassword();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleBack();
            }
        });
    }
    
    private void setupFrame() {
        setTitle("Costume Rental System - Account Settings");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(650, 600); // ウィンドウの高さをさらに増加
        setLocationRelativeTo(null);
        setResizable(false);
        
        // ウィンドウクローズ処理
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                handleBack();
            }
        });
    }
    
    private void handleUpdateInfo() {
        // 入力値を取得
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        
        // バリデーション
        if (!validateUpdateInfo(name, email, phone, address)) {
            return;
        }
        
        try {
            // FileIOを使用して会員情報を更新
            boolean success = fileIO.updateMemberInfo(currentMemberId, name, email, phone, address);
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Information updated successfully!",
                    "Update Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // データを再読み込み
                loadMemberData();
                populateFields();
            } else {
                showErrorMessage("Failed to update information. Please try again.");
            }
        } catch (Exception e) {
            showErrorMessage("Error updating information: " + e.getMessage());
        }
    }
    
    private void handleChangePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // バリデーション
        if (!validatePasswordChange(currentPassword, newPassword, confirmPassword)) {
            return;
        }
        
        try {
            // 現在のパスワードを確認
            if (!fileIO.isValidLogin(currentMemberId, currentPassword)) {
                showErrorMessage("Current password is incorrect.");
                currentPasswordField.requestFocus();
                return;
            }
            
            // FileIOを使用してパスワードを変更
            boolean success = fileIO.changePassword(currentMemberId, newPassword);
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Password changed successfully!",
                    "Password Update Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // パスワードフィールドをクリア
                clearPasswordFields();
            } else {
                showErrorMessage("Failed to change password. Please try again.");
            }
        } catch (Exception e) {
            showErrorMessage("Error changing password: " + e.getMessage());
        }
    }
    
    private boolean validateUpdateInfo(String name, String email, String phone, String address) {
        // 名前のバリデーション
        if (name.isEmpty()) {
            showErrorMessage("Please enter your name.");
            nameField.requestFocus();
            return false;
        }
        
        // メールのバリデーション
        if (email.isEmpty()) {
            showErrorMessage("Please enter your email address.");
            emailField.requestFocus();
            return false;
        }
        
        if (!isValidEmail(email)) {
            showErrorMessage("Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }
        
        // 電話番号のバリデーション
        if (phone.isEmpty()) {
            showErrorMessage("Please enter your phone number.");
            phoneField.requestFocus();
            return false;
        }
        
        // 住所のバリデーション
        if (address.isEmpty()) {
            showErrorMessage("Please enter your address.");
            addressArea.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean validatePasswordChange(String currentPassword, String newPassword, String confirmPassword) {
        // 現在のパスワードチェック
        if (currentPassword.isEmpty()) {
            showErrorMessage("Please enter your current password.");
            currentPasswordField.requestFocus();
            return false;
        }
        
        // 新しいパスワードチェック
        if (newPassword.isEmpty()) {
            showErrorMessage("Please enter a new password.");
            newPasswordField.requestFocus();
            return false;
        }
        
        if (newPassword.length() < 6) {
            showErrorMessage("New password must be at least 6 characters long.");
            newPasswordField.requestFocus();
            return false;
        }
        
        // パスワード確認チェック
        if (!newPassword.equals(confirmPassword)) {
            showErrorMessage("New passwords do not match.");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        // 現在のパスワードと同じかチェック
        if (currentPassword.equals(newPassword)) {
            showErrorMessage("New password must be different from current password.");
            newPasswordField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }
    
    private void clearPasswordFields() {
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }
    
    private void handleBack() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to go back?\nAny unsaved changes will be lost.",
            "Confirm Back",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            // 既存のメインフレームを表示し、このフレームを閉じる
            if (parentMainFrame != null) {
                parentMainFrame.setVisible(true);
                parentMainFrame.toFront(); // フレームを前面に表示
                parentMainFrame.requestFocus(); // フォーカスを設定
            }
            this.dispose();
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}