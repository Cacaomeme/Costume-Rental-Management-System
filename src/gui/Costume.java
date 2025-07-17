package gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Costume {
    private String costumeId;       
    private String costumeName;     
    private CostumeEvent event;     
    private double price;           
    private Map<String, Integer> sizeStock; 
    private String imagePath;

    public Costume(String costumeId, String costumeName, CostumeEvent event, double price, String imagePath) {
        this.costumeId = costumeId;
        this.costumeName = costumeName;
        this.event = event;
        this.price = price;
        this.sizeStock = new HashMap<>(); 
        this.imagePath = imagePath;
    }

    // Constructor for backward compatibility
    public Costume(String costumeId, String costumeName, CostumeEvent event, double price, String size, int stock, String imagePath) {
        this(costumeId, costumeName, event, price, imagePath);
        this.sizeStock.put(size, stock); 
    }

    // Getters and Setters
    public String getCostumeId() { return costumeId; }
    public void setCostumeId(String costumeId) { this.costumeId = costumeId; }
    public String getCostumeName() { return costumeName; }
    public void setCostumeName(String costumeName) { this.costumeName = costumeName; }
    public CostumeEvent getEvent() { return event; }
    public String getEventDisplayName() { return this.event.getDisplayName(); }
    public void setEvent(CostumeEvent event) { this.event = event; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    // Methods for managing multiple sizes and stocks
    public Map<String, Integer> getSizeStock() { return sizeStock; }
    public void setSizeStock(Map<String, Integer> sizeStock) { this.sizeStock = sizeStock; }
    public void addSizeStock(String size, int stock) { this.sizeStock.put(size, stock); }
    public int getStockForSize(String size) { return sizeStock.getOrDefault(size, 0); }
    public Set<String> getAvailableSizes() { return sizeStock.keySet(); }
    public boolean hasSize(String size) { return sizeStock.containsKey(size) && sizeStock.get(size) > 0; }
    
    // Get total stock across all sizes
    public int getTotalStock() {
        return sizeStock.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Old methods for compatibility, now using the map
    public String getSize() {
        if (!sizeStock.isEmpty()) {
            return sizeStock.keySet().iterator().next(); // Returns the first size
        }
        return "";
    }
    public int getStock() {
        if (!sizeStock.isEmpty()) {
            return sizeStock.values().iterator().next(); // Returns the stock of the first size
        }
        return 0;
    }
}