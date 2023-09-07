package util;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ui.JSONTabManager;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileOpenService {
    public static void openJsonFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
            
            
            reader.close();

            // Validate JSON content
            if (!FileValidationService.isValidJSON(fileContent.toString())) {
                JOptionPane.showMessageDialog(null, "Invalid JSON content.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JSONTabManager.createJsonTab(file.getName(), fileContent.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
            // Handle file read error
        }
    }
}
