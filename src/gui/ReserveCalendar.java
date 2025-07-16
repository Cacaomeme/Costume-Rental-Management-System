package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ReserveCalendar extends JDialog {

    private String costumeId;
    private String selectedSize;
    private YearMonth currentMonth;
    private final JLabel monthLabel;
    private final JPanel calendarPanel;
    private final Map<LocalDate, Integer> reservationCounts = new HashMap<>();
    private int maxStock = 0;
    private final FileIO fileIO; 

    public ReserveCalendar(Frame owner, String costumeId, String selectedSize) {
        super(owner, "StockCalendar", false); 
        this.costumeId = costumeId;
        this.selectedSize = selectedSize;
        this.currentMonth = YearMonth.now();
        this.fileIO = FileIO.getInstance(); 

        loadCostumeStock();
        loadReservations();

        setLayout(new BorderLayout());

        setAlwaysOnTop(true);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton prevButton = new JButton("<");
        prevButton.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendar();
        });

        JButton nextButton = new JButton(">");
        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendar();
        });

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        headerPanel.add(prevButton, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextButton, BorderLayout.EAST);

        calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : daysOfWeek) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            if (day.equals("Sun")) dayLabel.setForeground(Color.RED);
            if (day.equals("Sat")) dayLabel.setForeground(Color.BLUE);
            calendarPanel.add(dayLabel);
        }

        add(headerPanel, BorderLayout.NORTH);
        add(calendarPanel, BorderLayout.CENTER);

        updateCalendar();
    }

    /**
     *
     * @param newCostumeId 
     * @param newSelectedSize 
     */
    public void updateData(String newCostumeId, String newSelectedSize) {
        this.costumeId = newCostumeId;
        this.selectedSize = newSelectedSize;
        
        loadCostumeStock();
        loadReservations();
        

        updateCalendar();
    }

    private void loadCostumeStock() {
        this.maxStock = fileIO.getCostumeStock(this.costumeId, this.selectedSize);
    }

    private void loadReservations() {
        this.reservationCounts.clear();
        Map<LocalDate, Integer> newCounts = fileIO.getReservationCounts(this.costumeId, this.selectedSize);
        this.reservationCounts.putAll(newCounts);
    }

    private void updateCalendar() {
        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("M yyyy")));
        
        while (calendarPanel.getComponentCount() > 7) {
            calendarPanel.remove(7);
        }

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int dayOfWeekOfFirst = firstDayOfMonth.getDayOfWeek().getValue() % 7; 

        for (int i = 0; i < dayOfWeekOfFirst; i++) {
            calendarPanel.add(new JLabel(""));
        }

        int daysInMonth = currentMonth.lengthOfMonth();
        LocalDate today = LocalDate.now(); 

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = currentMonth.atDay(day);
            int reservedCount = reservationCounts.getOrDefault(currentDate, 0);
            int availableStock = maxStock - reservedCount;

            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.LEFT);
            dayLabel.setBorder(new EmptyBorder(2, 5, 2, 2));
            
            JLabel stockLabel = new JLabel(String.valueOf(availableStock), SwingConstants.CENTER);
            stockLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

            if (currentDate.isBefore(today)) {
                dayLabel.setForeground(Color.GRAY);
                stockLabel.setForeground(Color.GRAY);
                stockLabel.setText("-"); 
            } else {
                
                if (availableStock <= 0) {
                    stockLabel.setForeground(Color.RED);
                    stockLabel.setText("0");
                } else if (availableStock < 3) {
                    stockLabel.setForeground(new Color(255, 165, 0)); 
                } else {
                    stockLabel.setForeground(Color.BLUE);
                }
            }

            dayPanel.add(dayLabel, BorderLayout.NORTH);
            dayPanel.add(stockLabel, BorderLayout.CENTER);
            calendarPanel.add(dayPanel);
        }

        revalidate();
        repaint();
    }
}