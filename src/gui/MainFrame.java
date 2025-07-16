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
   
    private JLayeredPane myRentalsContainer; 
    private JLabel overdueBadge; 
    private int currentOverdueCount = 0; 
    
    public MainFrame(String memberId) {
        this.currentMemberId = memberId;
        this.fileIO = FileIO.getInstance(); 
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
        startClock();
        
       
        checkOverdueRentals();
    }
    
    private void initializeComponents() {
     
        welcomeLabel = new JLabel("Welcome, " + currentMemberId + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(70, 130, 180));
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timeLabel.setForeground(Color.GRAY);
        updateTimeLabel();
        
        
        searchButton = new JButton("Search Costumes");
        myRentalsButton = new JButton("My Rentals");
        accountButton = new JButton("Account Settings");
        logoutButton = new JButton("Logout");
        
       
        createMyRentalsContainer();
        
    
        setupButtonStyle(searchButton, new Color(70, 130, 180));
        setupButtonStyle(myRentalsButton, new Color(60, 179, 113));
        setupButtonStyle(accountButton, new Color(147, 112, 219));
        setupButtonStyle(logoutButton, new Color(220, 20, 60));
        
    
        searchButton.setFocusable(false);
        myRentalsButton.setFocusable(false);
        accountButton.setFocusable(false);
        logoutButton.setFocusable(false);
        
        
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
        
  
        searchButton.setToolTipText("Browse and search available costumes");
        myRentalsButton.setToolTipText("View your current and past rentals");
        accountButton.setToolTipText("Update your profile information");
        logoutButton.setToolTipText("Sign out from the system");

    
        addHoverEffect(searchButton);
        addMyRentalsHoverEffect(); 
        addHoverEffect(accountButton);
        addHoverEffect(logoutButton);
    }
    
    
    private void createMyRentalsContainer() {
        
        myRentalsContainer = new JLayeredPane();
        myRentalsContainer.setOpaque(false);
        myRentalsContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
   
        myRentalsButton.setBounds(20, 5, 200, 50); 
        myRentalsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
   
        overdueBadge = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isVisible() || getText() == null || getText().isEmpty()) return;
                
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(Color.RED);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
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
        
       
        overdueBadge.setBounds(205, 0, 22, 22); 
        overdueBadge.setFont(new Font("Arial", Font.BOLD, 10));
        overdueBadge.setVisible(false);
        overdueBadge.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        myRentalsContainer.add(myRentalsButton, JLayeredPane.DEFAULT_LAYER);  
        myRentalsContainer.add(overdueBadge, JLayeredPane.POPUP_LAYER);      
        
        Dimension containerSize = new Dimension(240, 70);
        myRentalsContainer.setPreferredSize(containerSize);
        myRentalsContainer.setMinimumSize(containerSize);
        myRentalsContainer.setMaximumSize(containerSize);
        
      
        myRentalsContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openMyRentals();
            }
        });
    }
    

    private void addMyRentalsHoverEffect() {
        Color originalButtonColor = myRentalsButton.getBackground();
        Dimension originalContainerSize = myRentalsContainer.getPreferredSize();
        Dimension hoverContainerSize = new Dimension(originalContainerSize.width + 20, originalContainerSize.height + 5);


        java.awt.event.MouseAdapter hoverAdapter = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
          
                myRentalsButton.setBackground(originalButtonColor.brighter());
                
                myRentalsContainer.setPreferredSize(hoverContainerSize);
                myRentalsContainer.setMinimumSize(hoverContainerSize);
                myRentalsContainer.setMaximumSize(hoverContainerSize);
                
                myRentalsButton.setBounds(15, 8, 210, 55);
                if (overdueBadge.isVisible()) {
                    overdueBadge.setBounds(210, 3, 22, 22);
                }
                
                myRentalsContainer.revalidate();
                if (myRentalsContainer.getParent() != null) {
                    myRentalsContainer.getParent().revalidate();
                    myRentalsContainer.getParent().repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
    
                myRentalsButton.setBackground(originalButtonColor);
                
                myRentalsContainer.setPreferredSize(originalContainerSize);
                myRentalsContainer.setMinimumSize(originalContainerSize);
                myRentalsContainer.setMaximumSize(originalContainerSize);
  
                myRentalsButton.setBounds(20, 5, 200, 50);
                if (overdueBadge.isVisible()) {
                    overdueBadge.setBounds(205, 0, 22, 22);
                }
 
                myRentalsContainer.revalidate();
                if (myRentalsContainer.getParent() != null) {
                    myRentalsContainer.getParent().revalidate();
                    myRentalsContainer.getParent().repaint();
                }
            }
        };

        java.awt.event.MouseAdapter clickAdapter = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openMyRentals();
            }
        };

  
        myRentalsContainer.addMouseListener(hoverAdapter);
        myRentalsContainer.addMouseListener(clickAdapter);
        
        myRentalsButton.addMouseListener(hoverAdapter);
        myRentalsButton.addMouseListener(clickAdapter);
        
        overdueBadge.addMouseListener(hoverAdapter);
        overdueBadge.addMouseListener(clickAdapter);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
   
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
    
        JLabel systemTitle = new JLabel("Costume Rental Management System");
        systemTitle.setFont(new Font("Arial", Font.BOLD, 18));
        systemTitle.setForeground(new Color(70, 130, 180));
        

        JPanel headerInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerInfoPanel.setBackground(new Color(245, 245, 245));
        headerInfoPanel.add(systemTitle);
        
    
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timePanel.setBackground(new Color(245, 245, 245));
        timePanel.add(timeLabel);
        
        headerPanel.add(headerInfoPanel, BorderLayout.WEST);
        headerPanel.add(timePanel, BorderLayout.EAST);
        
    
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        welcomePanel.add(welcomeLabel);
        
      
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));
        
  
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        myRentalsContainer.setAlignmentX(Component.CENTER_ALIGNMENT); 
        accountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(searchButton);
        menuPanel.add(Box.createVerticalStrut(20)); 
        menuPanel.add(myRentalsContainer);
        menuPanel.add(Box.createVerticalStrut(20)); 
        menuPanel.add(accountButton);
        menuPanel.add(Box.createVerticalStrut(25));
        menuPanel.add(logoutButton);
        menuPanel.add(Box.createVerticalStrut(10));
        
        JPanel statsPanel = createStatsPanel();
      
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
     
        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statsLabel.setForeground(Color.GRAY);
        updateStatsLabel();
        
        statsPanel.add(statsLabel);
        return statsPanel;
    }
    
    private void setupEventListeners() {
        searchButton.addActionListener(e -> openCostumeSearch());
 
        
        accountButton.addActionListener(e -> openAccountSettings());
        
        logoutButton.addActionListener(e -> handleLogout());
    }
    
    private void setupFrame() {
        setTitle("Costume Rental System - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(580, 600); 
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(520, 450)); 
    }
    
    private void startClock() {
        clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimeLabel();
                updateStatsLabel(); 
            }
        });
        clockTimer.start();
    }
    
    private void updateTimeLabel() {
        LocalDateTime now = LocalDateTime.now();
        String timeText = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        timeLabel.setText(timeText);
    }
    
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
    
    public void checkOverdueRentals() {
        try {
            int overdueRentals = getOverdueRentalsForCurrentUser();
            updateOverdueBadge(overdueRentals);
        } catch (Exception e) {
            System.err.println("Error checking overdue rentals: " + e.getMessage());
        }
    }
  
    private int getOverdueRentalsForCurrentUser() {
        try {
            RentalService rentalService = new RentalService();
            rentalService.updateAllRentalStatuses(); 
            
            List<Rental> userRentals = rentalService.getActiveRentalsByMemberId(currentMemberId);
            return (int) userRentals.stream()
                    .filter(rental -> rental.getStatus() == Rental.RentalStatus.OVERDUE)
                    .count();
        } catch (Exception e) {
            System.err.println("Error getting overdue rentals: " + e.getMessage());
            return 0;
        }
    }
   
    private void updateOverdueBadge(int overdueCount) {
        if (overdueCount > 0 && overdueCount != currentOverdueCount) {
            currentOverdueCount = overdueCount;
            overdueBadge.setText(String.valueOf(overdueCount));
            overdueBadge.setVisible(true);
  
            myRentalsContainer.setComponentZOrder(overdueBadge, 0);
          
            String tooltipText = overdueCount == 1 ? 
                "You have 1 overdue rental" : 
                "You have " + overdueCount + " overdue rentals";
            myRentalsButton.setToolTipText(tooltipText + " - Click to view details");
            myRentalsContainer.setToolTipText(tooltipText + " - Click to view details");
            
        } else if (overdueCount == 0 && currentOverdueCount != 0) {
            currentOverdueCount = 0;
            overdueBadge.setVisible(false);
            myRentalsButton.setToolTipText("View your current and past rentals");
            myRentalsContainer.setToolTipText("View your current and past rentals");
        }
     
        myRentalsContainer.revalidate();
        myRentalsContainer.repaint();
    }
  
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
    
    private void setupButtonStyle(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK); 
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
          
            rentalsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    checkOverdueRentals(); 
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
            new LoginFrame().setVisible(true);
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    @Override
    public void dispose() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        super.dispose();
    }
}