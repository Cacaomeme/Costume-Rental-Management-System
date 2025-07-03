package gui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CostumeDataManager {

    
    private static final String RESOURCE_PATH = "/gui/costumes.csv";

    public List<Costume> loadCostumes() {
        List<Costume> costumeList = new ArrayList<>();

    
        InputStream is = this.getClass().getResourceAsStream(RESOURCE_PATH);

     
        if (is == null) {
            System.err.println("Costume data file not found in classpath: " + RESOURCE_PATH);
            return costumeList;
        }

       
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignore empty or commented lines
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 5) {
                    try {
                        // Parse basic data
                        String costumeId = data[0].trim();
                        String costumeName = data[1].trim();
                        CostumeEvent event = CostumeEvent.valueOf(data[2].trim());
                        double price = Double.parseDouble(data[3].trim());
                        
                        // Find the image path (last element)
                        String imagePath = data[data.length - 1].trim();
                        
                        // Create costume object
                        Costume costume = new Costume(costumeId, costumeName, event, price, imagePath);
                        
                        // Parse size:stock pairs (elements 4 to second-to-last)
                        for (int i = 4; i < data.length - 1; i++) {
                            String sizeStockPair = data[i].trim();
                            if (sizeStockPair.contains(":")) {
                                String[] pair = sizeStockPair.split(":");
                                if (pair.length == 2) {
                                    String size = pair[0].trim();
                                    int stock = Integer.parseInt(pair[1].trim());
                                    costume.addSizeStock(size, stock);
                                }
                            }
                        }
                        
                        costumeList.add(costume);

                    } catch (IllegalArgumentException e) {
                        System.err.println("Error parsing line: " + line + ". Invalid data format. " + e.getMessage());
                    }
                } else {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading costume data resource: " + e.getMessage());
            e.printStackTrace();
        }

        return costumeList;
    }
}