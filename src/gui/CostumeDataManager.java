package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CostumeDataManager {

    private static final String RESOURCE_PATH_STR = "gui/costumes.csv";
    private FileIO fileIO = FileIO.getInstance();

    public List<Costume> loadCostumes() {
        List<Costume> costumeList = new ArrayList<>();
        List<String> lines = fileIO.readAllLines(RESOURCE_PATH_STR);

        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] data = line.split(",");
            if (data.length >= 5) {
                try {
                    String costumeId = data[0].trim();
                    String costumeName = data[1].trim();
                    CostumeEvent event = CostumeEvent.valueOf(data[2].trim());
                    double price = Double.parseDouble(data[3].trim());
                    String imagePath = data[data.length - 1].trim();

                    Costume costume = new Costume(costumeId, costumeName, event, price, imagePath);

                    // Parse size:stock pairs (from 4th element to second to last)
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
        return costumeList;
    }

    private String costumeToCsvString(Costume costume) {
        String sizeStockStr = costume.getSizeStock().entrySet().stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.joining(","));
        
        return String.join(",",
            costume.getCostumeId(),
            costume.getCostumeName(),
            costume.getEvent().name(),
            String.valueOf(costume.getPrice()),
            sizeStockStr,
            costume.getImagePath()
        );
    }
    
    private boolean saveAllCostumes(List<Costume> costumes) {
        List<String> lines = new ArrayList<>();
        lines.add("# Costume ID, Costume Name, Event, Price, Size:Stock pairs (comma-separated), Image Path");
        for (Costume costume : costumes) {
            lines.add(costumeToCsvString(costume));
        }
        return fileIO.writeAllLines(RESOURCE_PATH_STR, lines);
    }

    public boolean addCostume(Costume newCostume) {
        List<Costume> costumes = loadCostumes();
        // Check for duplicate ID
        if (costumes.stream().anyMatch(c -> c.getCostumeId().equals(newCostume.getCostumeId()))) {
            return false;
        }
        costumes.add(newCostume);
        return saveAllCostumes(costumes);
    }

    public boolean updateCostume(Costume updatedCostume) {
        List<Costume> costumes = loadCostumes();
        for (int i = 0; i < costumes.size(); i++) {
            if (costumes.get(i).getCostumeId().equals(updatedCostume.getCostumeId())) {
                costumes.set(i, updatedCostume);
                return saveAllCostumes(costumes);
            }
        }
        return false; // Costume not found
    }

    public boolean deleteCostume(String costumeId) {
        List<Costume> costumes = loadCostumes();
        boolean removed = costumes.removeIf(c -> c.getCostumeId().equals(costumeId));
        if (removed) {
            return saveAllCostumes(costumes);
        }
        return false;
    }
}