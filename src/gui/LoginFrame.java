package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gui.FileIO;

public class LoginFrame extends JFrame {
    private JTextField memberIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    
    public LoginFrame() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
    }
    
    private void initializeComponents() {
        // コンポーネント初期化
        memberIdField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        registerButton = new JButton("New Registration");
        
        // ボタンの色とフォント設定
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        registerButton.setBackground(new Color(60, 179, 113));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // タイトルパネル
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("Costume Rental Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        titlePanel.add(titleLabel);
        
        // ログインフォームパネル
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 会員IDラベルとフィールド
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel memberIdLabel = new JLabel("Member ID:");
        memberIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(memberIdLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(memberIdField, gbc);
        
        // パスワードラベルとフィールド
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        // ボタンパネル - より良い配置のため
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.insets = new Insets(10, 10, 10, 10);
        
        // ログインボタン
        buttonGbc.gridx = 0; buttonGbc.gridy = 0;
        buttonPanel.add(loginButton, buttonGbc);
        
        // 新規登録ボタン
        buttonGbc.gridx = 1; buttonGbc.gridy = 0;
        buttonPanel.add(registerButton, buttonGbc);
        
        // フレームにパネル追加
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        
        // Enterキーでログイン実行
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }
    
    private void setupFrame() {
        setTitle("Costume Rental System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null); // 画面中央に配置
        setResizable(false);
    }
    
    private void handleLogin() {
        String memberId = memberIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // 入力チェック
        if (memberId.isEmpty()) {
            showErrorMessage("Please enter Member ID.");
            memberIdField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showErrorMessage("Please enter Password.");
            passwordField.requestFocus();
            return;
        }
        
        //MemberServiceによる実際のログイン処理を実装
        if (authenticateUser(memberId, password)) {
            JOptionPane.showMessageDialog(this, 
                "Login successful! Welcome, " + memberId, 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            //メイン画面を開く
            MainFrame mainFrame = new MainFrame(memberId);
            mainFrame.setVisible(true);
            this.dispose();
            
        } else {
            showErrorMessage("Invalid Member ID or Password.");
            clearPasswordField();
        }
    }
    
    private void handleRegister() {
        // 新規登録画面を開く
        try {
            RegistrationFrame registerFrame = new RegistrationFrame(this);
            registerFrame.setVisible(true);
            this.setVisible(false); // ログイン画面を隠す
        } catch (Exception e) {
            showErrorMessage("Failed to open registration window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 新規登録画面から戻ってきた時に呼ばれるメソッド
    public void returnFromRegistration() {
        this.setVisible(true);
        this.toFront();
        // フィールドをクリアして新しい状態にする
        memberIdField.setText("");
        clearPasswordField();
    }
    
    //ログイン認証
    private boolean authenticateUser(String memberId, String password) {
        FileIO checkData = new FileIO();
        if (checkData.isValidLogin(memberId, password)) {
            return true; // 認証成功
        }
        return false; // 認証失敗
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void clearPasswordField() {
        passwordField.setText("");
        passwordField.requestFocus();
    }
    
    public static void main(String[] args) {
        // Look and Feel設定
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}