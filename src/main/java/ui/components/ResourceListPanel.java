package ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResourceListPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JList<String> resourceList;

	public ResourceListPanel(JSONObject jsonObject, JTextArea textArea) {

		DefaultListModel<String> resourceListModel = new DefaultListModel<>();

		// Add the top-level JSON as the "Full JSON File" entry
		resourceListModel.addElement("Full JSON File");

		// Check if the top-level object has an "entry" array and "resourceType" is
		// "Bundle"
		if (jsonObject.has("entry") && jsonObject.has("resourceType")
				&& jsonObject.getString("resourceType").equals("Bundle")) {
			JSONArray jsonArray = jsonObject.getJSONArray("entry");

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
		}

		resourceList = new JList<>(resourceListModel);
		resourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add a ListSelectionListener to handle list item clicks
		resourceList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()) {
					int selectedIndex = resourceList.getSelectedIndex();
					if (selectedIndex == 0) {
						// Display the full JSON file
						textArea.setText(jsonObject.toString(4));
					} else {
						// Display the JSON snippet for the selected resource
						if (jsonObject.has("entry")) {
							JSONArray jsonArray = jsonObject.getJSONArray("entry");
							if (selectedIndex - 1 >= 0 && selectedIndex - 1 < jsonArray.length()) {
								JSONObject selectedResource = jsonArray.getJSONObject(selectedIndex - 1);
								String snippet = selectedResource.toString(4); // 4-space indentation
								textArea.setText(snippet);
							}
						}
					}
					textArea.setCaretPosition(0); // Scroll to the top
				}
			}
		});

		// Create a scroll pane for the resource list
		JScrollPane resourceScrollPane = new JScrollPane(resourceList);

		// Create a button to remove selected resource
		JButton removeButton = new JButton("Remove Resource");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = resourceList.getSelectedIndex();
				if (selectedIndex > 0) {
					// Remove the selected resource from the JSON object and update the display
					if (jsonObject.has("entry")) {
						JSONArray jsonArray = jsonObject.getJSONArray("entry");
						if (selectedIndex - 1 >= 0 && selectedIndex - 1 < jsonArray.length()) {
							jsonArray.remove(selectedIndex - 1);
							resourceListModel.remove(selectedIndex);
							textArea.setText(jsonObject.toString(4));
							resourceList.setSelectedIndex(0);
						}
					}
				}
			}
		});

		// Add components to the panel
		setLayout(new BorderLayout());
		add(resourceScrollPane, BorderLayout.CENTER);
		add(removeButton, BorderLayout.SOUTH);
	}
}
