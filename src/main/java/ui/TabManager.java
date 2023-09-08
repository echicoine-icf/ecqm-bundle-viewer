package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
import javax.swing.border.EmptyBorder;
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
        JPanel searchPanel = new JPanel(new BorderLayout());
        JPanel searchInputPanel = new JPanel();
        searchInputPanel.setLayout(new BoxLayout(searchInputPanel, BoxLayout.X_AXIS));

        JTextField searchTextField = new JTextField();
        JTextArea textArea = new JTextArea(20, 60);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JCheckBox multiSearchCheckBox = new JCheckBox("Multi-highlight");

        JLabel searchLabel = new JLabel("Highlight:");
        searchLabel.setBorder(new EmptyBorder(0, 8, 0, 0));
        searchLabel.setMaximumSize(searchLabel.getPreferredSize());

        searchInputPanel.add(searchLabel);
        searchInputPanel.add(searchTextField);
        searchInputPanel.add(Box.createHorizontalGlue());
        searchInputPanel.add(multiSearchCheckBox);
        searchPanel.add(searchInputPanel, BorderLayout.NORTH);
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        searchTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	if (!multiSearchCheckBox.isSelected()) {
						removeAllHighlights(textArea);
					}
					// Highlight text
					if (searchTextField.getText() != null && searchTextField.getText().length() > 0) {
						// Checkbox is unchecked
						highlightText(textArea, searchTextField.getText());
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
						highlightText(textArea, searchTextField.getText());
					}
				}
			}
		});

     // Check if the top-level object has an "entry" array and "resourceType" is "Bundle"
        if (jsonObject.has(ENTRY) && jsonObject.has(RESOURCE_TYPE) && jsonObject.getString(RESOURCE_TYPE).equals(BUNDLE)) {
            JSONArray jsonArray = jsonObject.getJSONArray(ENTRY);
            DefaultListModel<String> resourceListModel = new DefaultListModel<>();
            resourceListModel.addElement(FULL_JSON_FILE);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject entryObject = jsonArray.getJSONObject(i);
                JSONObject resourceObject = entryObject.optJSONObject(RESOURCE);
                String title = (resourceObject != null && resourceObject.has(TITLE))
                        ? resourceObject.getString(TITLE)
                        : "Resource " + (i + 1);
                resourceListModel.addElement(title);
            }

            JList<String> resourceList = new JList<>(resourceListModel);
            resourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            resourceList.addListSelectionListener(e -> {
                int selectedIndex = resourceList.getSelectedIndex();
                if (selectedIndex == 0) {
                    textArea.setText(fileContent);
                } else {
                    JSONObject selectedResource = jsonArray.getJSONObject(selectedIndex - 1);
                    textArea.setText(selectedResource.toString(4));
                }
                textArea.setCaretPosition(0);
                searchTextField.setText("");
            });

            if (resourceListModel.getSize() > 0) {
                resourceList.setSelectedIndex(0);
            }

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(resourceList), mainPanel);
            splitPane.setResizeWeight(0.25);
            int listWidth = (int) (splitPane.getWidth() * 0.25);
            resourceList.setPreferredSize(new Dimension(listWidth, resourceList.getPreferredSize().height));
            BundleViewerMain.tabbedPane.addTab(fileName, splitPane);
            BundleViewerMain.tabbedPane.setSelectedIndex(BundleViewerMain.tabbedPane.getTabCount() - 1);

        } else {
            JOptionPane.showMessageDialog(frame, Constants.ERROR_INVALID_JSON, Constants.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void highlightText(JTextComponent textComponent, String textToHighlight) {
    	if (textToHighlight == null || !(textToHighlight.length() >= 2)) {
    		//bug exists where single characters hang the app up in highlighting. Minimum 2 characters needed.
    		return;
    	}
        try {
            Highlighter highlighter = textComponent.getHighlighter();
            Document doc = textComponent.getDocument();
            String text = doc.getText(0, doc.getLength());
            int startPos = 0;

            while (startPos < text.length()) {
                int pos = text.indexOf(textToHighlight, startPos);
                if (pos == -1) {
                    break;
                }
                highlighter.addHighlight(pos, pos + textToHighlight.length(), DefaultHighlighter.DefaultPainter);
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
