package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

import main.BundleViewerMain;
import persist.Constants;
import persist.PersistedOpenFileList;
import ui.TabManager;

public class FileOpenService {
	

	public static void openJsonFile(File file) {
		try {

			// focus on the tab that already has the file open if open:
			Integer addFile = PersistedOpenFileList.addFileToList(file);
			
			if (addFile != -1) {
				BundleViewerMain.tabbedPane.setSelectedIndex(addFile);
				return;
			}

			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder fileContent = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				fileContent.append(line).append("\n");
			}

			reader.close();

			// Validate JSON content
			if (!FileValidationService.isValidJSON(fileContent.toString())) {
				JOptionPane.showMessageDialog(null, Constants.ERROR_INVALID_JSON_CONTENT, Constants.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
				return;
			}

			TabManager.createJsonTab(file.getName(), fileContent.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
			// Handle file read error
		}
	}
}
