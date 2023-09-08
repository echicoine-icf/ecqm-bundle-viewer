package main;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class TextFieldsPaneExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Text Fields Pane Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            JPanel mainPanel = new JPanel(new BorderLayout());

            // Create the "Search:" label and text field
            JPanel searchPanel = new JPanel();
            searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS)); // Use BoxLayout

            JLabel searchLabel = new JLabel("Search:");
            JTextField searchTextField = new JTextField();
            
            // Add padding to the label
            searchLabel.setBorder(new EmptyBorder(0, 8, 0, 0)); // 8 pixels of padding on the left
            
            // Set the maximum size of the label to its preferred size
            searchLabel.setMaximumSize(searchLabel.getPreferredSize());
            
            // Add the label
            searchPanel.add(searchLabel);
            
            // Add horizontal glue to push the text field to the right
            searchPanel.add(Box.createHorizontalGlue());
            
            // Add the text field
            searchPanel.add(searchTextField);

            // Create the multiline text field
            JTextArea textArea = new JTextArea();
            JScrollPane scrollPane = new JScrollPane(textArea);

            // Add the components to the main panel
            mainPanel.add(searchPanel, BorderLayout.NORTH); // Place the search components at the top
            mainPanel.add(scrollPane, BorderLayout.CENTER); // Place the text area to fill the remaining space

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}
