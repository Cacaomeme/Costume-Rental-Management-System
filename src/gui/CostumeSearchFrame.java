package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List; 
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CostumeSearchFrame extends JFrame {
    private static final List<String> STANDARD_SIZE_ORDER = List.of("XS", "S", "M", "L", "XL", "XXL", "One Size");
    
    private String currentMemberId;
    private JTextField searchField;
    private JComboBox<CostumeEvent> eventComboBox;
    private JComboBox<String> sizeComboBox;
    private JTextField minPriceField;
    private JTextField maxPriceField;
    private JPanel costumeDisplayPanel;
    private JScrollPane scrollPane;
    
    private CostumeDataManager dataManager;
    private FileIO fileIO; 
    private List<Costume> allCostumes;
    private List<Costume> filteredCostumes;

    public CostumeSearchFrame(String memberId) {
        this.currentMemberId = memberId;
        this.fileIO = FileIO.getInstance(); 
        initializeComponents();
        loadCostumeData();
        setupLayout();
        setupEventListeners();
        displayAllCostumes();
    }
    
    private Image getHighQualityScaledImage(Image originalImage, int targetWidth, int targetHeight) {
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(
            targetWidth, targetHeight, java.awt.image.BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = bufferedImage.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        
        return bufferedImage;
    }

    private void initializeComponents() {
        setTitle("Costume Search - Rental Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        searchField = new JTextField(20);
        
        eventComboBox = new JComboBox<>();
        eventComboBox.addItem(null); 
        for (CostumeEvent event : CostumeEvent.values()) {
            eventComboBox.addItem(event);
        }
        
        List<String> sizeOptions = new ArrayList<>();
        sizeOptions.add("All");
        sizeOptions.addAll(STANDARD_SIZE_ORDER);
        sizeComboBox = new JComboBox<>(sizeOptions.toArray(new String[0]));
        

        minPriceField = new JTextField(8);
        maxPriceField = new JTextField(8);
        

        costumeDisplayPanel = new JPanel();
        costumeDisplayPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 3列
        costumeDisplayPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        scrollPane = new JScrollPane(costumeDisplayPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
           
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);
        
        costumeDisplayPanel.setDoubleBuffered(true);
        
        dataManager = new CostumeDataManager();
        allCostumes = new ArrayList<>();
        filteredCostumes = new ArrayList<>();
    }

    private void loadCostumeData() {
        try {
            allCostumes = dataManager.loadCostumes();
            filteredCostumes = new ArrayList<>(allCostumes);
            System.out.println("Loaded " + allCostumes.size() + " costumes");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading costume data: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
   
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        
   
        add(scrollPane, BorderLayout.CENTER);
        
 
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Search & Filter", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font(Font.SANS_SERIF, Font.BOLD, 14)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        
        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
        
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Costume Name:");
        nameLabel.setFont(labelFont);
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(searchField, gbc);

        
        gbc.gridx = 2; gbc.gridy = 0;
        JLabel eventLabel = new JLabel("Event:");
        eventLabel.setFont(labelFont);
        panel.add(eventLabel, gbc);
        gbc.gridx = 3;
        panel.add(eventComboBox, gbc);

  
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel sizeLabel = new JLabel("Size:");
        sizeLabel.setFont(labelFont);
        panel.add(sizeLabel, gbc);
        gbc.gridx = 1;
        panel.add(sizeComboBox, gbc);

  
        gbc.gridx = 2; gbc.gridy = 1;
        JLabel priceLabel = new JLabel("Price Range:");
        priceLabel.setFont(labelFont);
        panel.add(priceLabel, gbc);
        
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pricePanel.add(minPriceField);
        JLabel dashLabel = new JLabel(" - ");
        dashLabel.setFont(labelFont);
        pricePanel.add(dashLabel);
        pricePanel.add(maxPriceField);
        JLabel dollarLabel = new JLabel(" $");
        dollarLabel.setFont(labelFont);
        pricePanel.add(dollarLabel);
        
        gbc.gridx = 3;
        panel.add(pricePanel, gbc);

  
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.BLACK);
        searchButton.setFocusPainted(false);
        searchButton.setPreferredSize(new Dimension(90, 40)); 
        searchButton.addActionListener(e -> performSearch());
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 2;
        panel.add(searchButton, gbc);

  
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        resetButton.setBackground(new Color(220, 20, 60));
        resetButton.setForeground(Color.BLACK);
        resetButton.setFocusPainted(false);
        resetButton.setPreferredSize(new Dimension(90, 40));
        resetButton.addActionListener(e -> resetFilters());
        gbc.gridx = 5;
        panel.add(resetButton, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton backButton = new JButton("Back to Main");
        backButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        backButton.setBackground(new Color(128, 128, 128));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createRaisedBevelBorder());
        backButton.setPreferredSize(new Dimension(140, 40)); // Back to Main
        backButton.addActionListener(e -> {
            dispose(); 
           
        });
        
        panel.add(backButton);
        return panel;
    }

    private void setupEventListeners() {
       
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
        });
        
    
        eventComboBox.addActionListener(e -> performSearch());
        sizeComboBox.addActionListener(e -> performSearch());
    }


    private int getAvailableStock(String costumeId, String size) {
        int maxStock = fileIO.getCostumeStock(costumeId, size);
        if (maxStock <= 0) {
            return 0;
        }
        
        
        java.time.LocalDate today = java.time.LocalDate.now();
        Map<java.time.LocalDate, Integer> reservationCounts = fileIO.getReservationCounts(costumeId, size);
        int reservedToday = reservationCounts.getOrDefault(today, 0);
        
        return Math.max(0, maxStock - reservedToday);
    }
    

    private boolean hasAvailableStock(Costume costume) {
        for (String size : costume.getAvailableSizes()) {
            int availableStock = getAvailableStock(costume.getCostumeId(), size);
            if (availableStock > 0) {
                return true;
            }
        }
        return false;
    }
    

    public void refreshStock() {
        SwingUtilities.invokeLater(() -> {
            performSearch(); 
        });
    }

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        CostumeEvent selectedEvent = (CostumeEvent) eventComboBox.getSelectedItem();
        String selectedSize = (String) sizeComboBox.getSelectedItem();
      
        double minPrice = 0;
        double maxPrice = Double.MAX_VALUE;
        
        try {
            if (!minPriceField.getText().trim().isEmpty()) {
                minPrice = Double.parseDouble(minPriceField.getText().trim());
            }
            if (!maxPriceField.getText().trim().isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceField.getText().trim());
            }
        } catch (NumberFormatException e) {
            
        }

      
        filteredCostumes.clear();
        for (Costume costume : allCostumes) {
            boolean matches = true;
            
     
            if (!searchText.isEmpty()) {
                matches &= costume.getCostumeName().toLowerCase().contains(searchText);
            }
            

            if (selectedEvent != null) {
                matches &= costume.getEvent() == selectedEvent;
            }
  
            if (!"All".equals(selectedSize)) {
                matches &= costume.getSize().equals(selectedSize);
            }
            
           
            matches &= costume.getPrice() >= minPrice && costume.getPrice() <= maxPrice;
            
           
            matches &= hasAvailableStock(costume);
       
            
            if (matches) {
                filteredCostumes.add(costume);
            }
        }
        
        displayCostumes(filteredCostumes);
    }

    private void resetFilters() {
        searchField.setText("");
        eventComboBox.setSelectedIndex(0);
        sizeComboBox.setSelectedIndex(0);
        minPriceField.setText("");
        maxPriceField.setText("");
        displayAllCostumes();
    }

    private void displayAllCostumes() {
        filteredCostumes = new ArrayList<>(allCostumes);
        displayCostumes(filteredCostumes);
    }

    private void displayCostumes(List<Costume> costumes) {
        costumeDisplayPanel.removeAll();
        
        if (costumes.isEmpty()) {
            JLabel noResultsLabel = new JLabel("No costumes found matching your criteria", JLabel.CENTER);
            noResultsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
            noResultsLabel.setForeground(Color.GRAY);
            costumeDisplayPanel.add(noResultsLabel);
        } else {
            for (Costume costume : costumes) {
                JPanel costumeCard = createCostumeCard(costume);
                costumeDisplayPanel.add(costumeCard);
            }
        }
        
        costumeDisplayPanel.revalidate();
        costumeDisplayPanel.repaint();
        
       
        setTitle("Costume Search - " + costumes.size() + " results");
    }

    private JPanel createCostumeCard(Costume costume) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createRaisedBevelBorder());
        card.setPreferredSize(new Dimension(250, 300));

    
        JPanel imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(200, 150));
        imagePanel.setBackground(Color.LIGHT_GRAY);
        imagePanel.setBorder(BorderFactory.createLoweredBevelBorder());
        
    
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/" + costume.getImagePath()));
            if (icon.getIconWidth() > 0) {
               
                Image img = getHighQualityScaledImage(icon.getImage(), 200, 150);
                JLabel imageLabel = new JLabel(new ImageIcon(img));
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                imagePanel.removeAll();
                imagePanel.add(imageLabel, BorderLayout.CENTER);
            } else {
                JLabel placeholderLabel = new JLabel("No Image", JLabel.CENTER);
                placeholderLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                imagePanel.add(placeholderLabel);
            }
        } catch (Exception e) {
            JLabel placeholderLabel = new JLabel("No Image", JLabel.CENTER);
            placeholderLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            imagePanel.add(placeholderLabel);
        }

        
        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel nameLabel = new JLabel("<html><b>" + costume.getCostumeName() + "</b></html>");
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        JLabel eventLabel = new JLabel("Event: " + costume.getEventDisplayName());
        eventLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        JLabel priceLabel = new JLabel("Price: $" + String.format("%.1f", costume.getPrice()));
        priceLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        priceLabel.setForeground(new Color(0, 128, 0)); 
        
     
        StringBuilder sizeStockInfo = new StringBuilder();
        for (String size : STANDARD_SIZE_ORDER) {
            if (costume.getAvailableSizes().contains(size)) {
                int availableStock = getAvailableStock(costume.getCostumeId(), size);
                sizeStockInfo.append(size).append("(").append(availableStock).append("), ");
            }
        }
        if (sizeStockInfo.length() > 0) {
            sizeStockInfo.setLength(sizeStockInfo.length() - 2); 
        }
        JLabel sizeLabel = new JLabel("Available: " + sizeStockInfo.toString());
        sizeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
 
        
        infoPanel.add(nameLabel);
        infoPanel.add(eventLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(sizeLabel);


        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton rentButton = new JButton("Rent");
        
       
        rentButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        rentButton.setBackground(new Color(70, 130, 180));
        rentButton.setForeground(Color.BLACK);
        rentButton.setFocusPainted(false);
        rentButton.setBorder(BorderFactory.createRaisedBevelBorder());
        rentButton.setPreferredSize(new Dimension(100, 35)); 
        
        rentButton.addActionListener(e -> {
            try {
               
                RentalFrame rentalFrame = new RentalFrame(currentMemberId, costume);
                
              
                rentalFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                       
                        refreshStock();
                    }
                });
            
                
                rentalFrame.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error opening rental screen: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        buttonPanel.add(rentButton);

        card.add(imagePanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void showCostumeDetails(Costume costume) {
        String details = String.format(
            "Costume Details:\n\n" +
            "ID: %s\n" +
            "Name: %s\n" +
            "Event: %s\n" +
            "Price: $%.1f\n" +
            "Size: %s\n" +
            "Stock: %d\n" +
            "Image: %s",
            costume.getCostumeId(),
            costume.getCostumeName(),
            costume.getEventDisplayName(),
            costume.getPrice(),
            costume.getSize(),
            costume.getStock(),
            costume.getImagePath()
        );
        
        JOptionPane.showMessageDialog(this, details, "Costume Details", JOptionPane.INFORMATION_MESSAGE);
    }
}