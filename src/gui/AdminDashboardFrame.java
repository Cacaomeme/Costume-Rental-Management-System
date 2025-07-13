package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboardFrame extends JFrame {

    private JLabel statsLabel;
    private FileIO fileIO;

    public AdminDashboardFrame() {
        this.fileIO = FileIO.getInstance();
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main panel (for buttons)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1, 20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Management buttons
        JButton memberButton = new JButton("Member Management");
        memberButton.addActionListener(e -> new MemberListFrame().setVisible(true));
        setupButtonStyle(memberButton);
        mainPanel.add(memberButton);

        JButton costumeButton = new JButton("Costume & Stock Management");
        costumeButton.addActionListener(e -> new CostumeListFrame().setVisible(true));
        setupButtonStyle(costumeButton);
        mainPanel.add(costumeButton);

        JButton rentalButton = new JButton("Rental Management");
        rentalButton.addActionListener(e -> new AllRentalsFrame().setVisible(true));
        setupButtonStyle(rentalButton);
        mainPanel.add(rentalButton);
        
        add(mainPanel, BorderLayout.CENTER);

        // Footer (for stats and logout)
        JPanel footerPanel = new JPanel(new BorderLayout(10, 10));
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        updateStats();
        footerPanel.add(statsLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        footerPanel.add(logoutButton, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private void setupButtonStyle(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void updateStats() {
        int memberCount = fileIO.getRegistrationCount();
        int costumeCount = fileIO.getAvailableCostumesCount(); // This method might need to be created in FileIO
        int activeRentals = fileIO.getActiveRentalsCount(); // This method might need to be created in FileIO
        
        statsLabel.setText(String.format(
            "<html><b>System Status:</b><br>" +
            "&nbsp;  - Total Members: %d<br>" +
            "&nbsp;  - Total Costumes: %d<br>" +
            "&nbsp;  - Currently Rented: %d items</html>",
            memberCount, costumeCount, activeRentals
        ));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboardFrame().setVisible(true));
    }
}