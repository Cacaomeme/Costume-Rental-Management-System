package gui;

/**
 * Represents a single costume item in the rental system.
 */
public class Costume {
    private String costumeId;       
    private String costumeName;     
    private CostumeEvent event;     
    private double price;           
    private String size;            // Size S, M, L
    private int stock;            
    private String imagePath;       //"images/costume01.jpg"


    public Costume(String costumeId, String costumeName, CostumeEvent event, double price, String size, int stock, String imagePath) {
        this.costumeId = costumeId;
        this.costumeName = costumeName;
        this.event = event;
        this.price = price;
        this.size = size;
        this.stock = stock;
        this.imagePath = imagePath;
    }


    public String getCostumeId() {
        return costumeId;
    }

    public void setCostumeId(String costumeId) {
        this.costumeId = costumeId;
    }

    public String getCostumeName() {
        return costumeName;
    }

    public void setCostumeName(String costumeName) {
        this.costumeName = costumeName;
    }

    public CostumeEvent getEvent() {
        return event;
    }

    public String getEventDisplayName() {
        return this.event.getDisplayName();
    }

    public void setEvent(CostumeEvent event) {
        this.event = event;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}