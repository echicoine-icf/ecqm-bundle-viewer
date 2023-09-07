import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONFileViewer {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("eCQM Bundle Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Set the initial size to 720p (1280x720)
            frame.setSize(1280, 720);

            JTabbedPane tabbedPane = new JTabbedPane();

            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            JMenuItem openMenuItem = new JMenuItem("Open");

            openMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
                    fileChooser.setFileFilter(filter);

                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                            StringBuilder fileContent = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                fileContent.append(line).append("\n");
                            }
                            reader.close();

                            // Validate JSON content
                            if (!isValidJSON(fileContent.toString())) {
                                JOptionPane.showMessageDialog(null, "Invalid JSON content.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            JTextArea textArea = new JTextArea(20, 60);
                            textArea.setEditable(false);
                            textArea.setText(fileContent.toString());

                            JScrollPane scrollPane = new JScrollPane(textArea);

                            // Parse the JSON content as a JSONObject
                            JSONObject jsonObject = new JSONObject(fileContent.toString());

                            // Check if the top-level object has an "entry" array and "resourceType" is "Bundle"
                            if (jsonObject.has("entry") && jsonObject.has("resourceType") && jsonObject.getString("resourceType").equals("Bundle")) {
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
                                                textArea.setText(fileContent.toString());
                                            } else {
                                                // Display the JSON snippet for the selected resource
                                                if (jsonObject.has("entry")) {
                                                    JSONArray jsonArray = jsonObject.getJSONArray("entry");
                                                    JSONObject selectedResource = jsonArray.getJSONObject(selectedIndex - 1);
                                                    String snippet = selectedResource.toString(4); // 4-space indentation
                                                    textArea.setText(snippet);
                                                }
                                            }
                                            textArea.setCaretPosition(0); // Scroll to the top
                                        }
                                    }
                                });

                                // Create a split pane to display list and text
                                JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(resourceList), scrollPane);
                                splitPane.setResizeWeight(0.2);

                                tabbedPane.addTab(fileChooser.getSelectedFile().getName(), splitPane);
                            } else {
                                JOptionPane.showMessageDialog(null, "The selected file is not a valid Bundle JSON.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            // Handle file read error
                        }
                    }
                }
            });

            fileMenu.add(openMenuItem);
            menuBar.add(fileMenu);

            frame.setJMenuBar(menuBar);
            frame.add(tabbedPane, BorderLayout.CENTER);

            frame.setLocationRelativeTo(null); // Center the window
            frame.setVisible(true);
        });
    }

    // Validate JSON content using JSONTokener
    private static boolean isValidJSON(String jsonContent) {
        try {
            new JSONTokener(jsonContent).nextValue();
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}
