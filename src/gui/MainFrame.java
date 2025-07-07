package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainFrame extends JFrame {
    private String currentMemberId;
    private JLabel welcomeLabel;
    private JLabel timeLabel;
    private JLabel statsLabel;
    private JButton searchButton;
    private JButton myRentalsButton;
    private JButton accountButton;
    private JButton logoutButton;
    private Timer clockTimer;
    private FileIO fileIO;
    
    // ★ 追加: バッジ表示用のコンポーネント
    private JLayeredPane myRentalsContainer; // ボタンとバッジを包含するパネル
    private JLabel overdueBadge; // 延滞数表示バッジ
    private int currentOverdueCount = 0; // 現在の延滞数
    
    public MainFrame(String memberId) {
        this.currentMemberId = memberId;
        this.fileIO = FileIO.getInstance(); // FileIOインスタンスを取得
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
        startClock();
        
        // ★ 初期読み込み時に延滞数をチェック
        checkOverdueRentals();
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
        
        // ★ My Rentalsボタン用のコンテナとバッジを作成（中央配置修正版）
        createMyRentalsContainer();
        
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

        // 各ボタンにホバーエフェクトを追加
        addHoverEffect(searchButton);
        addMyRentalsHoverEffect(); // ★ My Rentalsボタンは専用のホバーエフェクト
        addHoverEffect(accountButton);
        addHoverEffect(logoutButton);
    }
    
    // ★ ここから追加: My Rentalsボタン用のコンテナとバッジを作成（中央配置修正版）
    // ★ 代替案：JLayeredPaneを使用した完全確実版
    private void createMyRentalsContainer() {
        // JLayeredPaneを使用してレイヤー管理を完全に制御
        myRentalsContainer = new JLayeredPane();
        myRentalsContainer.setOpaque(false);
        myRentalsContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // ★ My Rentalsボタンの設定（コンテナ内で中央に配置）
        myRentalsButton.setBounds(20, 5, 200, 50); // 左端から20pxに移動して中央に配置
        myRentalsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // バッジラベルを作成
        overdueBadge = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isVisible() || getText() == null || getText().isEmpty()) return;
                
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 背景を丸く描画
                g2.setColor(Color.RED);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // テキストを白色で描画
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight) / 2 - 2;
                
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        
        // ★ バッジの設定（ボタン位置に合わせて調整）
        overdueBadge.setBounds(205, 0, 22, 22); // ボタンの右上に配置
        overdueBadge.setFont(new Font("Arial", Font.BOLD, 10));
        overdueBadge.setVisible(false);
        overdueBadge.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // ★ JLayeredPaneに異なるレイヤーで追加（確実にレイヤー分離）
        myRentalsContainer.add(myRentalsButton, JLayeredPane.DEFAULT_LAYER);  // レイヤー 0
        myRentalsContainer.add(overdueBadge, JLayeredPane.POPUP_LAYER);       // レイヤー 200
        
        // ★ コンテナのサイズ設定（右側とホバー時の余白を十分に確保）
        Dimension containerSize = new Dimension(240, 70);
        myRentalsContainer.setPreferredSize(containerSize);
        myRentalsContainer.setMinimumSize(containerSize);
        myRentalsContainer.setMaximumSize(containerSize);
        
        // クリックイベントの追加
        myRentalsContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openMyRentals();
            }
        });
    }
    
    // ★ My Rentalsパネル全体のホバーエフェクト（中央配置対応版）
    private void addMyRentalsHoverEffect() {
        Color originalButtonColor = myRentalsButton.getBackground();
        Dimension originalContainerSize = myRentalsContainer.getPreferredSize();
        Dimension hoverContainerSize = new Dimension(originalContainerSize.width + 20, originalContainerSize.height + 5);

        // ★ 共通のホバーエフェクトアダプター
        java.awt.event.MouseAdapter hoverAdapter = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // ボタンの色を明るくする
                myRentalsButton.setBackground(originalButtonColor.brighter());
                
                // ★ パネル全体のサイズを変更（余白を保持）
                myRentalsContainer.setPreferredSize(hoverContainerSize);
                myRentalsContainer.setMinimumSize(hoverContainerSize);
                myRentalsContainer.setMaximumSize(hoverContainerSize);
                
                // ★ ボタンとバッジの位置を調整（中央を保持）
                myRentalsButton.setBounds(15, 8, 210, 55);
                if (overdueBadge.isVisible()) {
                    overdueBadge.setBounds(210, 3, 22, 22);
                }
                
                // レイアウトを更新
                myRentalsContainer.revalidate();
                if (myRentalsContainer.getParent() != null) {
                    myRentalsContainer.getParent().revalidate();
                    myRentalsContainer.getParent().repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // ボタンの色を元に戻す
                myRentalsButton.setBackground(originalButtonColor);
                
                // パネル全体のサイズを元に戻す
                myRentalsContainer.setPreferredSize(originalContainerSize);
                myRentalsContainer.setMinimumSize(originalContainerSize);
                myRentalsContainer.setMaximumSize(originalContainerSize);
                
                // ★ ボタンとバッジの位置を元に戻す（中央配置を保持）
                myRentalsButton.setBounds(20, 5, 200, 50);
                if (overdueBadge.isVisible()) {
                    overdueBadge.setBounds(205, 0, 22, 22);
                }
                
                // レイアウトを更新
                myRentalsContainer.revalidate();
                if (myRentalsContainer.getParent() != null) {
                    myRentalsContainer.getParent().revalidate();
                    myRentalsContainer.getParent().repaint();
                }
            }
        };

        // ★ 共通のクリックイベントアダプター
        java.awt.event.MouseAdapter clickAdapter = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openMyRentals(); // My Rentalsを開く
            }
        };

        // ★ パネル全体、ボタン、バッジのすべてに同じイベントを適用
        myRentalsContainer.addMouseListener(hoverAdapter);
        myRentalsContainer.addMouseListener(clickAdapter);
        
        myRentalsButton.addMouseListener(hoverAdapter);
        myRentalsButton.addMouseListener(clickAdapter);
        
        overdueBadge.addMouseListener(hoverAdapter);
        overdueBadge.addMouseListener(clickAdapter);
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
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        welcomePanel.add(welcomeLabel);
        
        // メインメニューパネル
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));
        
        // ボタンを中央揃えで追加
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        myRentalsContainer.setAlignmentX(Component.CENTER_ALIGNMENT); // ★ コンテナを使用
        accountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(searchButton);
        menuPanel.add(Box.createVerticalStrut(20)); // ★ 間隔を少し広げる
        menuPanel.add(myRentalsContainer); // ★ ボタンの代わりにコンテナを追加
        menuPanel.add(Box.createVerticalStrut(20)); // ★ 間隔を少し広げる
        menuPanel.add(accountButton);
        menuPanel.add(Box.createVerticalStrut(25));
        menuPanel.add(logoutButton);
        menuPanel.add(Box.createVerticalStrut(10));
        
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
        
        // ★ myRentalsButtonのActionListenerを削除（パネルのクリックイベントを使用）
        // myRentalsButton.addActionListener(e -> openMyRentals());
        
        accountButton.addActionListener(e -> openAccountSettings());
        
        logoutButton.addActionListener(e -> handleLogout());
    }
    
    private void setupFrame() {
        setTitle("Costume Rental System - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(580, 600); // ★ 幅と高さを少し大きくして余白を確保
        setLocationRelativeTo(null); // 画面中央に配置
        setResizable(true);
        setMinimumSize(new Dimension(520, 450)); // ★ 最小サイズも調整
    }
    
    private void startClock() {
        // 1秒ごとに時刻を更新するタイマー
        clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimeLabel();
                updateStatsLabel(); // ★ 延滞チェックは含まない
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
     * 統計情報ラベルを更新（延滞チェックを除外）
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
    
    // ★ 延滞チェックを独立したメソッドに分離
    /**
     * 延滞レンタルをチェックしてバッジを更新
     */
    public void checkOverdueRentals() {
        try {
            int overdueRentals = getOverdueRentalsForCurrentUser();
            updateOverdueBadge(overdueRentals);
        } catch (Exception e) {
            System.err.println("Error checking overdue rentals: " + e.getMessage());
        }
    }
    
    // ★ 現在のユーザーの延滞レンタル数を取得
    private int getOverdueRentalsForCurrentUser() {
        try {
            RentalService rentalService = new RentalService();
            rentalService.updateAllRentalStatuses(); // ステータスを最新に更新
            
            List<Rental> userRentals = rentalService.getActiveRentalsByMemberId(currentMemberId);
            return (int) userRentals.stream()
                    .filter(rental -> rental.getStatus() == Rental.RentalStatus.OVERDUE)
                    .count();
        } catch (Exception e) {
            System.err.println("Error getting overdue rentals: " + e.getMessage());
            return 0;
        }
    }
    
    // ★ 延滞バッジを更新
    private void updateOverdueBadge(int overdueCount) {
        if (overdueCount > 0 && overdueCount != currentOverdueCount) {
            // 延滞があり、数が変更された場合
            currentOverdueCount = overdueCount;
            overdueBadge.setText(String.valueOf(overdueCount));
            overdueBadge.setVisible(true);
            
            // バッジを最前面に確実に移動
            myRentalsContainer.setComponentZOrder(overdueBadge, 0);
            
            // ツールチップを更新（パネルとボタンの両方に設定）
            String tooltipText = overdueCount == 1 ? 
                "You have 1 overdue rental" : 
                "You have " + overdueCount + " overdue rentals";
            myRentalsButton.setToolTipText(tooltipText + " - Click to view details");
            myRentalsContainer.setToolTipText(tooltipText + " - Click to view details");
            
        } else if (overdueCount == 0 && currentOverdueCount != 0) {
            // 延滞がなくなった場合
            currentOverdueCount = 0;
            overdueBadge.setVisible(false);
            myRentalsButton.setToolTipText("View your current and past rentals");
            myRentalsContainer.setToolTipText("View your current and past rentals");
        }
        
        // レイアウトを更新
        myRentalsContainer.revalidate();
        myRentalsContainer.repaint();
    }
    
    // ★ 他のボタンのホバーエフェクト
    private void addHoverEffect(JButton button) {
        Color originalColor = button.getBackground();
        Dimension originalSize = button.getPreferredSize();
        Dimension hoverSize = new Dimension(originalSize.width + 20, originalSize.height + 5);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.brighter());
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
                button.setBackground(originalColor);
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
    
    /**
     * ボタンのスタイルを設定
     */
    private void setupButtonStyle(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK); // ★ 黒色のテキスト
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
    }
    
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
            
            // ★ My Rentals画面を開いた後、延滞状況を再チェック
            // WindowListenerを追加して、ウィンドウが閉じられた時に延滞状況を更新
            rentalsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    checkOverdueRentals(); // ★ 画面が閉じられた時に延滞状況を再チェック
                }
            });
            
        } catch (Exception e) {
            showErrorMessage("Failed to open rentals: " + e.getMessage());
        }
    }
    
    private void openAccountSettings() {
        try {
            AccountSettingsFrame accountFrame = new AccountSettingsFrame(currentMemberId, MainFrame.this);
            accountFrame.setVisible(true);
        } catch (Exception e) {
            showErrorMessage("Failed to open account settings: " + e.getMessage());
        }
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            // ログイン画面に戻る
            new LoginFrame().setVisible(true);
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
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