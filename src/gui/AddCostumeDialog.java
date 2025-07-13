package gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class AddCostumeDialog extends JDialog {
    private JTextField idField, nameField, priceField, imagePathField, sizeStockField;
    private JComboBox<CostumeEvent> eventComboBox;
    private CostumeDataManager costumeManager;

    public AddCostumeDialog(Frame owner, CostumeDataManager manager) {
        super(owner, "Add New Costume", true);
        this.costumeManager = manager;

        setLayout(new GridLayout(7, 2, 10, 10));
        setSize(500, 400);
        setLocationRelativeTo(owner);

        // Initialize fields
        idField = new JTextField();
        nameField = new JTextField();
        priceField = new JTextField();
        imagePathField = new JTextField("images/placeholder.jpg");
        sizeStockField = new JTextField("S:1,M:1,L:1");
        eventComboBox = new JComboBox<>(CostumeEvent.values());

        // Add components to dialog
        add(new JLabel("Costume ID:"));
        add(idField);
        add(new JLabel("Costume Name:"));
        add(nameField);
        add(new JLabel("Event:"));
        add(eventComboBox);
        add(new JLabel("Price:"));
        add(priceField);
        add(new JLabel("Image Path:"));
        add(imagePathField);
        add(new JLabel("Sizes & Stock (e.g., S:5,M:10):"));
        add(sizeStockField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveCostume());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);
    }

    private void saveCostume() {
        try {
            String id = idField.getText();
            String name = nameField.getText();
            CostumeEvent event = (CostumeEvent) eventComboBox.getSelectedItem();
            double price = Double.parseDouble(priceField.getText());
            String imagePath = imagePathField.getText();
            
            // Parse size:stock map
            Map<String, Integer> sizeStock = new HashMap<>();
            String[] pairs = sizeStockField.getText().split(",");
            for (String pair : pairs) {
                String[] parts = pair.split(":");
                if (parts.length == 2) {
                    sizeStock.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                }
            }
            
            if (id.isEmpty() || name.isEmpty() || sizeStock.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID, Name, and Size/Stock cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Costume newCostume = new Costume(id, name, event, price, imagePath);
            newCostume.setSizeStock(sizeStock);

            if (costumeManager.addCostume(newCostume)) {
                JOptionPane.showMessageDialog(this, "Costume added successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add costume. Check if ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}