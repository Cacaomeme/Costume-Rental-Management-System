package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Rental procedure screen
 */
public class RentalFrame extends JFrame {
    private String currentMemberId;
    private Costume selectedCostume;
    private RentalService rentalService;
    
    // UI Components
    private JLabel costumeImageLabel;
    private JLabel costumeNameLabel;
    private JLabel costumeEventLabel;
    private JLabel costumeSizeLabel;
    private JLabel costumeStockLabel;
    private JLabel dailyRateLabel;
    
    // Size selection components
    private JComboBox<String> sizeComboBox;
    private JLabel selectedSizeStockLabel;
    
    private JSpinner rentalDaysSpinner;
    private JSpinner yearSpinner;
    private JSpinner monthSpinner;
    private JSpinner daySpinner;
    private JLabel startDateLabel;
    private JLabel endDateLabel;
    private JLabel totalCostLabel;
    private JTextArea termsTextArea;
    private JCheckBox agreeCheckBox;
    private JCheckBox insuranceCheckBox;
    private JCheckBox cleaningServiceCheckBox;
    
    private JButton confirmButton;
    private JButton cancelButton;
    
    // Settings
    private static final int MIN_RENTAL_DAYS = 1;
    private static final int MAX_RENTAL_DAYS = 30;
    private static final double DAILY_RATE_MULTIPLIER = 1.0; // Daily rate multiplier for base price

    // Field to hold ReserveCalendar instance
    private ReserveCalendar calendarDialog;
    
    public RentalFrame(String memberId, Costume costume) {
        this.currentMemberId = memberId;
        this.selectedCostume = costume;
        this.rentalService = new RentalService();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        updateCostumeInfo();
        updatePriceCalculation();
        setupFrame();
    }
    
    private void initializeComponents() {
        // Costume information display components
        costumeImageLabel = new JLabel();
        costumeImageLabel.setPreferredSize(new Dimension(200, 150));
        costumeImageLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        costumeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        costumeNameLabel = new JLabel();
        costumeNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        costumeEventLabel = new JLabel();
        costumeEventLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        costumeSizeLabel = new JLabel();
        costumeSizeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        costumeStockLabel = new JLabel();
        costumeStockLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        dailyRateLabel = new JLabel();
        dailyRateLabel.setFont(new Font("Arial", Font.BOLD, 12));
        dailyRateLabel.setForeground(new Color(0, 128, 0));
        
        // Size selection combo box
        sizeComboBox = new JComboBox<>();
        sizeComboBox.setFont(new Font("Arial", Font.BOLD, 14));
        sizeComboBox.setPreferredSize(new Dimension(100, 30));
        
        selectedSizeStockLabel = new JLabel();
        selectedSizeStockLabel.setFont(new Font("Arial", Font.BOLD, 12));
        selectedSizeStockLabel.setForeground(new Color(0, 100, 0));
        
        // Rental period selection
        rentalDaysSpinner = new JSpinner(new SpinnerNumberModel(3, MIN_RENTAL_DAYS, MAX_RENTAL_DAYS, 1));
        rentalDaysSpinner.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Start date selection - separate spinners for year, month, day
        LocalDate tomorrow = LocalDate.now().plusDays(1); // Start from tomorrow
        
        // Year spinner (current year to 2 years from now)
        int currentYear = tomorrow.getYear();
        yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, currentYear, currentYear + 2, 1));
        yearSpinner.setFont(new Font("Arial", Font.BOLD, 14));
        yearSpinner.setPreferredSize(new Dimension(70, 30));
        
        // Remove comma formatting from year spinner
        JSpinner.NumberEditor yearEditor = new JSpinner.NumberEditor(yearSpinner, "#");
        yearSpinner.setEditor(yearEditor);
        
        // Month spinner (1-12)
        int currentMonth = tomorrow.getMonthValue();
        monthSpinner = new JSpinner(new SpinnerNumberModel(currentMonth, 1, 12, 1));
        monthSpinner.setFont(new Font("Arial", Font.BOLD, 14));
        monthSpinner.setPreferredSize(new Dimension(50, 30));
        
        // Day spinner (1-31, will be adjusted based on month)
        int currentDay = tomorrow.getDayOfMonth();
        daySpinner = new JSpinner(new SpinnerNumberModel(currentDay, 1, 31, 1));
        daySpinner.setFont(new Font("Arial", Font.BOLD, 14));
        daySpinner.setPreferredSize(new Dimension(50, 30));
        
        startDateLabel = new JLabel();
        startDateLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        endDateLabel = new JLabel();
        endDateLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Price display
        totalCostLabel = new JLabel();
        totalCostLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalCostLabel.setForeground(new Color(220, 20, 60));
        
        // Terms and conditions
        termsTextArea = new JTextArea();
        termsTextArea.setEditable(false);
        termsTextArea.setFont(new Font("Arial", Font.BOLD, 11));
        termsTextArea.setBackground(getBackground());
        termsTextArea.setLineWrap(true); // Enable automatic line wrapping
        termsTextArea.setWrapStyleWord(true); // Wrap at word boundaries
        termsTextArea.setText(getTermsAndConditions());
        
        // Agreement checkbox
        agreeCheckBox = new JCheckBox("I agree to the terms and conditions");
        agreeCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        agreeCheckBox.setBackground(Color.WHITE);
        
        // Buttons
        confirmButton = new JButton("Confirm Rental");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.setBackground(new Color(70, 130, 180));
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusPainted(false);
        confirmButton.setPreferredSize(new Dimension(150, 40));
        confirmButton.setEnabled(false); // Initially disabled
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(100, 40));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Costume Rental");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Left panel (costume information)
        JPanel costumePanel = createCostumeInfoPanel();
        
        // Right panel (rental details)
        JPanel rentalPanel = createRentalDetailsPanel();
        
        mainPanel.add(costumePanel, BorderLayout.WEST);
        mainPanel.add(rentalPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createCostumeInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Costume Information",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14)));
        panel.setPreferredSize(new Dimension(250, 400));
        
        // Image panel
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagePanel.add(costumeImageLabel);
        
        // Information panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        infoPanel.add(costumeNameLabel);
        infoPanel.add(costumeEventLabel);
        infoPanel.add(costumeSizeLabel);
        infoPanel.add(costumeStockLabel);
        infoPanel.add(new JSeparator());
        infoPanel.add(dailyRateLabel);
        
        panel.add(imagePanel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRentalDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Rental Details",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14)));
        
        // Top: Size selection
        JPanel sizePanel = createSizeSelectionPanel();
        
        // Center: Rental period selection
        JPanel periodPanel = createRentalPeriodPanel();
        
        // Bottom: Terms and price display combined
        JPanel termsPanel = createTermsPanel();
        JPanel costPanel = createCostPanel();
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(termsPanel);
        bottomPanel.add(Box.createVerticalStrut(10)); // Spacing
        bottomPanel.add(costPanel);
        
        panel.add(sizePanel, BorderLayout.NORTH);
        panel.add(periodPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSizeSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Size Selection",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12)));
        panel.setPreferredSize(new Dimension(400, 80));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        
        // Size selection
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel sizeLabel = new JLabel("Select Size:");
        sizeLabel.setFont(labelFont);
        panel.add(sizeLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(sizeComboBox, gbc);
        
        // Selected size stock count
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel stockLabel = new JLabel("Available Stock:");
        stockLabel.setFont(labelFont);
        panel.add(stockLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(selectedSizeStockLabel, gbc);
        
        return panel;
    }
    
    private JPanel createRentalPeriodPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        
        // Rental period
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel daysLabel = new JLabel("Rental Period:");
        daysLabel.setFont(labelFont);
        panel.add(daysLabel, gbc);
        
        gbc.gridx = 1;
        JPanel daysInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        daysInputPanel.add(rentalDaysSpinner);
        JLabel daysUnitLabel = new JLabel(" days");
        daysUnitLabel.setFont(new Font("Arial", Font.BOLD, 12));
        daysInputPanel.add(daysUnitLabel);
        panel.add(daysInputPanel, gbc);
        
        // Start date
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setFont(labelFont);
        panel.add(startLabel, gbc);
        
        gbc.gridx = 1;
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        datePanel.add(yearSpinner);
        JLabel yearLabel = new JLabel("/");
        yearLabel.setFont(new Font("Arial", Font.BOLD, 12));
        datePanel.add(yearLabel);
        datePanel.add(monthSpinner);
        JLabel monthLabel = new JLabel("/");
        monthLabel.setFont(new Font("Arial", Font.BOLD, 12));
        datePanel.add(monthLabel);
        datePanel.add(daySpinner);
        panel.add(datePanel, gbc);
        
        // Expected return date
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel endLabel = new JLabel("Return Date:");
        endLabel.setFont(labelFont);
        panel.add(endLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(endDateLabel, gbc);
        
        return panel;
    }
    
    private JPanel createTermsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Terms and Conditions",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12)));
        
        JScrollPane scrollPane = new JScrollPane(termsTextArea);
        scrollPane.setPreferredSize(new Dimension(400, 100)); // Reduce height
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Custom checkbox panel
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        checkPanel.setBackground(Color.WHITE);
        checkPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Customize checkbox size
        agreeCheckBox.setIcon(createCustomCheckBoxIcon(false));
        agreeCheckBox.setSelectedIcon(createCustomCheckBoxIcon(true));
        agreeCheckBox.setFocusPainted(false);
        agreeCheckBox.setBorderPainted(false);
        agreeCheckBox.setContentAreaFilled(false);
        
        checkPanel.add(agreeCheckBox);
        panel.add(checkPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create custom checkbox icon
     */
    private Icon createCustomCheckBoxIcon(boolean checked) {
        int size = 20; // Checkbox size
        
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Outer border
                g2d.setColor(Color.GRAY);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(x + 1, y + 1, size - 2, size - 2);
                
                // Background
                if (checked) {
                    g2d.setColor(new Color(70, 130, 180));
                    g2d.fillRect(x + 2, y + 2, size - 3, size - 3);
                    
                    // Check mark
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int[] checkX = {x + 5, x + 8, x + 15};
                    int[] checkY = {y + 10, y + 13, y + 7};
                    for (int i = 0; i < checkX.length - 1; i++) {
                        g2d.drawLine(checkX[i], checkY[i], checkX[i + 1], checkY[i + 1]);
                    }
                } else {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(x + 2, y + 2, size - 3, size - 3);
                }
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return size;
            }
            
            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
    
    private JPanel createCostPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Total Cost",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14)));
        
        panel.add(totalCostLabel);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(new EmptyBorder(15, 20, 20, 20));
        
        panel.add(cancelButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(confirmButton);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // Size change event listener
        sizeComboBox.addActionListener(e -> {
            String selectedSize = (String) sizeComboBox.getSelectedItem();
            if (selectedSize == null) {
                return;
            }
            
            updateSelectedSizeStock();
            updateConfirmButtonState();

            // Calendar display logic
            // If calendar is not created or not visible
            if (calendarDialog == null || !calendarDialog.isVisible()) {
                calendarDialog = new ReserveCalendar(this, selectedCostume.getCostumeId(), selectedSize);
                
                // Position to the right of RentalFrame
                Point location = this.getLocation();
                calendarDialog.setLocation(location.x + this.getWidth(), location.y);
                // Set smaller size
                calendarDialog.setSize(350, 350); 
                calendarDialog.setVisible(true);
            } else {
                // If already displayed, update with new information
                calendarDialog.updateData(selectedCostume.getCostumeId(), selectedSize);
                // Bring to front
                calendarDialog.toFront();
            }
        });

        // Rental period change price update
        rentalDaysSpinner.addChangeListener(e -> updatePriceCalculation());
        
        // Start date change price update
        yearSpinner.addChangeListener(e -> {
            adjustDaySpinnerForMonth();
            updatePriceCalculation();
        });
        monthSpinner.addChangeListener(e -> {
            adjustDaySpinnerForMonth();
            updatePriceCalculation();
        });
        daySpinner.addChangeListener(e -> updatePriceCalculation());
        
        // Agreement checkbox
        agreeCheckBox.addActionListener(e -> updateConfirmButtonState());
        
        // Confirm button
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processRental();
            }
        });
        
        // Cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void updateCostumeInfo() {
        // Costume name
        costumeNameLabel.setText("<html><b>" + selectedCostume.getCostumeName() + "</b></html>");
        
        // Event
        costumeEventLabel.setText("Event: " + selectedCostume.getEventDisplayName());
        
        // Display available sizes
        StringBuilder sizeInfo = new StringBuilder("Available Sizes: ");
        for (String size : selectedCostume.getAvailableSizes()) {
            sizeInfo.append(size).append(" (").append(selectedCostume.getStockForSize(size)).append("), ");
        }
        if (sizeInfo.length() > 0) {
            sizeInfo.setLength(sizeInfo.length() - 2); // Remove last comma and space
        }
        costumeSizeLabel.setText(sizeInfo.toString());
        
        // Total stock
        int totalStock = selectedCostume.getTotalStock();
        costumeStockLabel.setText("Total Stock: " + totalStock);
        if (totalStock <= 2) {
            costumeStockLabel.setForeground(new Color(255, 69, 0));
        } else {
            costumeStockLabel.setForeground(new Color(0, 128, 0));
        }
        
        // Daily rate
        double dailyRate = selectedCostume.getPrice() * DAILY_RATE_MULTIPLIER;
        dailyRateLabel.setText("Daily Rate: $" + String.format("%.2f", dailyRate));
        
        // Setup size selection combo box
        setupSizeComboBox();
        
        // Load image
        loadCostumeImage();
    }
    
    private void setupSizeComboBox() {
        sizeComboBox.removeAllItems();
        
        // Add available sizes to combo box
        for (String size : selectedCostume.getAvailableSizes()) {
            if (selectedCostume.getStockForSize(size) > 0) {
                sizeComboBox.addItem(size);
            }
        }
        
        // Select first size
        if (sizeComboBox.getItemCount() > 0) {
            sizeComboBox.setSelectedIndex(0);
            updateSelectedSizeStock();
        }
    }

    private void updateSelectedSizeStock() {
        String selectedSize = (String) sizeComboBox.getSelectedItem();
        if (selectedSize != null) {
            int stock = selectedCostume.getStockForSize(selectedSize);
            selectedSizeStockLabel.setText(selectedSize + ": " + stock + " available");
            
            // Change color if stock is low
            if (stock <= 2) {
                selectedSizeStockLabel.setForeground(new Color(255, 69, 0));
            } else {
                selectedSizeStockLabel.setForeground(new Color(0, 100, 0));
            }
        }
    }
    
    private void loadCostumeImage() {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/" + selectedCostume.getImagePath()));
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                costumeImageLabel.setIcon(new ImageIcon(img));
                costumeImageLabel.setText("");
            } else {
                costumeImageLabel.setIcon(null);
                costumeImageLabel.setText("No Image");
            }
        } catch (Exception e) {
            costumeImageLabel.setIcon(null);
            costumeImageLabel.setText("No Image");
        }
    }
    
    /**
     * Adjust day spinner maximum value based on selected year and month
     */
    private void adjustDaySpinnerForMonth() {
        int year = (Integer) yearSpinner.getValue();
        int month = (Integer) monthSpinner.getValue();
        int currentDay = (Integer) daySpinner.getValue();
        
        // Get maximum days for the selected month
        LocalDate tempDate = LocalDate.of(year, month, 1);
        int maxDays = tempDate.lengthOfMonth();
        
        // Update day spinner model
        SpinnerNumberModel dayModel = new SpinnerNumberModel(
            Math.min(currentDay, maxDays), 1, maxDays, 1
        );
        daySpinner.setModel(dayModel);
    }
    
    /**
     * Get LocalDate from individual spinners
     */
    private LocalDate getSelectedDate() {
        int year = (Integer) yearSpinner.getValue();
        int month = (Integer) monthSpinner.getValue();
        int day = (Integer) daySpinner.getValue();
        return LocalDate.of(year, month, day);
    }
    
    private void updatePriceCalculation() {
        int days = (Integer) rentalDaysSpinner.getValue();
        
        // Get selected start date from individual spinners
        LocalDate startDate = getSelectedDate();
        LocalDate endDate = startDate.plusDays(days - 1);
        
        // Update date display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd (EEE)", java.util.Locale.ENGLISH);
        startDateLabel.setText(startDate.format(formatter));
        endDateLabel.setText(endDate.format(formatter));
        
        // Calculate price
        double dailyRate = selectedCostume.getPrice() * DAILY_RATE_MULTIPLIER;
        double totalCost = dailyRate * days;
        
        totalCostLabel.setText("$" + String.format("%.2f", totalCost) + " (" + days + " days)");
    }
    
    private void updateConfirmButtonState() {
        String selectedSize = (String) sizeComboBox.getSelectedItem();
        boolean hasStock = selectedSize != null && selectedCostume.getStockForSize(selectedSize) > 0;
        boolean canConfirm = agreeCheckBox.isSelected() && hasStock;
        confirmButton.setEnabled(canConfirm);
    }
    
    private void processRental() {
        if (!agreeCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this,
                "Please agree to the terms and conditions.",
                "Agreement Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedSize = (String) sizeComboBox.getSelectedItem();
        if (selectedSize == null) {
            // This case normally doesn't occur but just in case
            return;
        }

        try {
            // Calculate rental period
            int days = (Integer) rentalDaysSpinner.getValue();
            LocalDate startDate = getSelectedDate();
            LocalDate endDate = startDate.plusDays(days - 1);

            // Check stock availability for selected period
            boolean isAvailable = FileIO.getInstance().isStockAvailableForPeriod(selectedCostume.getCostumeId(), selectedSize, startDate, endDate);
            if (!isAvailable) {
                JOptionPane.showMessageDialog(this,
                    "The selected period includes dates with no stock available.\nPlease check the stock calendar and select a different period.",
                    "Stock Unavailable",
                    JOptionPane.ERROR_MESSAGE);
                return; // Stop processing due to no stock
            }

            double dailyRate = selectedCostume.getPrice() * DAILY_RATE_MULTIPLIER;
            double totalCost = dailyRate * days;
            
            // Create rental
            boolean success = rentalService.createRental(
                currentMemberId,
                selectedCostume.getCostumeId(),
                selectedSize,
                startDate,
                endDate,
                totalCost
            );
            
            if (success) {
                // Success message
                String message = String.format(
                    "Rental confirmed successfully!\n\n" +
                    "Costume: %s\n" +
                    "Period: %s to %s\n" +
                    "Total Cost: $%.2f\n\n" +
                    "Please pick up the costume on the start date.\n" +
                    "Thank you for using our service!",
                    selectedCostume.getCostumeName(),
                    startDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                    endDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                    totalCost
                );
                
                JOptionPane.showMessageDialog(this,
                    message,
                    "Rental Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dispose(); // Close window
                
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to process rental. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "An error occurred: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void setupFrame() {
        setTitle("Costume Rental - " + selectedCostume.getCostumeName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 700); // Increase height by 100px
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private String getTermsAndConditions() {
        return "RENTAL TERMS AND CONDITIONS\n\n" +
               "1. Rental Period: The costume must be returned by the specified return date.\n\n" +
               "2. Late Returns: A late fee of 10% of the daily rate will be charged for each day the costume is returned late.\n\n" +
               "3. Damage Policy: Any damage to the costume will result in repair charges or full replacement cost.\n\n" +
               "4. Cleaning: Costumes must be returned in clean condition. Additional cleaning fees may apply.\n\n" +
               "5. Cancellation: Rentals can be cancelled up to 24 hours before the start date for a full refund.\n\n" +
               "6. Pick-up: Costumes must be picked up on the rental start date during business hours.\n\n" +
               "7. Liability: The renter is responsible for the costume during the rental period.\n\n" +
               "8. Payment: Full payment is required at the time of rental confirmation.\n\n" +
               "By checking the agreement box, you acknowledge that you have read and agree to these terms.";
    }
}