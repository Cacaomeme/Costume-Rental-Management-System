package gui;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField memberIdField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;
    
    public LoginFrame() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
    }
    
    private void initializeComponents() {
        // Input fields
        memberIdField = new JTextField(20);
        passwordField = new JPasswordField(20);
        passwordField.setEchoChar('●');
        
        // Password visibility checkbox
        showPasswordCheckBox = new JCheckBox("Show password");
        showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
        showPasswordCheckBox.setFocusable(false);
        
        // Buttons
        loginButton = new JButton("Login");
        registerButton = new JButton("Register New Member");
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Button styling
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(150, 40));
        
        registerButton.setBackground(new Color(60, 179, 113));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFont(new Font("Arial", Font.BOLD, 12));
        registerButton.setPreferredSize(new Dimension(180, 35));
        
        // Tooltips
        memberIdField.setToolTipText("Enter your Member ID");
        passwordField.setToolTipText("Enter your password");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(245, 245, 245));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Costume Rental Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        
        // Login panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Member Login",
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                new Color(70, 130, 180)
            ),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Member ID field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel memberIdLabel = new JLabel("Member ID:");
        memberIdLabel.setFont(new Font("Arial", Font.BOLD, 12));
        loginPanel.add(memberIdLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(memberIdField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        loginPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(passwordField, gbc);
        
        // Show password checkbox
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 15, 10);
        loginPanel.add(showPasswordCheckBox, gbc);
        
        // Status label
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 10, 10, 10);
        loginPanel.add(statusLabel, gbc);
        
        // Login button
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        loginPanel.add(loginButton, gbc);
        
        // Registration panel
        JPanel registrationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registrationPanel.setBackground(Color.WHITE);
        registrationPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
        
        JLabel registerLabel = new JLabel("Don't have an account?");
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        registrationPanel.add(registerLabel);
        registrationPanel.add(Box.createHorizontalStrut(10));
        registrationPanel.add(registerButton);
        
        // Add panels to frame
        add(titlePanel, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);
        add(registrationPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // Password visibility toggle
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());
        
        // Login button
        loginButton.addActionListener(e -> handleLogin());
        
        // Register button
        registerButton.addActionListener(e -> openRegistrationFrame());
        
        // Enter key support
        passwordField.addActionListener(e -> handleLogin());
        memberIdField.addActionListener(e -> passwordField.requestFocus());
    }
    
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0); // Show password
        } else {
            passwordField.setEchoChar('●'); // Hide password
        }
    }
    
    private void setupFrame() {
        setTitle("Costume Rental System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 420);
        setLocationRelativeTo(null);
        setResizable(false);
        
        SwingUtilities.invokeLater(() -> memberIdField.requestFocus());
    }
    
    private void handleLogin() {
        String memberId = memberIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        statusLabel.setText(" ");
        
        // Validation
        if (memberId.isEmpty()) {
            showStatus("Please enter your Member ID.", Color.RED);
            memberIdField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showStatus("Please enter your password.", Color.RED);
            passwordField.requestFocus();
            return;
        }
        
        // Authentication using FileIO
        try {
            FileIO fileIO = FileIO.getInstance();
            if (fileIO.isValidLogin(memberId, password)) {
                showStatus("Login successful!", Color.GREEN);
                
                SwingUtilities.invokeLater(() -> {
                    MainFrame mainFrame = new MainFrame(memberId);
                    mainFrame.setVisible(true);
                    this.dispose();
                });
            } else {
                showStatus("Invalid Member ID or password.", Color.RED);
                passwordField.setText("");
                passwordField.requestFocus();
            }
        } catch (Exception e) {
            showStatus("Authentication system error. Please try again.", Color.RED);
            System.err.println("Login error: " + e.getMessage());
        }
    }
    
    private void openRegistrationFrame() {
        RegistrationFrame registrationFrame = new RegistrationFrame(this);
        registrationFrame.setVisible(true);
        this.setVisible(false);
    }
    
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
        
        if (color == Color.GREEN) {
            Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    public void clearFields() {
        memberIdField.setText("");
        passwordField.setText("");
        showPasswordCheckBox.setSelected(false);
        togglePasswordVisibility();
        statusLabel.setText(" ");
        memberIdField.requestFocus();
    }
}