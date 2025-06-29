package gui;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileIO {
    private Path registraterPath;

    public FileIO() {
        // Ensure the registrations file exists
        this.registraterPath = Paths.get("gui/Registrater.txt");
        try {
            if (!Files.exists(registraterPath)) {
                Files.createFile(registraterPath);
            }
        } catch (IOException e) {
            System.err.println("Error creating registrations file: " + e.getMessage());
        }
    }

    public void Write(String name, String memberId, String email, String phone, String password, String address) {
        try {
            List<String> lines = new ArrayList<>();
            lines.add(name + "," + memberId + "," + email + "," + phone + "," + password + "," + address);
            Files.write(registraterPath, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error writing to registrations file: " + e.getMessage());
        }
    }

    public boolean isMemberIdExists(String memberId) {
        try {
            List<String> lines = Files.readAllLines(registraterPath, Charset.defaultCharset());
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                if (parts.length > 1 && parts[1].equals(memberId)) {
                    return true; // Member ID exists
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading registrations file: " + e.getMessage());
        }
        return false; // Member ID does not exist
    }

    public boolean isValidLogin(String memberId, String password) {
        try {
            List<String> lines = Files.readAllLines(registraterPath, Charset.defaultCharset());
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                if (parts.length > 2 && parts[1].equals(memberId) && parts[4].equals(password)) {
                    return true; // Valid login
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading registrations file: " + e.getMessage());
        }
        return false; // Invalid login
    }
    
}
