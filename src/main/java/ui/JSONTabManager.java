package ui;

import org.json.JSONArray;
import org.json.JSONObject;

import main.JSONFileViewer;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JSONTabManager {
	public static void createJsonTab(String fileName, String fileContent) {
		JSONObject jsonObject = new JSONObject(fileContent);

		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(JSONFileViewer.tabbedPane);

		JTextArea textArea = new JTextArea(20, 60);
		textArea.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textArea);

		// Check if the top-level object has an "entry" array and "resourceType" is
		// "Bundle"
		if (jsonObject.has("entry") && jsonObject.has("resourceType")
				&& jsonObject.getString("resourceType").equals("Bundle")) {
			JSONArray jsonArray = jsonObject.getJSONArray("entry");
			DefaultListModel<String> resourceListModel = new DefaultListModel<>();

			// Add the top-level JSON as the "Full JSON File" entry
			resourceListModel.addElement("Full JSON File");

			// Iterate through the "entry" array
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject entryObject = jsonArray.getJSONObject(i);
				if (entryObject.has("resource") && entryObject.getJSONObject("resource").has("title")) {
					// Use the "title" field if it exists
					resourceListModel.addElement(entryObject.getJSONObject("resource").getString("title"));
				} else {
					// Use a default title
					resourceListModel.addElement("Resource " + (i + 1));
				}
			}

			JList<String> resourceList = new JList<>(resourceListModel);
			resourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			// Add a ListSelectionListener to handle list item clicks
			resourceList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent event) {
					if (!event.getValueIsAdjusting()) {
						int selectedIndex = resourceList.getSelectedIndex();
						if (selectedIndex == 0) {
							// Display the full JSON file
							textArea.setText(fileContent);
						} else {
							// Display the JSON snippet for the selected resource
							if (jsonArray != null) {
								JSONObject selectedResource = jsonArray.getJSONObject(selectedIndex - 1);
								String snippet = selectedResource.toString(4); // 4-space indentation
								textArea.setText(snippet);
							}
						}
						textArea.setCaretPosition(0); // Scroll to the top
					}
				}
			});

			if (resourceList.getModel().getSize() > 0) {
				resourceList.setSelectedIndex(0);
			}

			// Create a split pane to display list and text
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(resourceList),
					scrollPane);
			splitPane.setResizeWeight(0.2);

			// Add the new tab with the split pane
			JSONFileViewer.tabbedPane.addTab(fileName, splitPane);

			// Set focus to the newly created tab
			JSONFileViewer.tabbedPane.setSelectedIndex(JSONFileViewer.tabbedPane.getTabCount() - 1);

		} else {
			JOptionPane.showMessageDialog(frame, "The selected file is not a valid Bundle JSON.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
