package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import org.json.JSONArray;
import org.json.JSONObject;

import main.BundleViewerMain;
import persist.Constants;

public class TabManager {
	private static final String FULL_JSON_FILE = "Full JSON File";
	private static final String TITLE = "title";
	private static final String RESOURCE = "resource";
	private static final String BUNDLE = "Bundle";
	private static final String RESOURCE_TYPE = "resourceType";
	private static final String ENTRY = "entry";

	public static void createJsonTab(String fileName, String fileContent) {
		JSONObject jsonObject = new JSONObject(fileContent);

		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(BundleViewerMain.tabbedPane);

		JPanel mainPanel = new JPanel(new BorderLayout());

		// Create the "Search:" label and text field
		JPanel searchPanel = new JPanel(new BorderLayout());

		JPanel searchInputPanel = new JPanel();
		searchInputPanel.setLayout(new BoxLayout(searchInputPanel, BoxLayout.X_AXIS)); // Use BoxLayout

		JLabel searchLabel = new JLabel("Highlight:");
		JTextField searchTextField = new JTextField();

		// Create a multiline text field
		JTextArea textArea = new JTextArea(20, 60);
		JScrollPane scrollPane = new JScrollPane(textArea);

		// Create the "Multi-search" checkbox
		JCheckBox multiSearchCheckBox = new JCheckBox("Multi-highlight");

		// Add padding to the label
		searchLabel.setBorder(new EmptyBorder(0, 8, 0, 0)); // 8 pixels of padding on the left

		// Set the maximum size of the label to its preferred size
		searchLabel.setMaximumSize(searchLabel.getPreferredSize());

		// Add the components to the search input panel
		searchInputPanel.add(searchLabel);
		searchInputPanel.add(searchTextField);
		searchInputPanel.add(Box.createHorizontalGlue());
		searchInputPanel.add(multiSearchCheckBox);

		// Add the search input panel to the search panel
		searchPanel.add(searchInputPanel, BorderLayout.NORTH);

		// Add the search panel and text area to the main panel
		mainPanel.add(searchPanel, BorderLayout.NORTH); // Place the search components at the top
		mainPanel.add(scrollPane, BorderLayout.CENTER); // Place the text area to fill the remaining space

		searchTextField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (KeyEvent.getKeyText(keyCode).equalsIgnoreCase("Enter")) {
					if (!multiSearchCheckBox.isSelected()) {
						removeAllHighlights(textArea);
					}
					// Highlight text
					if (searchTextField.getText() != null && searchTextField.getText().length() > 0) {
						// Checkbox is unchecked
						invokeHighlight(textArea, searchTextField.getText());
					}
				}
			}
		});

		multiSearchCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() != ItemEvent.SELECTED) {
					removeAllHighlights(textArea);
					if (searchTextField.getText() != null && searchTextField.getText().length() > 0) {
						// Checkbox is unchecked
						invokeHighlight(textArea, searchTextField.getText());
					}
				}
			}
		});

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
						// reset highlighter
						searchTextField.setText("");
					}
				}
			});

			if (resourceList.getModel().getSize() > 0) {
				resourceList.setSelectedIndex(0);
			}

			// Create a split pane to display list and text
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(resourceList),
					mainPanel);

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

	public static void invokeHighlight(JTextComponent textComponent, String textToHighlight) {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				if (textToHighlight.length() > 1) {
					highlightText(textComponent, textToHighlight);
				}
				return null;
			}
		};
		worker.execute();
	}

	public static void highlightText(JTextComponent textComponent, String textToHighlight) {
		try {
			Highlighter highlighter = textComponent.getHighlighter();
			Document doc = textComponent.getDocument();
			String text = doc.getText(0, doc.getLength());
			int startPos = 0;

			while (startPos < text.length()) {
				int pos = text.indexOf(textToHighlight, startPos);
				if (pos == -1) {
					break; // No more occurrences found
				}

				// Scroll to the first occurrence of the highlighted text
				highlighter.addHighlight(pos, pos + textToHighlight.length(), DefaultHighlighter.DefaultPainter);

				// Move the starting position after the occurrence to avoid infinite looping
				startPos = pos + textToHighlight.length();
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public static void removeAllHighlights(JTextComponent textComponent) {
		Highlighter highlighter = textComponent.getHighlighter();
		highlighter.removeAllHighlights();
	}
}
