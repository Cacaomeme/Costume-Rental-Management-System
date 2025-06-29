package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gui.FileIO;

public class RegistrationFrame extends JFrame {
    private JTextField nameField;
    private JTextField memberIdField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JButton registerButton;
    private JButton cancelButton;
    private LoginFrame parentFrame;
    
    public RegistrationFrame(LoginFrame parent) {
        this.parentFrame = parent;
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
    }
    
    private void initializeComponents() {
        // Input fields initialization
        nameField = new JTextField(20);
        memberIdField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        phoneField = new JTextField(20);
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        
        // Buttons
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");
        
        // Button styling
        registerButton.setBackground(new Color(60, 179, 113));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Tooltips for user guidance
        memberIdField.setToolTipText("Enter a unique member ID (alphanumeric, 4-20 characters)");
        passwordField.setToolTipText("Password must be at least 6 characters");
        confirmPasswordField.setToolTipText("Re-enter your password");
        emailField.setToolTipText("Enter a valid email address");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(245, 245, 245));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("New Member Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 130, 180));
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        // Member ID field
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel memberIdLabel = new JLabel("Member ID:");
        memberIdLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(memberIdLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(memberIdField, gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        // Phone field
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        // Confirm password field
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);
        
        // Address field
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(addressLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        addressScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formPanel.add(addressScrollPane, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to frame
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCancel();
            }
        });
        
        // Enter key support for registration
        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });
    }
    
    private void setupFrame() {
        setTitle("Costume Rental System - New Registration");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(parentFrame);
        setResizable(false);
        
        // Handle window closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                handleCancel();
            }
        });
    }
    
    private void handleRegistration() {
        // Get input values
        String name = nameField.getText().trim();
        String memberId = memberIdField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String address = addressArea.getText().trim();
        
        // Validation
        if (!validateInput(name, memberId, email, phone, password, confirmPassword, address)) {
            return;
        }
        
        // TODO: Check if member ID already exists
        if (isMemberIdExists(memberId)) {
            showErrorMessage("Member ID already exists. Please choose a different one.");
            memberIdField.requestFocus();
            return;
        }
        
        // TODO: Register new member using MemberService
        if (registerNewMember(name, memberId, email, phone, password, address)) {
            JOptionPane.showMessageDialog(this,
                "Registration successful!\nYou can now login with your Member ID: " + memberId,
                "Registration Complete",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Return to login screen
            returnToLogin();
        } else {
            showErrorMessage("Registration failed. Please try again.");
        }
    }
    
    private boolean validateInput(String name, String memberId, String email, String phone, 
                                String password, String confirmPassword, String address) {
        
        // Name validation
        if (name.isEmpty()) {
            showErrorMessage("Please enter your full name.");
            nameField.requestFocus();
            return false;
        }
        
        // Member ID validation
        if (memberId.isEmpty()) {
            showErrorMessage("Please enter a Member ID.");
            memberIdField.requestFocus();
            return false;
        }
        
        if (memberId.length() < 4 || memberId.length() > 20) {
            showErrorMessage("Member ID must be between 4 and 20 characters.");
            memberIdField.requestFocus();
            return false;
        }
        
        if (!memberId.matches("^[a-zA-Z0-9]+$")) {
            showErrorMessage("Member ID can only contain letters and numbers.");
            memberIdField.requestFocus();
            return false;
        }
        
        // Email validation
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
        
        // Phone validation
        if (phone.isEmpty()) {
            showErrorMessage("Please enter your phone number.");
            phoneField.requestFocus();
            return false;
        }
        
        // Password validation
        if (password.isEmpty()) {
            showErrorMessage("Please enter a password.");
            passwordField.requestFocus();
            return false;
        }
        
        if (password.length() < 6) {
            showErrorMessage("Password must be at least 6 characters long.");
            passwordField.requestFocus();
            return false;
        }
        
        // Confirm password validation
        if (!password.equals(confirmPassword)) {
            showErrorMessage("Passwords do not match.");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        // Address validation
        if (address.isEmpty()) {
            showErrorMessage("Please enter your address.");
            addressArea.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean isValidEmail(String email) {
        // Simple email validation pattern
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }
    
    private boolean isMemberIdExists(String memberId) {
        // TODO: Implement actual database check
        FileIO checkMemberId = new FileIO();
        return checkMemberId.isMemberIdExists(memberId);
    }
    
    private boolean registerNewMember(String name, String memberId, String email, 
                                    String phone, String password, String address) {
        // TODO: Implement actual member registration using MemberService
        // This would involve saving to database/file
        
        // Temporary implementation - always return true for testing
        System.out.println("Registering new member:");
        System.out.println("Name: " + name);
        System.out.println("Member ID: " + memberId);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
        System.out.println("Address: " + address);

        FileIO writeData = new FileIO();
        writeData.Write(name, memberId, email, phone, password, address);
        System.out.println("Member registered successfully.");
        
        return true;
    }
    
    private void handleCancel() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel registration?\nAll entered data will be lost.",
            "Cancel Registration",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            returnToLogin();
        }
    }
    
    private void returnToLogin() {
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        }
        this.dispose();
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Registration Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Method to clear all fields (useful for testing)
    private void clearAllFields() {
        nameField.setText("");
        memberIdField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        addressArea.setText("");
        nameField.requestFocus();
    }
}