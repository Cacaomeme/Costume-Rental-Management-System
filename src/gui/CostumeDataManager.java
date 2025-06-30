package gui;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CostumeDataManager {

    private static final String FILE_NAME = "gui/costumes.csv";


    public List<Costume> loadCostumes() {
        List<Costume> costumeList = new ArrayList<>();
        Path path = Paths.get(FILE_NAME);

        if (!Files.exists(path)) {
            System.err.println("Costume data file not found: " + FILE_NAME);
            return costumeList; // Return empty list if file doesn't exist
        }

        try {
            // Read all lines from the file
            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                // Ignore empty or commented lines
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length == 7) {
                    try {
                        // Parse data and create a Costume object
                        String costumeId = data[0].trim();
                        String costumeName = data[1].trim();
                        CostumeEvent event = CostumeEvent.valueOf(data[2].trim());
                        double price = Double.parseDouble(data[3].trim());
                        String size = data[4].trim();
                        int stock = Integer.parseInt(data[5].trim());
                        String imagePath = data[6].trim();

                        Costume costume = new Costume(costumeId, costumeName, event, price, size, stock, imagePath);
                        costumeList.add(costume);

                    } catch (IllegalArgumentException e) {
                        System.err.println("Error parsing line: " + line + ". Invalid data format. " + e.getMessage());
                    }
                } else {
                    System.err.println("Skipping malformed line: " + line);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading costume data file: " + e.getMessage());
            e.printStackTrace();
        }

        return costumeList;
    }
}