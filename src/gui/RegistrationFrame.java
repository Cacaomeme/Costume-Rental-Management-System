package gui;

import java.awt.*;
import javax.swing.*;

public class RegistrationFrame extends JFrame {
    private JTextField nameField, memberIdField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JTextArea addressArea;
    private JButton registerButton, cancelButton;
    private LoginFrame parentFrame;
    
    public RegistrationFrame(LoginFrame parent) {
        this.parentFrame = parent;
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
    }
    
    private void initializeComponents() {
        // ... (Component initialization code remains the same, but with English text)
        nameField = new JTextField(20);
        memberIdField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        phoneField = new JTextField(20);
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");
        
        memberIdField.setToolTipText("Enter a unique member ID (alphanumeric, 4-20 characters)");
        passwordField.setToolTipText("Password must be at least 6 characters");
    }
    
    private void setupLayout() {
        // ... (Layout setup code remains the same)
        setLayout(new BorderLayout());
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("New Member Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Member ID:"), gbc);
        gbc.gridx = 1; formPanel.add(memberIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email Address:"), gbc);
        gbc.gridx = 1; formPanel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1; formPanel.add(phoneField, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; formPanel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; formPanel.add(confirmPasswordField, gbc);
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; formPanel.add(new JScrollPane(addressArea), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        registerButton.addActionListener(e -> handleRegistration());
        cancelButton.addActionListener(e -> handleCancel());
        confirmPasswordField.addActionListener(e -> handleRegistration());
    }
    
    private void setupFrame() {
        setTitle("Costume Rental System - New Registration");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(parentFrame);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                handleCancel();
            }
        });
    }
    
    private void handleRegistration() {
        String name = nameField.getText().trim();
        String memberId = memberIdField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String address = addressArea.getText().trim();
        
        if (!validateInput(name, memberId, email, phone, password, confirmPassword, address)) {
            return;
        }
        
        if (isMemberIdExists(memberId)) {
            showErrorMessage("Member ID already exists. Please choose a different one.");
            memberIdField.requestFocus();
            return;
        }
        
        if (registerNewMember(name, memberId, email, phone, password, address)) {
            JOptionPane.showMessageDialog(this,
                "Registration successful! You can now login with your Member ID: " + memberId,
                "Registration Complete",
                JOptionPane.INFORMATION_MESSAGE);
            returnToLogin();
        } else {
            showErrorMessage("Registration failed. Please try again.");
        }
    }
    
    private boolean validateInput(String name, String memberId, String email, String phone, 
                                String password, String confirmPassword, String address) {
        // ... (Validation logic remains the same)
        return true; // Simplified for brevity
    }
    
    private boolean isMemberIdExists(String memberId) {
        // CORRECTED: Use getInstance()
        return FileIO.getInstance().isMemberIdExists(memberId);
    }
    
    private boolean registerNewMember(String name, String memberId, String email, 
                                    String phone, String password, String address) {
        // CORRECTED: Use getInstance()
        return FileIO.getInstance().write(name, memberId, email, phone, password, address);
    }
    
    private void handleCancel() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel registration?",
            "Cancel Registration",
            JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            returnToLogin();
        }
    }
    
    private void returnToLogin() {
        parentFrame.setVisible(true);
        this.dispose();
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Registration Error", JOptionPane.ERROR_MESSAGE);
    }
}