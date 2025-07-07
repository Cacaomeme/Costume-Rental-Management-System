package gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

/**
 * Represents a single costume item in the rental system.
 */
public class Costume {
    private String costumeId;       
    private String costumeName;     
    private CostumeEvent event;     
    private double price;           
    private Map<String, Integer> sizeStock; // Size -> Stock mapping
    private String imagePath;       //"images/costume01.jpg"

    public Costume(String costumeId, String costumeName, CostumeEvent event, double price, String imagePath) {
        this.costumeId = costumeId;
        this.costumeName = costumeName;
        this.event = event;
        this.price = price;
        this.sizeStock = new HashMap<>();
        this.imagePath = imagePath;
    }

    // 従来のコンストラクタとの互換性のため
    public Costume(String costumeId, String costumeName, CostumeEvent event, double price, String size, int stock, String imagePath) {
        this(costumeId, costumeName, event, price, imagePath);
        this.sizeStock.put(size, stock);
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

    // 新しいメソッド群
    public void addSizeStock(String size, int stock) {
        this.sizeStock.put(size, stock);
    }

    public int getStockForSize(String size) {
        return sizeStock.getOrDefault(size, 0);
    }

    public void setStockForSize(String size, int stock) {
        this.sizeStock.put(size, stock);
    }

    public Set<String> getAvailableSizes() {
        return sizeStock.keySet();
    }

    public boolean hasSize(String size) {
        return sizeStock.containsKey(size) && sizeStock.get(size) > 0;
    }

    public boolean decreaseStock(String size) {
        if (hasSize(size)) {
            int currentStock = sizeStock.get(size);
            sizeStock.put(size, currentStock - 1);
            return true;
        }
        return false;
    }

    public void increaseStock(String size) {
        int currentStock = sizeStock.getOrDefault(size, 0);
        sizeStock.put(size, currentStock + 1);
    }

    // 従来のメソッドとの互換性のため（最初のサイズを返す）
    public String getSize() {
        if (!sizeStock.isEmpty()) {
            return sizeStock.keySet().iterator().next();
        }
        return "";
    }

    public void setSize(String size) {
        // 既存のサイズをクリアして新しいサイズを追加
        sizeStock.clear();
        sizeStock.put(size, 1);
    }

    // 従来のメソッドとの互換性のため（最初のサイズの在庫を返す）
    public int getStock() {
        if (!sizeStock.isEmpty()) {
            return sizeStock.values().iterator().next();
        }
        return 0;
    }

    public void setStock(int stock) {
        // 既存のサイズの在庫を更新
        if (!sizeStock.isEmpty()) {
            String firstSize = sizeStock.keySet().iterator().next();
            sizeStock.put(firstSize, stock);
        }
    }
    
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // 総在庫数を取得
    public int getTotalStock() {
        return sizeStock.values().stream().mapToInt(Integer::intValue).sum();
    }
}