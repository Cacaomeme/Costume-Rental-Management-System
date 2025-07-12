package gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EditCostumeDialog extends JDialog {
    private JTextField idField, nameField, priceField, imagePathField, sizeStockField;
    private JComboBox<CostumeEvent> eventComboBox;
    private Costume originalCostume;
    private CostumeDataManager costumeManager;

    public EditCostumeDialog(Frame owner, Costume costume, CostumeDataManager manager) {
        super(owner, "Edit Costume", true);
        this.originalCostume = costume;
        this.costumeManager = manager;

        setLayout(new GridLayout(7, 2, 10, 10));
        setSize(500, 400);
        setLocationRelativeTo(owner);

        // Initialize fields
        idField = new JTextField(costume.getCostumeId());
        idField.setEditable(false); // ID should not be changed
        nameField = new JTextField(costume.getCostumeName());
        priceField = new JTextField(String.valueOf(costume.getPrice()));
        imagePathField = new JTextField(costume.getImagePath());
        
        String sizeStockStr = costume.getSizeStock().entrySet().stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.joining(", "));
        sizeStockField = new JTextField(sizeStockStr);
        
        eventComboBox = new JComboBox<>(CostumeEvent.values());
        eventComboBox.setSelectedItem(costume.getEvent());

        // Add components
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

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveChanges());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);
    }

    private void saveChanges() {
        try {
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

            if (name.isEmpty() || sizeStock.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Size/Stock cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create a new costume object with updated details
            Costume updatedCostume = new Costume(originalCostume.getCostumeId(), name, event, price, imagePath);
            updatedCostume.setSizeStock(sizeStock);

            if (costumeManager.updateCostume(updatedCostume)) {
                JOptionPane.showMessageDialog(this, "Costume updated successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update costume.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}