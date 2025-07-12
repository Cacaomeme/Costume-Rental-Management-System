package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

public class CostumeListFrame extends JFrame {

    private JTable costumeTable;
    private DefaultTableModel tableModel;
    private CostumeDataManager costumeManager;

    public CostumeListFrame() {
        this.costumeManager = new CostumeDataManager();
        setTitle("Costume & Stock Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        // Initialize table model
        String[] columnNames = {"ID", "Name", "Event", "Price", "Sizes & Stock", "Image Path"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        costumeTable = new JTable(tableModel);
        
        add(new JScrollPane(costumeTable), BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel();
        JButton addButton = new JButton("Add New Costume");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(refreshButton);
        add(controlPanel, BorderLayout.SOUTH);
        
        // Event listeners
        addButton.addActionListener(e -> addNewCostume());
        editButton.addActionListener(e -> editSelectedCostume());
        deleteButton.addActionListener(e -> deleteSelectedCostume());
        refreshButton.addActionListener(e -> loadCostumes());

        loadCostumes(); // Initial data load
    }

    private void loadCostumes() {
        tableModel.setRowCount(0); // Clear table
        List<Costume> costumes = costumeManager.loadCostumes();
        for (Costume costume : costumes) {
            // Convert size:stock map to a readable string
            String sizeStockStr = costume.getSizeStock().entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(", "));

            Object[] row = {
                costume.getCostumeId(),
                costume.getCostumeName(),
                costume.getEvent().getDisplayName(),
                String.format("%.2f", costume.getPrice()),
                sizeStockStr,
                costume.getImagePath()
            };
            tableModel.addRow(row);
        }
    }

    private void addNewCostume() {
        AddCostumeDialog dialog = new AddCostumeDialog(this, costumeManager);
        dialog.setVisible(true);
        loadCostumes(); // Reload data after dialog closes
    }

    private Costume findCostumeById(String costumeId) {
        return costumeManager.loadCostumes().stream()
            .filter(c -> c.getCostumeId().equals(costumeId))
            .findFirst()
            .orElse(null);
    }

    private void editSelectedCostume() {
        int selectedRow = costumeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a costume to edit.");
            return;
        }
        String costumeId = (String) tableModel.getValueAt(selectedRow, 0);
        
        Costume selectedCostume = findCostumeById(costumeId);

        if (selectedCostume != null) {
            EditCostumeDialog dialog = new EditCostumeDialog(this, selectedCostume, costumeManager);
            dialog.setVisible(true);
            loadCostumes(); // Reload data after dialog closes
        } else {
             JOptionPane.showMessageDialog(this, "Could not find the selected costume data.");
        }
    }

    private void deleteSelectedCostume() {
        int selectedRow = costumeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a costume to delete.");
            return;
        }
        String costumeId = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete costume ID: " + costumeId + "?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = costumeManager.deleteCostume(costumeId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Costume deleted successfully.");
                loadCostumes();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete costume.");
            }
        }
    }
}