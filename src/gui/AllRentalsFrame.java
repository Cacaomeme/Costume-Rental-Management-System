package gui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AllRentalsFrame extends JFrame {

    private JTable rentalTable;
    private DefaultTableModel tableModel;
    private RentalService rentalService;

    public AllRentalsFrame() {
        this.rentalService = new RentalService();
        setTitle("All Rental Records");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize table model
        String[] columnNames = {"Rental ID", "Member ID", "Costume ID", "Size", "Rental Date", "Return Date", "Actual Return", "Status", "Total Cost", "Late Fee"};
        tableModel = new DefaultTableModel(columnNames, 0) {
             @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        rentalTable = new JTable(tableModel);
        
        add(new JScrollPane(rentalTable), BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        controlPanel.add(refreshButton);
        add(controlPanel, BorderLayout.SOUTH);
        
        // Event listener
        refreshButton.addActionListener(e -> loadRentals());

        loadRentals(); // Initial data load
    }

    private void loadRentals() {
        tableModel.setRowCount(0); // Clear table
        rentalService.updateAllRentalStatuses(); // Update statuses before loading
        List<Rental> rentals = rentalService.getAllRentals();
        
        for (Rental rental : rentals) {
            Object[] row = {
                rental.getRentalId(),
                rental.getMemberId(),
                rental.getCostumeId(),
                rental.getSize(),
                rental.getFormattedRentalDate(),
                rental.getFormattedReturnDate(),
                rental.getFormattedActualReturnDate(),
                rental.getStatus().getDisplayName(),
                String.format("%.2f", rental.getTotalCost()),
                String.format("%.2f", rental.getLateFee())
            };
            tableModel.addRow(row);
        }
    }
}