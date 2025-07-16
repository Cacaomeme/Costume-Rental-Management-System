package gui;

import java.awt.*;
import javax.swing.*;

public class AccountSettingsFrame extends JFrame {
    private String currentMemberId;
    private FileIO fileIO;
    private FileIO.MemberData currentMemberData;
    private MainFrame parentMainFrame;

    // UI Components
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JLabel memberIdLabel;
    private JLabel registrationDateLabel;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton updateInfoButton;
    private JButton changePasswordButton;
    private JButton backButton;
    private JTabbedPane tabbedPane;

    public AccountSettingsFrame(String memberId, MainFrame parentFrame) {
        this.currentMemberId = memberId;
        this.parentMainFrame = parentFrame;
        this.fileIO = FileIO.getInstance();

        loadMemberData();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
    }

    private void loadMemberData() {
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
        nameField = new JTextField(30);
        emailField = new JTextField(30);
        phoneField = new JTextField(30);
        addressArea = new JTextArea(4, 30);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        memberIdLabel = new JLabel();
        registrationDateLabel = new JLabel();

        currentPasswordField = new JPasswordField(30);
        newPasswordField = new JPasswordField(30);
        confirmPasswordField = new JPasswordField(30);

        updateInfoButton = new JButton("Update Information");
        changePasswordButton = new JButton("Change Password");
        backButton = new JButton("Back to Main");

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        populateFields();
    }

    private void populateFields() {
        if (currentMemberData != null) {
            nameField.setText(currentMemberData.getName());
            emailField.setText(currentMemberData.getEmail());
            phoneField.setText(currentMemberData.getPhone());
            addressArea.setText(currentMemberData.getAddress());

            memberIdLabel.setText("Member ID: " + currentMemberData.getMemberId());
            registrationDateLabel.setText("Registration Date: (Not implemented)");
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Account Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);

        JPanel infoTab = createInfoTab();
        JPanel passwordTab = createPasswordTab();
        
        tabbedPane.addTab("Personal Information", infoTab);
        tabbedPane.addTab("Change Password", passwordTab);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.add(backButton);

        add(titlePanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInfoTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(memberIdLabel, gbc);
        gbc.gridy = 1;
        panel.add(registrationDateLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.weightx = 1.0;                
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;      
        gbc.weightx = 0.0;
        panel.add(new JLabel("Email Address:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.weightx = 1.0;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;  
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;                    
        panel.add(new JScrollPane(addressArea), gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;      
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(updateInfoButton, gbc);
        
        return panel;
    }

    private JPanel createPasswordTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(new JLabel("Enter your current password and new password:"), gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.weightx = 1.0;                  
        panel.add(currentPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;      
        gbc.weightx = 0.0;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(newPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Confirm New Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(confirmPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(changePasswordButton, gbc);

        return panel;
    }

    private void setupEventListeners() {
        updateInfoButton.addActionListener(e -> handleUpdateInfo());
        changePasswordButton.addActionListener(e -> handleChangePassword());
        backButton.addActionListener(e -> handleBack());
    }

    private void setupFrame() {
        setTitle("Costume Rental System - Account Settings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void handleUpdateInfo() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            showErrorMessage("All fields must be filled.");
            return;
        }

        FileIO.MemberData updatedData = new FileIO.MemberData(
            name, currentMemberId, email, phone, currentMemberData.getPassword(), address
        );

        if (fileIO.updateMember(currentMemberId, updatedData)) {
            JOptionPane.showMessageDialog(this, "Information updated successfully!");
            loadMemberData();
            populateFields();
        } else {
            showErrorMessage("Failed to update information.");
        }
    }

    private void handleChangePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showErrorMessage("All password fields must be filled.");
            return;
        }
        if (!currentMemberData.getPassword().equals(currentPassword)) {
            showErrorMessage("Current password is incorrect.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            showErrorMessage("New passwords do not match.");
            return;
        }

        FileIO.MemberData updatedData = new FileIO.MemberData(
            currentMemberData.getName(), currentMemberId, currentMemberData.getEmail(),
            currentMemberData.getPhone(), newPassword, currentMemberData.getAddress()
        );

        if (fileIO.updateMember(currentMemberId, updatedData)) {
            JOptionPane.showMessageDialog(this, "Password changed successfully!");
            currentMemberData = fileIO.getMemberData(currentMemberId);
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        } else {
            showErrorMessage("Failed to change password.");
        }
    }

    private void handleBack() {
        this.dispose();
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}