package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {
    private String currentMemberId;
    private JLabel welcomeLabel;
    private JLabel timeLabel;
    private JLabel statsLabel; // 統計情報ラベルをフィールドに追加
    private JButton searchButton;
    private JButton myRentalsButton;
    private JButton accountButton;
    private JButton logoutButton;
    private Timer clockTimer;
    private FileIO fileIO; // FileIOインスタンスを追加
    
    public MainFrame(String memberId) {
        this.currentMemberId = memberId;
        this.fileIO = FileIO.getInstance(); // FileIOインスタンスを取得
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
        startClock();
    }
    
    private void initializeComponents() {
        // ラベルの初期化
        welcomeLabel = new JLabel("Welcome, " + currentMemberId + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(70, 130, 180));
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timeLabel.setForeground(Color.GRAY);
        updateTimeLabel();
        
        // メインメニューボタン
        searchButton = new JButton("Search Costumes");
        myRentalsButton = new JButton("My Rentals");
        accountButton = new JButton("Account Settings");
        logoutButton = new JButton("Logout");
        
        // ボタンのスタイル設定
        setupButtonStyle(searchButton, new Color(70, 130, 180));
        setupButtonStyle(myRentalsButton, new Color(60, 179, 113));
        setupButtonStyle(accountButton, new Color(147, 112, 219));
        setupButtonStyle(logoutButton, new Color(220, 20, 60));
        
        // すべてのボタンのフォーカスを無効化（点滅停止）
        searchButton.setFocusable(false);
        myRentalsButton.setFocusable(false);
        accountButton.setFocusable(false);
        logoutButton.setFocusable(false);
        
        // ボタンサイズを統一（より大きく設定）
        Dimension buttonSize = new Dimension(200, 50);
        searchButton.setPreferredSize(buttonSize);
        searchButton.setMinimumSize(buttonSize);
        searchButton.setMaximumSize(buttonSize);
        
        myRentalsButton.setPreferredSize(buttonSize);
        myRentalsButton.setMinimumSize(buttonSize);
        myRentalsButton.setMaximumSize(buttonSize);
        
        accountButton.setPreferredSize(buttonSize);
        accountButton.setMinimumSize(buttonSize);
        accountButton.setMaximumSize(buttonSize);
        
        logoutButton.setPreferredSize(buttonSize);
        logoutButton.setMinimumSize(buttonSize);
        logoutButton.setMaximumSize(buttonSize);
        
        // ツールチップの設定
        searchButton.setToolTipText("Browse and search available costumes");
        myRentalsButton.setToolTipText("View your current and past rentals");
        accountButton.setToolTipText("Update your profile information");
        logoutButton.setToolTipText("Sign out from the system");

        // --- ここから追加 ---
        // 各ボタンにホバーエフェクトを追加
        addHoverEffect(searchButton);
        addHoverEffect(myRentalsButton);
        addHoverEffect(accountButton);
        addHoverEffect(logoutButton);
        // --- ここまで追加 ---
    }
    
    private void setupButtonStyle(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK); // 文字色を黒に変更
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true); // 背景色を確実に表示
    }

    /**
     * ボタンにマウスホバー時の拡大エフェクトを追加します。
     * @param button エフェクトを適用するボタン
     */
    private void addHoverEffect(JButton button) {
        Dimension originalSize = new Dimension(200, 50);
        Dimension hoverSize = new Dimension(220, 55);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setPreferredSize(hoverSize);
                button.setMinimumSize(hoverSize);
                button.setMaximumSize(hoverSize);
                button.revalidate();
                if (button.getParent() != null) {
                    button.getParent().revalidate();
                    button.getParent().repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setPreferredSize(originalSize);
                button.setMinimumSize(originalSize);
                button.setMaximumSize(originalSize);
                button.revalidate();
                if (button.getParent() != null) {
                    button.getParent().revalidate();
                    button.getParent().repaint();
                }
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // ヘッダーパネル
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // システムタイトル
        JLabel systemTitle = new JLabel("Costume Rental Management System");
        systemTitle.setFont(new Font("Arial", Font.BOLD, 18));
        systemTitle.setForeground(new Color(70, 130, 180));
        
        // ヘッダー情報パネル
        JPanel headerInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerInfoPanel.setBackground(new Color(245, 245, 245));
        headerInfoPanel.add(systemTitle);
        
        // 時刻表示パネル
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timePanel.setBackground(new Color(245, 245, 245));
        timePanel.add(timeLabel);
        
        headerPanel.add(headerInfoPanel, BorderLayout.WEST);
        headerPanel.add(timePanel, BorderLayout.EAST);
        
        // ウェルカムパネル
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20)); // 上下の余白を狭く
        welcomePanel.add(welcomeLabel);
        
        // メインメニューパネル
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50)); // 上の余白を狭く
        
        // ボタンを中央揃えで追加
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        myRentalsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        accountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        menuPanel.add(Box.createVerticalStrut(5)); // 上の余白を狭く
        menuPanel.add(searchButton);
        menuPanel.add(Box.createVerticalStrut(15)); // ボタン間の余白
        menuPanel.add(myRentalsButton);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(accountButton);
        menuPanel.add(Box.createVerticalStrut(25)); // ログアウトボタンの前に大きめの余白
        menuPanel.add(logoutButton);
        menuPanel.add(Box.createVerticalStrut(10)); // 下の余白
        
        JPanel statsPanel = createStatsPanel();
        
        // フレームに追加
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(welcomePanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statsPanel.setBackground(new Color(240, 248, 255));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // 統計情報ラベル（実際の数値を表示）
        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statsLabel.setForeground(Color.GRAY);
        updateStatsLabel(); // 初期値を設定
        
        statsPanel.add(statsLabel);
        return statsPanel;
    }
    
    private void setupEventListeners() {
        searchButton.addActionListener(e -> openCostumeSearch());
        
        myRentalsButton.addActionListener(e -> openMyRentals());
        
        accountButton.addActionListener(e -> openAccountSettings());
        
        logoutButton.addActionListener(e -> handleLogout());
    }
    
    private void setupFrame() {
        setTitle("Costume Rental System - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(540, 560); // 高さを500から420に変更
        setLocationRelativeTo(null); // 画面中央に配置
        setResizable(true);
        setMinimumSize(new Dimension(500, 350)); // 最小サイズも調整
    }
    
    private void startClock() {
        // 1秒ごとに時刻を更新するタイマー
        clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimeLabel();
                updateStatsLabel(); // 統計情報も定期的に更新
            }
        });
        clockTimer.start();
    }
    
    private void updateTimeLabel() {
        LocalDateTime now = LocalDateTime.now();
        String timeText = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        timeLabel.setText(timeText);
    }
    
    /**
     * 統計情報ラベルを更新します
     */
    private void updateStatsLabel() {
        try {
            int activeRentals = fileIO.getActiveRentalsCount();
            int availableCostumes = fileIO.getAvailableCostumesCount();
            
            String statsText = String.format("System Status: Online | Active Rentals: %d | Available Costumes: %d", 
                                            activeRentals, availableCostumes);
            statsLabel.setText(statsText);
        } catch (Exception e) {
            statsLabel.setText("System Status: Online | Active Rentals: -- | Available Costumes: --");
        }
    }
    
    // 各機能を開くメソッド
    private void openCostumeSearch() {
        try {
            CostumeSearchFrame searchFrame = new CostumeSearchFrame(currentMemberId);
            searchFrame.setVisible(true);
        } catch (Exception e) {
            showErrorMessage("Failed to open costume search: " + e.getMessage());
        }
    }
    
    private void openMyRentals() {
        try {
            MyRentalsFrame rentalsFrame = new MyRentalsFrame(currentMemberId);
            rentalsFrame.setVisible(true);
        } catch (Exception e) {
            showErrorMessage("Failed to open rentals: " + e.getMessage());
        }
    }
    
    private void openAccountSettings() {
        try {
            AccountSettingsFrame accountFrame = new AccountSettingsFrame(currentMemberId);
            accountFrame.setVisible(true);
            this.setVisible(false); // メイン画面を隠す
        } catch (Exception e) {
            showErrorMessage("Failed to open account settings: " + e.getMessage());
        }
    }
    
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            // タイマーを停止
            if (clockTimer != null) {
                clockTimer.stop();
            }
            
            // ログイン画面に戻る
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LoginFrame().setVisible(true);
                    MainFrame.this.dispose();
                }
            });
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // 現在のメンバーIDを取得するメソッド
    public String getCurrentMemberId() {
        return currentMemberId;
    }
    
    // ウィンドウが閉じられる際の処理
    @Override
    public void dispose() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        super.dispose();
    }
}