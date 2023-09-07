package ui;

import org.json.JSONArray;
import org.json.JSONObject;

import main.BundleViewerMain;
import persist.Constants;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JSONTabManager {
	private static final String FULL_JSON_FILE = "Full JSON File";
	private static final String TITLE = "title";
	private static final String RESOURCE = "resource";
	private static final String BUNDLE = "Bundle";
	private static final String RESOURCE_TYPE = "resourceType";
	private static final String ENTRY = "entry";

	public static void createJsonTab(String fileName, String fileContent) {
		JSONObject jsonObject = new JSONObject(fileContent);

		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(BundleViewerMain.tabbedPane);

		JTextArea textArea = new JTextArea(20, 60);
		textArea.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textArea);

		// Check if the top-level object has an "entry" array and "resourceType" is
		// "Bundle"
		if (jsonObject.has(ENTRY) && jsonObject.has(RESOURCE_TYPE)
				&& jsonObject.getString(RESOURCE_TYPE).equals(BUNDLE)) {
			JSONArray jsonArray = jsonObject.getJSONArray(ENTRY);
			DefaultListModel<String> resourceListModel = new DefaultListModel<>();

			// Add the top-level JSON as the "Full JSON File" entry
			resourceListModel.addElement(FULL_JSON_FILE);

			// Iterate through the "entry" array
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject entryObject = jsonArray.getJSONObject(i);
				if (entryObject.has(RESOURCE) && entryObject.getJSONObject(RESOURCE).has(TITLE)) {
					// Use the "title" field if it exists
					resourceListModel.addElement(entryObject.getJSONObject(RESOURCE).getString(TITLE));
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

			// Set the resize weight to allocate 25% of the width to the list
			splitPane.setResizeWeight(0.25);

			// Calculate the width of the list to be 25% of the total width of the split
			// pane
			int totalWidth = splitPane.getWidth();
			int listWidth = (int) (totalWidth * 0.25);

			// Set the preferred size of the list component
			resourceList.setPreferredSize(new Dimension(listWidth, resourceList.getPreferredSize().height));

			// Add the new tab with the split pane
			BundleViewerMain.tabbedPane.addTab(fileName, splitPane);

			// Set focus to the newly created tab
			BundleViewerMain.tabbedPane.setSelectedIndex(BundleViewerMain.tabbedPane.getTabCount() - 1);

		} else {
			JOptionPane.showMessageDialog(frame, Constants.ERROR_INVALID_JSON, Constants.ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
