package ui.components;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class SearchPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel searchLabel;
    private JTextField searchTextField;
    private JCheckBox multiSearchCheckBox;
    private JTextArea textArea;

    public SearchPanel(JTextArea textArea) {
        // Initialize and arrange UI components
        this.textArea = textArea;
        searchLabel = new JLabel("Highlight:");
        searchTextField = new JTextField();
        multiSearchCheckBox = new JCheckBox("Multi-highlight");
        
        // Create a panel to hold the search label and text field
        JPanel searchInputPanel = new JPanel();
        searchInputPanel.setLayout(new BoxLayout(searchInputPanel, BoxLayout.X_AXIS));

        // Add padding to the label
        searchLabel.setBorder(new EmptyBorder(0, 8, 0, 0));

        // Set the maximum size of the label to its preferred size
        searchLabel.setMaximumSize(searchLabel.getPreferredSize());

        // Add the components to the search input panel
        searchInputPanel.add(searchLabel);
        searchInputPanel.add(searchTextField);
        searchInputPanel.add(Box.createHorizontalGlue());
        searchInputPanel.add(multiSearchCheckBox);

        // Add the search input panel to this panel
        setLayout(new BorderLayout());
        add(searchInputPanel, BorderLayout.NORTH);

        // Add a key listener to the search text field
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
                    highlightText(textArea, searchTextField.getText());
                }
            }
        });

        // Add an item listener to the "Multi-search" checkbox
        multiSearchCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    // Checkbox is unchecked
                    removeAllHighlights(textArea);
                    highlightText(textArea, searchTextField.getText());
                }
            }
        });
    }

    public void addSearchKeyListener(KeyListener listener) {
        searchTextField.addKeyListener(listener);
    }

    public void addMultiSearchItemListener(ItemListener listener) {
        multiSearchCheckBox.addItemListener(listener);
    }

    public String getSearchText() {
        return searchTextField.getText();
    }

    public boolean isMultiSearchSelected() {
        return multiSearchCheckBox.isSelected();
    }

    public void setSearchText(String searchText) {
        searchTextField.setText(searchText);
    }

    public void removeAllHighlights() {
        removeAllHighlights(textArea);
    }

    private void highlightText(JTextComponent textComponent, String textToHighlight) {
        try {
            Highlighter highlighter = textComponent.getHighlighter();
            Document doc = textComponent.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;

            while ((pos = text.indexOf(textToHighlight, pos)) >= 0) {
                highlighter.addHighlight(pos, pos + textToHighlight.length(), DefaultHighlighter.DefaultPainter);
                pos += textToHighlight.length();
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void removeAllHighlights(JTextComponent textComponent) {
        Highlighter highlighter = textComponent.getHighlighter();
        highlighter.removeAllHighlights();
    }
}
