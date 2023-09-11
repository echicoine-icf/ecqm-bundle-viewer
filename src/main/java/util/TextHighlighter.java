package util;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.text.*;

/**
 * @author echic
 *
 */
public class TextHighlighter {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Text Highlight Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            JTextArea textArea = new JTextArea();
            frame.add(new JScrollPane(textArea));

            JButton highlightButton = new JButton("Highlight Text");
            highlightButton.addActionListener(e -> {
                highlightText(textArea, "highlighted");
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(highlightButton);
            frame.add(buttonPanel, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }

    public static void highlightText(JTextComponent textComponent, String textToHighlight) {
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
}